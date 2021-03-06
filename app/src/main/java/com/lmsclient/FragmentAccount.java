package com.lmsclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class FragmentAccount extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v=inflater.inflate(R.layout.fragment_account, container, false);
        ((TextView)v.findViewById(R.id.uname)).setText(MainActivity.name);
        ((TextView)v.findViewById(R.id.erp_id)).setText(MainActivity.reference.getKey());
        MainActivity.reference.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    try {
                        ((TextView)v.findViewById(R.id.prog)).setText(dataSnapshot.child("Program").getValue(String.class));
                        ((TextView)v.findViewById(R.id.contact)).setText(dataSnapshot.child("Contact").getValue(String.class));
                        ((TextView)v.findViewById(R.id.email)).setText(dataSnapshot.child("Email").getValue(String.class));
                    }
                    catch(Exception ignored) {}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Account");
        super.onViewCreated(view, savedInstanceState);
    }
}
