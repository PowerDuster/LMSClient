package com.lmsclient;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Mahad Ahmed on 1/3/18.
 */

public class FragmentResources extends Fragment {
    Course course;
    static ArrayAdapter<String> filesAdapter;
    static ArrayList<String> fileList;
    ChildEventListener listener=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            filesAdapter.add(dataSnapshot.getValue(String.class));
        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        course=MainActivity.courseList.get(FragmentFullCourseView.position);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        View v=inflater.inflate(R.layout.fragment_resources, container, false);
        ListView listView=v.findViewById(R.id.file_list);
        fileList=new ArrayList<>();
        filesAdapter=new ArrayAdapter<String>(getContext(), R.layout.list_view_item, fileList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.list_view_item, parent, false);
                }
                String tmp[]=fileList.get(position).split("/");
                if(tmp.length>0) {
                    ((TextView)convertView.findViewById(R.id.item_head)).setText(tmp[tmp.length-1]);
                }
                ((TextView)convertView.findViewById(R.id.item_text)).setText(fileList.get(position));
                return convertView;
            }
        };
        listView.setAdapter(filesAdapter);

        course.courseReference.child("Files").addChildEventListener(listener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                String tmp=fileList.get(i);
                StorageReference storageRef=FirebaseStorage.getInstance().getReferenceFromUrl(tmp);
                final String arr[]=tmp.split("/");
                try {
                    if(arr.length<1) {
                        return;
                    }
                    final File file=new File(Environment.getExternalStorageDirectory()+File.separator+arr[arr.length-1]);
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), arr[arr.length-1]+" downloaded", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file),getMimeType(file.getAbsolutePath()));
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ;
//        storageRef
        return v;
    }

    @Override
    public void onPause() {
        course.courseReference.child("Files").removeEventListener(listener);
        filesAdapter.clear();
        fileList.clear();
        super.onPause();
    }

    private String getMimeType(String url) {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
