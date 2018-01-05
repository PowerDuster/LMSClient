package com.lmsclient;

/**
 * Created by Mahad Ahmed on 12/30/17.
 */

class Chat {
    String otherUser;
    String name;
    String lastMessage;

    Chat(String user, String n, String m) {
        otherUser=user;
        name=n;
        lastMessage=m;
    }
}