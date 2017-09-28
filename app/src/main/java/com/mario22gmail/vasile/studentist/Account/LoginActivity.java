package com.mario22gmail.vasile.studentist.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.auth.provider.GoogleProvider;
import com.firebase.ui.auth.provider.Provider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUseStudent;
import com.mario22gmail.vasile.studentist.Patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.StartActivity;
import com.mario22gmail.vasile.studentist.Student.StudentRequestListActivity;

import java.util.Arrays;
import java.util.List;

import Helpers.Constants;
import Helpers.FacebookApiLogic;
import Helpers.FirebaseLogic;
import Helpers.UserApp;

public class LoginActivity extends AppCompatActivity {


    private int signUpId = 111;

    private int _accoutTypeChoosed = 0;

    private FacebookApiLogic facebookApiLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        facebookApiLogic = new FacebookApiLogic();
        StartFireBaseUI();

    }

    public void StudentOptionClick(View view) {
        _accoutTypeChoosed = 1;
//        StartFireBaseUI();
    }

    public void PatientOptionClick(View view) {
        _accoutTypeChoosed = 2;
//        StartFireBaseUI();
    }


    private void StartFireBaseUI() {
        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build();
//                setPermissions(Arrays.asList("email", "user_birthday", "user_about_me")).build();

        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build();


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.loginbackgroundimage)
                        .setTheme(R.style.GreenTheme).setAvailableProviders(Arrays.asList(facebookIdp, googleIdp)).build(),
                signUpId);
    }


    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == signUpId) {
            if (resultCode == ResultCodes.OK) {
                DatabaseReference userTable = FirebaseLogic.getInstance().GetUserTableReference();
                ValueEventListener tableValueEvent = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Intent createProfileActivity = new Intent(getApplicationContext(), CreateProfileActivity.class);
                            startActivity(createProfileActivity);
                            finish();
                        } else {
                            UserApp user = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserApp.class);
                            String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            GetTheRightActivity(user.role, uuid);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                userTable.getRef().orderByChild("uid").startAt(uuid).endAt(uuid).addListenerForSingleValueEvent(tableValueEvent);

            }
        }
    }


    public void GetTheRightActivity(String role, String uid) {
        if (role == null) {
            Intent mainActivity = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(mainActivity);
            finish();
        }
        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_HOW_TO, MODE_PRIVATE);
        switch (role) {
            case "student":
                boolean showHowToStudentPage = sp.getBoolean(Constants.DISPLAY_HOW_TO_STUDENT, true);
                if (showHowToStudentPage) {
                    Intent howToStudentActivity = new Intent(getApplicationContext(), HowToUseStudent.class);
                    startActivity(howToStudentActivity);
                    finish();
                    return;
                }
                Intent studentActivity = new Intent(getApplicationContext(), StudentRequestListActivity.class);
                studentActivity.putExtra("uid", uid);
                startActivity(studentActivity);
                finish();
                break;
            case "patient":
                boolean showHowToPage = sp.getBoolean(Constants.DISPLAY_HOW_TO_PATIENT, true);
                if (showHowToPage) {
                    Intent howToPatientActivity = new Intent(getApplicationContext(), HowToUsePatientActivity.class);
                    startActivity(howToPatientActivity);
                    finish();
                    return;
                }
                Intent patientActivitity = new Intent(getApplicationContext(), PatientShowRequestListActivity.class);
                patientActivitity.putExtra("uid", uid);
                startActivity(patientActivitity);
                finish();
                break;
        }
    }

}
