package com.lmsclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mahad Ahmed on 1/3/18.
 */

public class FragmentFullCourseView extends Fragment {
    public static int position;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        position=getArguments().getInt("position");
        View v=inflater.inflate(R.layout.fragment_fullcourse, container, false);
        ((ViewPager)v.findViewById(R.id.course_pager)).setAdapter(new CoursePagerAdapter(getChildFragmentManager()));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(MainActivity.courseList.get(position).name);
    }
}

class CoursePagerAdapter extends FragmentPagerAdapter {
    CoursePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return new FragmentOverview();
        }
        else if(position==1) {
            return new FragmentResources();
        }
        else {
            return new FragmentAssignments();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0) {
            return "Overview";
        }
        else if(position==1) {
            return "Resources";
        }
        else {
            return "Assignments";
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}