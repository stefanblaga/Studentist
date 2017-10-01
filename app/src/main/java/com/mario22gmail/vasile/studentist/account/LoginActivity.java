package com.mario22gmail.vasile.studentist.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.StartActivity;
import com.mario22gmail.vasile.studentist.student.StudentRequestListActivity;

import java.util.Arrays;

import Helpers.Constants;
import Helpers.FacebookApiLogic;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.start_activity_logo_textView)
    TextView logoTitle;

    @BindView(R.id.cityNameTextView)
    TextView cityNameTextView;

    private int signUpId = 111;

    private int _accoutTypeChoosed = 0;

    private FacebookApiLogic facebookApiLogic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/mainfont.ttf");
        logoTitle.setTypeface(custom_font);
        cityNameTextView.setTypeface(custom_font);
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
                String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                userTable.getRef().orderByChild("uid").startAt(uuid).endAt(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Intent createProfileActivity = new Intent(getApplicationContext(), CreateProfileActivity.class);
                            startActivity(createProfileActivity);
                            finish();
                        } else {
                            UserApp user = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserApp.class);
                            if(user == null)
                            {
                                Constants.ShowErrorFragment(getSupportFragmentManager());
                                return;
                            }
                            String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            GetTheRightActivity(user.role, uuid);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void GetTheRightActivity(String role, String uid) {
        if (role == null || role.equals("")) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
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
            default:
                Constants.ShowErrorFragment(getSupportFragmentManager());
                return;
        }
    }

}
