package com.lmsclient;

import java.util.ArrayList;

/**
 * Created by Mahad Ahmed on 12/30/17.
 */

public class Chat {
    String otherUser;
    String name;
    ArrayList<String> messages;

    public Chat(String user, String n, String msg) {
        otherUser=user;
        name=n;
        messages=new ArrayList<>();
        messages.add(msg);
    }
}
