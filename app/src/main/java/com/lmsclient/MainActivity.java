package com.lmsclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayAdapter<Course> courseAdapter;
    ArrayList<Course> courseList =new ArrayList<>();
    SparseArray<Course> courseMap=new SparseArray<>();

    public static ArrayAdapter<Chat> chatAdapter;
    ArrayList<Chat> chatList=new ArrayList<>();
    HashMap<String, Chat> chatHashMap=new HashMap<>();

    public static DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        courseAdapter=new ArrayAdapter<Course>(this, R.layout.course_view, courseList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.course_view, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.course_name)).setText(courseList.get(position).name);
                ((TextView)convertView.findViewById(R.id.course_time)).setText(courseList.get(position).sessions);
                ((TextView)convertView.findViewById(R.id.course_ins)).setText(courseList.get(position).instructor);
                return convertView;
            }
        };

        reference.child("Courses").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                StringBuilder sessions=new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.child("Sessions").getChildren()) {
                    sessions.append(snapshot.child("Day").getValue(String.class)).append(" ")
                            .append(snapshot.child("Start").getValue(String.class)).append(" - ")
                            .append(snapshot.child("End").getValue(String.class)).append('\n');
                }
                sessions.deleteCharAt(sessions.length()-1);
                Course tmp=new Course(dataSnapshot.child("Name").getValue(String.class), dataSnapshot.child("Instructor").getValue(String.class), sessions.toString());
                courseMap.put(Integer.parseInt(dataSnapshot.getKey()), tmp);
                courseAdapter.add(tmp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                courseAdapter.remove(courseMap.get(Integer.parseInt(dataSnapshot.getKey())));
                courseMap.remove(Integer.parseInt(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        chatAdapter=new ArrayAdapter<Chat>(this, R.layout.chat_view, chatList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.chat_view, parent, false);
                }
                Chat chat=chatList.get(position);
                ((TextView)convertView.findViewById(R.id.chatlist_userid)).setText(chat.otherUser);
//                ((TextView)convertView.findViewById(R.id.chatlist_last)).setText(chat.messages.get(chat.messages.size()-1));
                ((TextView)convertView.findViewById(R.id.chatlist_last)).setText(chat.messages);
                return convertView;
            }
        };

        reference.child("Chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat tmp=new Chat(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));
                chatHashMap.put(dataSnapshot.getKey(), tmp);
                chatAdapter.add(tmp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                chatHashMap.get(dataSnapshot.getKey()).messages=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                chatAdapter.remove(chatHashMap.get(dataSnapshot.getKey()));
                chatHashMap.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        reference.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((TextView)findViewById(R.id.nav_nameview)).setText(dataSnapshot.getValue(String.class));
                ((TextView)findViewById(R.id.nav_idview)).setText(reference.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_courses) {
            Fragment fragment=new FragmentCourse();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
        else if(id == R.id.nav_chat) {
            Fragment fragment=new FragmentChat();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
        else if(id == R.id.nav_signout) {
            LoginActivity.prefs.edit().remove("u_pwd").apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }
}
