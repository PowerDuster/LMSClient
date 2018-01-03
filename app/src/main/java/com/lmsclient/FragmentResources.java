package com.lmsclient;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        course=MainActivity.courseList.get(FragmentFullCourseView.position);
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
                ((TextView)convertView.findViewById(R.id.item_head)).setText("");
                ((TextView)convertView.findViewById(R.id.item_text)).setText(fileList.get(position));
                return convertView;
            }
        };
        listView.setAdapter(filesAdapter);

        course.courseReference.child("Files").addChildEventListener(new ChildEventListener() {
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
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
}
