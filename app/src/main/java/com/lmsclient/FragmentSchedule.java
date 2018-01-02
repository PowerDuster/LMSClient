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
import android.widget.CalendarView;

/**
 * Created by Mahad Ahmed on 1/1/18.
 */

public class FragmentSchedule extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_schedule, container, false);
        ((ViewPager)v.findViewById(R.id.pager)).setAdapter(new SchedulePagerAdapter(getChildFragmentManager()));
//        ((CalendarView)v.findViewById(R.id.calendar)).set
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Schedule");
    }
}

class SchedulePagerAdapter extends FragmentPagerAdapter {
    SchedulePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
//        if(position==0) {
//            return new FragmentChat();
//        }
        return new AssignmentListFragment();
//        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0) {
            return "Assignments";
        }
        else if(position==1) {
            return "Classes";
        }
        else {
            return "Quizes";
        }
//        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 3;
    }
}