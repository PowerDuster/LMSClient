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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static DatabaseReference reference;

    public static ArrayAdapter<Course> courseAdapter;
    ArrayList<Course> courseList =new ArrayList<>();
    SparseArray<Course> courseMap=new SparseArray<>();

    public static ArrayAdapter<Chat> chatAdapter;
    public static ArrayList<Chat> chatList=new ArrayList<>();
    HashMap<String, Chat> chatHashMap=new HashMap<>();

    public static ArrayAdapter<String[]> assignmentListAdapter;
    ArrayList<String[]> assignmentList=new ArrayList<>();

    public static ArrayAdapter<String[]> classesAdapter;
    ArrayList<String[]> classesList=new ArrayList<>();

    public static AdapterView.OnItemClickListener itemClickListener;



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

        classesAdapter=new ArrayAdapter<String[]>(this, R.layout.list_view_item, classesList) {
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
                                classesAdapter.add(new String[] {"Class at "+tTmp+" on "+dTmp});
                            }
                            sessions.deleteCharAt(sessions.length() - 1);
                            Course tmp = new Course(dataSnap.child("Name").getValue(String.class), dataSnap.child("Instructor").getValue(String.class), sessions.toString());
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


        chatAdapter=new ArrayAdapter<Chat>(this, R.layout.chat_view, chatList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView==null) {
                    convertView=getLayoutInflater().inflate(R.layout.chat_view, parent, false);
                }
                Chat chat=chatList.get(position);
                ((TextView)convertView.findViewById(R.id.chatlist_userid)).setText(chat.name);
//                ((TextView)convertView.findViewById(R.id.chatlist_last)).setText(chat.messages.get(chat.messages.size()-1));
                ((TextView)convertView.findViewById(R.id.chatlist_last)).setText(chat.messages.get(chat.messages.size()-1));
                return convertView;
            }
        };
        reference.child("Chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final String key=dataSnapshot.getKey();
                reference.getParent().child(dataSnapshot.getKey()).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot data) {
                        Chat tmp;
                        if(data.exists()) {
                            tmp = new Chat(key, data.getValue(String.class), "");
                        }
                        else {
                            tmp = new Chat(key, key, "");
                        }
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            tmp.messages.add(snapshot.getValue(String.class));
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

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chatHashMap.get(dataSnapshot.getKey()).messages.add(snapshot.getValue(String.class));
                    FragmentChatbox.adapter.add(snapshot.getValue(String.class));
                }
                chatAdapter.notifyDataSetChanged();
                FragmentChatbox.adapter.notifyDataSetChanged();
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
        assignmentListAdapter.add(new String[] {"Due at 11:55pm", "Consumer Behaviour"});


        itemClickListener=new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment=new FragmentChatbox();
                Bundle bundle=new Bundle();
                bundle.putInt("position", i);
                fragment.setArguments(bundle);
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack("");
                transaction.commit();
            }
        };

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
        else if(id == R.id.nav_schedule) {
            Fragment fragment=new FragmentSchedule();
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
        else if(id == R.id.nav_account) {
//            StorageReference
            reference.child("Chat").child("07409").push().setValue("Woah hello"+Math.random());
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
