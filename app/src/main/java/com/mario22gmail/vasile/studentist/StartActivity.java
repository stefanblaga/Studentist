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
import com.mario22gmail.vasile.studentist.Account.CreateProfileActivity;
import com.mario22gmail.vasile.studentist.Account.LoginActivity;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUseStudent;
import com.mario22gmail.vasile.studentist.Patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.Student.StudentRequestListActivity;

import org.w3c.dom.Text;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.start_activity_logo_textView)
    TextView logoTitle;
    @BindView(R.id.start_activity_layout)
    View mainLayout;

    @BindView(R.id.cityNameTextView)
    TextView cityNameTextView;

    private boolean _isConnectedToInternet = false;
    final private Handler handler = new Handler();
    private boolean _checkingInternetConnection = true;
    Snackbar snackbarInternetConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        snackbarInternetConnection = Snackbar.make(mainLayout, "Verifica conexiunea la internet", Snackbar.LENGTH_INDEFINITE);
        snackbarInternetConnection.setAction("Activate", new View.OnClickListener() {
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
        logoTitle.setText("Studentist");

        cityNameTextView.setTypeface(custom_font);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Constants.LogKey,"entered on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(Constants.LogKey,"entered on destroy");
        handler.removeCallbacks(_networkRunnable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.LogKey,"entered on destroy");
        handler.removeCallbacks(_networkRunnable);


    }

    final private Runnable _networkRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(Constants.LogKey, "Thread is running");
            if (_checkingInternetConnection) {
                _isConnectedToInternet = isNetworkAvailable();
                DisplayViewBasedOnNetworkState(_isConnectedToInternet);
                if(_isConnectedToInternet)
                {
                    Log.i(Constants.LogKey, "Runnable removed");
                    handler.removeCallbacks(this);
                }
                Log.i(Constants.LogKey, "Runnable checked internet");
            }
            handler.postDelayed(this, 2000);
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
                snackbarInternetConnection.dismiss();
                _checkingInternetConnection = false;
                GetUserFromFirebase();
            }

        } else {
            snackbarInternetConnection.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.LogKey, "On resume entered");
        if(!isNetworkAvailable()) {
            _checkingInternetConnection = true;
            _isConnectedToInternet = false;
            handler.postDelayed(_networkRunnable, 2000);
        }else
        {
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
        DatabaseReference userTableRef = FirebaseLogic.getInstance().GetUserTableReference();
        userTableRef.getRef().orderByChild("uid").startAt(userUid).endAt(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Intent createUserActivity = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    startActivity(createUserActivity);
                    finish();
                } else {
                    UserApp user = dataSnapshot.child(userUid).getValue(UserApp.class);
                    if(user.appVersion == null || !user.appVersion.equals(Constants.APP_VERSION))
                    {
                        user.appVersion = Constants.APP_VERSION;
                        dataSnapshot.child(userUid).getRef().setValue(user);
                    }

                    StartRightActivity(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void StartRightActivity(UserApp user) {
        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_HOW_TO, MODE_PRIVATE);
        switch (user.role) {
            case "patient":
                boolean showHowToPage = sp.getBoolean(Constants.DISPLAY_HOW_TO_PATIENT,true);
                if(showHowToPage)
                {
                    Intent howToPatientActivity= new Intent(getApplicationContext(), HowToUsePatientActivity.class);
                    startActivity(howToPatientActivity);
                    finish();
                    return;
                }
                Intent patientActivitity = new Intent(getApplicationContext(), PatientShowRequestListActivity.class);
                patientActivitity.putExtra("uid", user.uid);
                startActivity(patientActivitity);
                //// TODO: 30/06/2017 pune finish
                break;
            case "student":
                boolean showHowToStudentPage = sp.getBoolean(Constants.DISPLAY_HOW_TO_STUDENT,true);
                if(showHowToStudentPage)
                {
                    Intent howToStudentActivity= new Intent(getApplicationContext(), HowToUseStudent.class);
                    startActivity(howToStudentActivity);
                    finish();
                    return;
                }
                Intent studentActivity = new Intent(getApplicationContext(), StudentRequestListActivity.class);
                studentActivity.putExtra("uid", user.uid);
                startActivity(studentActivity);
                //// TODO: 30/06/2017 pune finish
                break;
        }
    }
}
