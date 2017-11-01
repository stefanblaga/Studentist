package com.mario22gmail.vasile.studentist.account;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mario22gmail.vasile.studentist.MainNavigationActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.matthewtamlin.sliding_intro_screen_library.buttons.IntroButton;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;

public class CreateAccountActivity extends AppIntro2 implements ICreateAccount {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create_account);

        ChooseAccountTypeFragment chooseAccountTypeFragment = new ChooseAccountTypeFragment();
        addSlide(chooseAccountTypeFragment);

        ChooseCityFragment chooseCityFragment = new ChooseCityFragment();
        addSlide(chooseCityFragment);
        showSkipButton(false);
        setProgressButtonEnabled(false);

    }


    @Override
    public void onSlideChanged(@Nullable android.support.v4.app.Fragment oldFragment, @Nullable android.support.v4.app.Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    private String userRole = "";

    @Override
    public void GoToCitySlide(String userRole) {
        this.userRole = userRole;
        super.pager.goToNextSlide();
        //todo enable swipe lock
//        setGoBackLock(true);
//        setSwipeLock(true);
    }

    @Override
    public void CreateAccount(String city) {
        if (userRole.equals("")) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        if (city.equals("")) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        if (!(userRole.equals(Constants.StudentUserType) || userRole.equals(Constants.PatientUserType))) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }


        final UserApp user = new UserApp();
        user.uid = currentUser.getUid();
        user.appVersion = Constants.APP_VERSION;
        user.deviceToken = FirebaseInstanceId.getInstance().getToken();
        user.role = userRole;
        user.city = city;

        DatabaseReference usersTable = FirebaseLogic.getInstance().GetUserTableReference();
        usersTable.child(user.uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Constants.ShowErrorFragment(getSupportFragmentManager());
                    return;
                }

                //set user for current session
                FirebaseLogic.SetUserApp(user);

                if (user.role.equals(Constants.PatientUserType)) {
                    Intent patientActivitity = new Intent(getApplicationContext(), MainNavigationActivity.class);
                    startActivity(patientActivitity);
                    finish();
                    return;
                } else if (user.role.equals(Constants.StudentUserType)) {
                    Intent finishStudentProfile = new Intent(getApplicationContext(), CreateProfileActivity.class);
                    startActivity(finishStudentProfile);
                    finish();
                    return;
                }

                Constants.ShowErrorFragment(getSupportFragmentManager());
            }


        });
    }


}


