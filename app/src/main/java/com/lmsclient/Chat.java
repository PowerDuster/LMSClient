package com.lmsclient;

import java.util.ArrayList;

/**
 * Created by Mahad Ahmed on 12/30/17.
 */

public class Chat {
    String otherUser;
    String messages;

    public Chat(String user, String msg) {
//        messages=new ArrayList<>();
        otherUser=user;
        messages=msg;
    }
}
