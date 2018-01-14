package com.lmsclient;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

/**
 * Created by Mahad Ahmed on 1/4/18.
 */

public class FragmentAssignments extends Fragment {
    static ArrayAdapter<String[]> assignmentsAdapter;
    static ArrayList<String[]> assignmentsList;
    Course course;
    ChildEventListener listener=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            try {
                assignmentsAdapter.add(new String[] {"Due at "+dataSnapshot.child("Due").getValue(String.class),
                        dataSnapshot.child("Title").getValue(String.class)});
            }
            catch(Exception ignored) {}
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
        View v=inflater.inflate(R.layout.fragment_assign_list, container, false);
        assignmentsList=new ArrayList<>();
        assignmentsAdapter=new ArrayAdapter<String[]>(getContext(), R.layout.list_view_item, assignmentsList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.list_view_item, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.item_head)).setText(assignmentsList.get(position)[0]);
                ((TextView)convertView.findViewById(R.id.item_text)).setText(assignmentsList.get(position)[1]);
                return convertView;
            }
        };
        ListView listView=v.findViewById(R.id.assign_list);
        listView.setAdapter(assignmentsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                // To search for all documents available via installed storage providers,
                // it would be "*/*".
//                intent.setType("image/*");

                startActivityForResult(intent, 42);
            }
        });
        course.courseReference.child("Assignments").addChildEventListener(listener);
        return v;
    }

    @Override
    public void onPause() {
        course.courseReference.child("Assignments").removeEventListener(listener);
        assignmentsAdapter.clear();
        assignmentsList.clear();
        super.onPause();
    }
}
