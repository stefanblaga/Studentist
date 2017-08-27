package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import Helpers.FacebookApiLogic;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.ButterKnife;

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
        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).
                setPermissions(Arrays.asList("email", "user_birthday", "user_about_me")).build();

        AuthUI.IdpConfig googleIdp =  new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build();


        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.drawable.inceput)
                        .setTheme(R.style.GreenTheme)
                        .setProviders(Arrays.asList(googleIdp, facebookIdp )).build(),
                        signUpId);
    }


    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == signUpId) {
            if (resultCode == ResultCodes.OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String provider = user.getProviderId();

                DatabaseReference userTable = FirebaseLogic.getInstance().GetUserTableReference();
                ValueEventListener tableValueEvent = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists())
                        {
                            Intent createaProfileActivity = new Intent(getApplicationContext(), CreateProfileActivity.class);
                            startActivity(createaProfileActivity);
                            finish();
                        }
                        else
                        {
                            UserApp user = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(UserApp.class);
                            String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            GetTheRightActivity(user.role,uuid);
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


    public void GetTheRightActivity(String role, String uid)
    {
        if(role == null)
        {
            Intent mainActivity = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(mainActivity);
            finish();
        }

        switch (role)
        {
            case "student":
                Intent studentActivity = new Intent(getApplicationContext(), StudentMainActivity.class);
                studentActivity.putExtra("uid", uid);
                startActivity(studentActivity);
                finish();
                break;
            case "patient":
                Intent patientActivitity = new Intent(getApplicationContext(), PatientFirstActivity.class);
                patientActivitity.putExtra("uid", uid);
                startActivity(patientActivitity);
                finish();
                break;
        }
    }

}
