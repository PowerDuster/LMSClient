package com.lmsclient;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Mahad Ahmed on 1/3/18.
 */

public class FragmentOverview extends Fragment {
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_overview, container, false);
        ((TextView)v.findViewById(R.id.course_code)).setText(MainActivity.courseList.get(FragmentFullCourseView.position).id);
        ((TextView)v.findViewById(R.id.course_head)).setText(MainActivity.courseList.get(FragmentFullCourseView.position).name);
        ((TextView)v.findViewById(R.id.creds)).setText(MainActivity.courseList.get(FragmentFullCourseView.position).credits+" Credits");
        ((TextView)v.findViewById(R.id.course_details)).setText(MainActivity.courseList.get(FragmentFullCourseView.position).overview);
        ((TextView)v.findViewById(R.id.course_pre)).setText(MainActivity.courseList.get(FragmentFullCourseView.position).prereqs);
        return v;
    }
}
