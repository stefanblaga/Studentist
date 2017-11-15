package com.mario22gmail.vasile.studentist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mario22gmail.vasile.studentist.account.CreateAccountActivity;
import com.mario22gmail.vasile.studentist.account.CreateProfileActivity;
import com.mario22gmail.vasile.studentist.account.LoginActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseIntro;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.SharedPreferenceLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.start_activity_logo_textView)
    TextView logoTitle;
    @BindView(R.id.start_activity_layout)
    View mainLayout;

    private boolean _isConnectedToInternet = false;
    final private Handler handler = new Handler();
    private boolean _checkingInternetConnection = true;
    Snackbar snackBarInternetConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_Memorium, MODE_PRIVATE);
        boolean showMemoriumPage = sp.getBoolean(Constants.DISPLAY_MemoriumBool, true);
        if (showMemoriumPage) {
            Intent memoriumPage = new Intent(getApplicationContext(), MemoriumActivity.class);
            startActivity(memoriumPage);
            finish();
            return;
        }

        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        String notConnectedText = getResources().getString(R.string.internet_fail_connected_text);
        snackBarInternetConnection = Snackbar.make(mainLayout, notConnectedText, Snackbar.LENGTH_INDEFINITE);
        snackBarInternetConnection.setAction("Activate", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
                startActivity(intent);
            }
        });

        //set title
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/mainfont.ttf");
        logoTitle.setTypeface(custom_font);

        if (SharedPreferenceLogic.ShowHowToPagePatient(getApplicationContext())) {
            SharedPreferenceLogic.SetHowToPagePatient(getApplicationContext(), false);
            Intent showHowToPage = new Intent(getApplicationContext(), HowToUseIntro.class);
            startActivity(showHowToPage);
            finish();
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Constants.LogKey, "entered on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(Constants.LogKey, "entered on destroy");
        handler.removeCallbacks(_networkRunnable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LogKey, "entered on destroy");
        handler.removeCallbacks(_networkRunnable);
    }

    final private Runnable _networkRunnable = new Runnable() {
        @Override
        public void run() {
            if (_checkingInternetConnection) {
                _isConnectedToInternet = isNetworkAvailable();
                DisplayViewBasedOnNetworkState(_isConnectedToInternet);
                if (_isConnectedToInternet) {
                    handler.removeCallbacks(this);
                }
            }
            handler.postDelayed(this, 500);
        }
    };

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void DisplayViewBasedOnNetworkState(boolean isConnected) {
        if (isConnected) {
            if (_checkingInternetConnection) {
                snackBarInternetConnection.dismiss();
                _checkingInternetConnection = false;
                GetUserFromFirebase();
            }

        } else {
            snackBarInternetConnection.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkAvailable()) {
            _checkingInternetConnection = true;
            _isConnectedToInternet = false;
            handler.postDelayed(_networkRunnable, 500);
        } else {
            handler.removeCallbacks(_networkRunnable);
            GetUserFromFirebase();
        }


    }

    private void GetUserFromFirebase() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                    return;
                }
            }, 500);
            return;
        }

        final String userUid = auth.getCurrentUser().getUid();
        DatabaseReference userTableRef = FirebaseLogic.getInstance().GetUserTableReference();
        userTableRef.getRef().orderByChild("uid").startAt(userUid).endAt(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent createUserActivity = new Intent(getApplicationContext(), CreateAccountActivity.class);
                    startActivity(createUserActivity);
                    finish();
                } else {
                    UserApp user = dataSnapshot.child(userUid).getValue(UserApp.class);
                    if (user == null) {
                        Constants.ShowErrorFragment(getSupportFragmentManager());
                        return;
                    }
                    if (user.appVersion == null || !user.appVersion.equals(Constants.APP_VERSION)) {
                        dataSnapshot.child(userUid).child("appVersion").getRef().setValue(Constants.APP_VERSION);
                    }

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    if (user.deviceToken != null && !user.deviceToken.equals(deviceToken)) {
                        dataSnapshot.child(userUid).child("deviceToken").getRef().setValue(deviceToken);
                    }

                    FirebaseLogic.SetUserApp(user);
                    StartRightActivity(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Constants.ShowErrorFragment(getSupportFragmentManager());
            }
        });
    }

    private void StartRightActivity(UserApp user) {
        Intent navActivity = new Intent(getApplicationContext(), MainNavigationActivity.class);
        switch (user.role) {
            case "patient":
                navActivity.putExtra("uid", user.uid);
                navActivity.putExtra(Constants.UserTypeKey, user.role);
                startActivity(navActivity);
                finish();
                break;
            case "student":
                if (user.telephoneNumber == null || user.telephoneNumber.equals("") ||
                        user.name == null || user.name.equals("")) {
                    Intent finishStudentProfile = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    startActivity(finishStudentProfile);
                    finish();
                    return;
                }
                navActivity.putExtra("uid", user.uid);
                navActivity.putExtra(Constants.UserTypeKey, user.role);
                startActivity(navActivity);
                finish();
                break;
            default:
                Constants.ShowErrorFragment(getSupportFragmentManager());
        }
    }
}
