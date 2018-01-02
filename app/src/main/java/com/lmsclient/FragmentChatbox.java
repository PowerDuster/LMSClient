package com.lmsclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mahad Ahmed on 12/31/17.
 */

public class FragmentChatbox extends Fragment {
    ArrayList<String> messageList=new ArrayList<>();
    public static ArrayAdapter<String> adapter;
    Chat chat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_chatbox, container, false);
        chat=MainActivity.chatList.get(getArguments().getInt("position"));
        adapter=new ArrayAdapter<String>(getContext(), R.layout.messages_view, messageList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.messages_view, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.message_view)).setText(messageList.get(position));
                return convertView;
            }
        };
        ((ListView)v.findViewById(R.id.message_list)).setAdapter(adapter);
        for(int i=0;i<chat.messages.size();i++) {
            adapter.add(chat.messages.get(i));
        }
        adapter.notifyDataSetChanged();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(chat!=null) {
            getActivity().setTitle(chat.name);
        }
    }
}
