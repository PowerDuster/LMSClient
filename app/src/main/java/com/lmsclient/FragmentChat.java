package com.lmsclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class FragmentChat extends Fragment {
    static AdapterView.OnItemClickListener itemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Fragment fragment=new FragmentChatbox();
            Bundle bundle=new Bundle();
            bundle.putInt("position", i);
            fragment.setArguments(bundle);
            FragmentTransaction transaction=MainActivity.fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("");
            transaction.commit();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chat, container, false);
        ListView lv=v.findViewById(R.id.chat_list);
        lv.setAdapter(MainActivity.chatAdapter);
        lv.setOnItemClickListener(itemClickListener);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Chat");
    }
}
