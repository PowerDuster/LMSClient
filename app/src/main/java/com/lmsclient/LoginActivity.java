package com.lmsclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    public static SharedPreferences prefs;
    private EditText idView;
    private EditText passwordView;
    private ScrollView formView;
    private View mProgressView;
    private DatabaseReference reference;
    private String id;
    private String pwd;
    private Handler handler=new Handler();
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                try {
                    if(dataSnapshot.getValue(String.class).equals(pwd)) {
                        prefs.edit().putString("u_id", id).apply();
                        prefs.edit().putString("u_pwd", pwd).apply();
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        MainActivity.reference=reference.getParent();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }
                catch(Exception ignored) {}
            }
            else {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), "Failed Bad", Toast.LENGTH_LONG).show();
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            reference.removeEventListener(listener);
            showProgress(false);
            handler.removeCallbacks(runnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        formView = findViewById(R.id.login_form);
        idView = findViewById(R.id.userid);
        passwordView = findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button button = findViewById(R.id.button);
        mProgressView = findViewById(R.id.login_progress);

        prefs=getPreferences(0);

        id=prefs.getString("u_id", null);
        if(id!=null) {
            idView.setText(id);
        }
        pwd=prefs.getString("u_pwd", null);
        if(pwd!=null) {
            passwordView.setText(pwd);
        }
        if(pwd!=null&&id!=null) {
            attemptLogin();
        }

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        findViewById(R.id.forgot).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });

    }

    private void attemptLogin() {
        idView.setError(null);
        passwordView.setError(null);

        id = idView.getText().toString();
        pwd = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(pwd)) {
            passwordView.setError("Required");
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(id)) {
            idView.setError("Required");
            focusView = idView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            reference = FirebaseDatabase.getInstance().getReference(id).child("pwd");
            reference.addListenerForSingleValueEvent(listener);
            handler.postDelayed(runnable, 15000);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

