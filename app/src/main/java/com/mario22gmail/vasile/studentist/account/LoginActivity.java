package com.mario22gmail.vasile.studentist.account;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.MainNavigationActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.patient.PatientMainFragment;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.student.StudentMainFragment;

import java.util.Arrays;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.start_activity_logo_textView)
    TextView logoTitle;

    private int signUpId = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/mainfont.ttf");
        logoTitle.setTypeface(custom_font);
        StartFireBaseUI();

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
            if (resultCode == Activity.RESULT_OK) {
                DatabaseReference userTable = FirebaseLogic.getInstance().GetUserTableReference();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Constants.ShowErrorFragment(getSupportFragmentManager());
                    return;
                }
                final String uuid = currentUser.getUid();
                userTable.getRef().orderByChild("uid").startAt(uuid).endAt(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Intent createAccountActivity = new Intent(getApplicationContext(), CreateAccountActivity.class);
                            startActivity(createAccountActivity);
                            finish();
                            return;
                        } else {
                            UserApp user = dataSnapshot.child(uuid).getValue(UserApp.class);
                            if (user == null) {
                                Constants.ShowErrorFragment(getSupportFragmentManager());
                                return;
                            }
                            //set user for session
                            FirebaseLogic.SetUserApp(user);
                            GetTheRightActivity(user.role, uuid);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Constants.ShowErrorFragment(getSupportFragmentManager());
                    }
                });
            }
        } else {
            Constants.ShowErrorFragment(getSupportFragmentManager());
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
        Intent navActivity = new Intent(getApplicationContext(), MainNavigationActivity.class);
        switch (role) {
            case "student":
                navActivity.putExtra("uid", uid);
                navActivity.putExtra(Constants.UserTypeKey, role);
                startActivity(navActivity);
                break;
            case "patient":
                navActivity.putExtra("uid", uid);
                navActivity.putExtra(Constants.UserTypeKey, role);
                startActivity(navActivity);
                break;
            default:
                Constants.ShowErrorFragment(getSupportFragmentManager());
        }
    }

}
