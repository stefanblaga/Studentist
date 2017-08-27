package com.mario22gmail.vasile.studentist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.start_activity_logo_textView)
    TextView logoTitle;
    @BindView(R.id.start_activity_layout)
    View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        //set title
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/mainfont.ttf");
        logoTitle.setTypeface(custom_font);
        logoTitle.setText("Studentist");


    }

    @Override
    protected void onResume() {
        super.onResume();

        //internet is available
        if (isNetworkAvailable() == false) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Check Internet connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Activate", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
                    startActivity(intent);
                }
            });
            snackbar.show();
        } else {

            final FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() == null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginActivity);
                    }
                }, 1000);
                return;
            }

            Log.i("MMM", "UserApp autentificat cu numarul " + auth.getCurrentUser().getProviderId());
            Log.i("MMM", "UserApp autentificat cu uid " + auth.getCurrentUser().getUid());
            Log.i("MMM", "UserApp autentificat cu email " + auth.getCurrentUser().getEmail());
            Log.i("MMM", "UserApp autentificat cu display name " + auth.getCurrentUser().getDisplayName());
//                auth.getCurrentUser().getUid()
//                auth.signOut();
//                LoginManager.getInstance().logOut();
            final String userUid = auth.getCurrentUser().getUid();


            ValueEventListener userEventListner = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Intent createUserActivity = new Intent(getApplicationContext(), CreateProfileActivity.class);
                        startActivity(createUserActivity);
                        finish();
                    } else {
                        UserApp user = dataSnapshot.child(userUid).getValue(UserApp.class);
                        StartRightActivity(user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            DatabaseReference userTableRef = FirebaseLogic.getInstance().GetUserTableReference();
            userTableRef.getRef().orderByChild("uid").startAt(userUid).endAt(userUid).addListenerForSingleValueEvent(userEventListner);
        }

    }

    private void StartRightActivity(UserApp user) {
        switch (user.role) {
            case "patient":
                Intent patientActivitity = new Intent(getApplicationContext(), PatientFirstActivity.class);
                patientActivitity.putExtra("uid", user.uid);
                startActivity(patientActivitity);
                //// TODO: 30/06/2017 pute finish
                break;
            case "student":
                Intent studentActivity = new Intent(getApplicationContext(), StudentMainActivity.class);
                studentActivity.putExtra("uid", user.uid);
                startActivity(studentActivity);
                //// TODO: 30/06/2017 pune finish
                break;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
