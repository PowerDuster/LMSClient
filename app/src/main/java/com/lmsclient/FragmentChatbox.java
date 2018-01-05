package com.lmsclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FragmentChatbox extends Fragment {
    ArrayList<mMessage> messageList=new ArrayList<>();
    public static ArrayAdapter<mMessage> adapter;
    ChildEventListener listener=new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Integer sendr=dataSnapshot.child("Sender").getValue(Integer.class);
            if(sendr==null) {
                return;
            }
            adapter.add(new mMessage(sendr, dataSnapshot.child("msg").getValue(String.class),
                    dataSnapshot.child("time").getValue(String.class)));
            adapter.notifyDataSetChanged();
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
    View.OnClickListener buttonListener;
    Chat chat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chat=MainActivity.chatList.get(getArguments().getInt("position"));
        final View v=inflater.inflate(R.layout.fragment_chatbox, container, false);
        adapter=new ArrayAdapter<mMessage>(getContext(), R.layout.messages_view, messageList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.messages_view, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.message_text)).setText(messageList.get(position).message);
                ((TextView)convertView.findViewById(R.id.message_time)).setText(messageList.get(position).time);
//                TextView textView=convertView.findViewById(R.id.message_text);
                if(messageList.get(position).sender==1) {
                    ((TextView)convertView.findViewById(R.id.message_sender)).setText(chat.name);
//                    textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                }
                else {
                    ((TextView)convertView.findViewById(R.id.message_sender)).setText("Me");
                }
//                textView.setText(messageList.get(position).message);
                return convertView;
            }
        };
        ((ListView)v.findViewById(R.id.message_list)).setAdapter(adapter);
        MainActivity.reference.child("Chat").child(chat.otherUser).addChildEventListener(listener);
        buttonListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText=v.findViewById(R.id.msg_box);
                String str=editText.getText().toString();
                if(TextUtils.isEmpty(str)) {
                    return;
                }
                String time=new SimpleDateFormat("E dd-MM-yyyy hh:mma", Locale.getDefault()).format(new Date());
                Map<String, Object> map = new HashMap<>(3);
                map.put("Sender", 0);
                map.put("msg", str);
                map.put("time", time);
                MainActivity.reference.child("Chat").child(chat.otherUser).push().updateChildren(map);
                Map<String, Object> map2 = new HashMap<>(3);// Not modifying the original map because updateChildren is asynchronous and it might change sender value before updates are sent
                map2.put("Sender", 1);
                map2.put("msg", str);
                map2.put("time", time);
                MainActivity.reference.getParent().child(chat.otherUser).child("Chat").child(MainActivity.erp).push().updateChildren(map2);
                editText.setText("");
            }
        };
        v.findViewById(R.id.send_btn).setOnClickListener(buttonListener);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(chat!=null) {
            getActivity().setTitle(chat.name);
        }
    }

    @Override
    public void onPause() {
        MainActivity.reference.child("Chat").child(chat.otherUser).removeEventListener(listener);
        adapter.clear();
        messageList.clear();
        super.onPause();
    }
}

class mMessage {
    int sender;
    String message;
    String time;
    mMessage(int s, String m, String t) {
        sender=s;
        message=m;
        time=t;
    }
}