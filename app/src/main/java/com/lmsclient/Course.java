package com.lmsclient;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Mahad Ahmed on 12/26/2017.
 */

public class Course {
    String name;
    String id;
    String overview;
    String prereqs;
    DatabaseReference courseReference;
    String instructor;
    String sessions;
    int credits;
    Course(String name, String ID, String over, String pre, String ins, String s, int creds) {
        this.name=name;
        id=ID;
        courseReference=MainActivity.reference.getParent().child("Courses").child(id);
        overview=over;
        prereqs=pre;
        this.instructor=ins;
        this.sessions=s;
        credits=creds;
    }
}
