package com.lmsclient;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
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
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static DatabaseReference reference;
    public static String name="";
    public static String erp="";

    public static ArrayAdapter<Course> courseAdapter;
    public static ArrayList<Course> courseList =new ArrayList<>();
    SparseArray<Course> courseMap=new SparseArray<>();

    public static ArrayAdapter<Chat> chatAdapter;
    public static ArrayList<Chat> chatList=new ArrayList<>();
    HashMap<String, Chat> chatHashMap=new HashMap<>();

    public static ArrayAdapter<String[]> assignmentListAdapter;
    ArrayList<String[]> assignmentList=new ArrayList<>();

    public static ArrayAdapter<String[]> classesAdapter;
    ArrayList<String[]> classesList=new ArrayList<>();

    public static ArrayAdapter<String> notifChatAdapter;
    public static ArrayList<String> notifChatList=new ArrayList<>();

    public static ArrayAdapter<String> notifResourceAdapter;
    public static ArrayList<String> notifResourceList=new ArrayList<>();

    public static FragmentManager fragmentManager;

    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        fragmentManager=getSupportFragmentManager();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        classesAdapter=new ArrayAdapter<String[]>(this, R.layout.list_view_item, classesList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.list_view_item, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.item_head)).setText(classesList.get(position)[0]);
                ((TextView)convertView.findViewById(R.id.item_text)).setText(classesList.get(position)[1]);
                return convertView;
            }
        };

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

        assignmentListAdapter=new ArrayAdapter<String[]>(this, R.layout.list_view_item, assignmentList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.list_view_item, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.item_head)).setText(assignmentList.get(position)[0]);
                ((TextView)convertView.findViewById(R.id.item_text)).setText(assignmentList.get(position)[1]);
                return convertView;
            }
        };

        notifResourceAdapter=new ArrayAdapter<String>(this, R.layout.notification_view, notifResourceList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.notification_view, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.notif_text)).setText(notifResourceList.get(position));
                return super.getView(position, convertView, parent);
            }
        };

        reference.child("Courses").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                reference.getParent().child("Courses").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnap) {
                        try {
                            StringBuilder sessions = new StringBuilder();
                            for (DataSnapshot snapshot : dataSnap.child("Sessions").getChildren()) {
                                String dTmp=snapshot.child("Day").getValue(String.class);
                                String tTmp=snapshot.child("Start").getValue(String.class);
                                sessions.append(dTmp).append(" ")
                                        .append(tTmp).append(" - ")
                                        .append(snapshot.child("End").getValue(String.class)).append('\n');
                                classesAdapter.add(new String[] {"Class at "+tTmp+" on "+dTmp, dataSnap.child("Name").getValue(String.class)});
                            }
                            StringBuilder pres = new StringBuilder();
                            for (DataSnapshot snapshot : dataSnap.child("prereqs").getChildren()) {
                                pres.append(snapshot.getValue(String.class));
                                pres.append("\n");
                            }
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "res");
                            builder.setSmallIcon(R.drawable.common_full_open_on_phone)
                                    .setAutoCancel(true).setVibrate(new long[]{500, 200, 30, 100});
                            for (DataSnapshot snapshot : dataSnap.child("Assignments").getChildren()) {
                                assignmentListAdapter.add(new String[] {"Due at "+snapshot.child("Due").getValue(String.class),
                                    snapshot.child("Title").getValue(String.class)+" for "+dataSnap.child("Name").getValue(String.class)});
                                builder.setContentTitle(snapshot.child("Title").getValue(String.class)).setContentText("Due at "+snapshot.child("Due").getValue(String.class));
                                notificationManager.notify(1, builder.build());
                            }
                            ///////////////////////////////////////////////
                            if(dataSnap.child("Files").getChildrenCount()>0) {
                                notifResourceAdapter.add("New resource uploaded for "+dataSnap.child("Name").getValue(String.class));
                            }
                            ///////////////////////////////////////////////
                            sessions.deleteCharAt(sessions.length() - 1);
                            Integer i=dataSnap.child("Creds").getValue(Integer.class);
                            if(i==null) {
                                i=3;
                            }
                            Course tmp = new Course(dataSnap.child("Name").getValue(String.class), dataSnapshot.getKey(), dataSnap.child("Overview").getValue(String.class), pres.toString(), dataSnap.child("Instructor").getValue(String.class), sessions.toString(), i);
                            courseMap.put(Integer.parseInt(dataSnap.getKey()), tmp);
                            courseAdapter.add(tmp);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

        notifChatAdapter=new ArrayAdapter<String>(this, R.layout.notification_view, notifChatList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.notification_view, parent, false);
                }
                ((TextView)convertView.findViewById(R.id.notif_text)).setText(notifChatList.get(position));
                return super.getView(position, convertView, parent);
            }
        };

        chatAdapter=new ArrayAdapter<Chat>(this, R.layout.chat_view, chatList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.chat_view, parent, false);
                }
                Chat chat=chatList.get(position);
                ((TextView)convertView.findViewById(R.id.chatlist_userid)).setText(chat.name);
                ((TextView)convertView.findViewById(R.id.chatlist_last)).setText(chat.lastMessage);
                return convertView;
            }
        };
        reference.child("Chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                try {
                    final String key = dataSnapshot.getKey();
                    reference.getParent().child(dataSnapshot.getKey()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot data) {
                            Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                            for(int c=0;c<dataSnapshot.getChildrenCount()-1;c++) {
                                iterator.next();
                            }
                            Chat tmp;
                            if (data.exists()) {
                                tmp = new Chat(key, data.getValue(String.class), iterator.next().child("msg").getValue(String.class));
                            }
                            else {
                                tmp = new Chat(key, key, iterator.next().child("msg").getValue(String.class));
                            }
                            chatHashMap.put(key, tmp);
                            chatAdapter.add(tmp);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //
                        }
                    });
                }
                catch(Exception ignored) {}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                    for(int c=0;c<dataSnapshot.getChildrenCount()-1;c++) {
                        iterator.next();
                    }
                    Chat tmp=chatHashMap.get(dataSnapshot.getKey());
                    tmp.lastMessage=iterator.next().child("msg").getValue(String.class);
                    chatAdapter.notifyDataSetChanged();
                    notifChatAdapter.add("New message from "+tmp.name);
                    notifChatAdapter.notifyDataSetChanged();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "res");
                    builder.setSmallIcon(R.drawable.common_full_open_on_phone).setContentTitle(tmp.name).setContentText(tmp.lastMessage)
                            .setAutoCancel(true).setVibrate(new long[]{500, 200, 30, 100});
                    notificationManager.notify(2, builder.build());
                }
                catch(Exception ignored) {}
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

        erp=reference.getKey();
        reference.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    name=dataSnapshot.getValue(String.class);
                    ((TextView)findViewById(R.id.nav_nameview)).setText(name);
                }
                ((TextView)findViewById(R.id.nav_idview)).setText(erp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Fragment fragment=new FragmentHome();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment, "home");
        transaction.commit();
    }






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            FragmentHome home=(FragmentHome)fragmentManager.findFragmentByTag("home");
            if(home!=null && home.isVisible()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Fragment fragment=new FragmentHome();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment, "home");
            transaction.commit();
        }
        else if (id == R.id.nav_courses) {
            Fragment fragment=new FragmentCourse();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("");
            transaction.commit();
        }
        else if(id == R.id.nav_chat) {
            Fragment fragment=new FragmentChat();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("");
            transaction.commit();
        }
        else if(id == R.id.nav_schedule) {
            Fragment fragment=new FragmentSchedule();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("");
            transaction.commit();
        }
        else if(id == R.id.nav_account) {
            Fragment fragment=new FragmentAccount();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack("");
            transaction.commit();
        }
        else if(id == R.id.nav_signout) {
            LoginActivity.prefs.edit().remove("u_pwd").apply();
            startActivity(new Intent(this, LoginActivity.class));
//            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
            finish();
        }
        ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }
}
