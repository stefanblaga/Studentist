package com.mario22gmail.vasile.studentist.patient;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.SharedPreferenceLogic;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientRequestDetails extends AppCompatActivity {

    @BindView(R.id.patientRequestDetailToolbar)
    public Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_request_details);

        ButterKnife.bind(this);


        toolbar.setTitle(R.string.detailRequestToolbarLabel);
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

//        guideline.setp
        String  requestUUID ="";
        Bundle bundleFromListView = getIntent().getExtras();
        if (bundleFromListView.getString(Constants.requestUuidIntentExtraName) != null) {
            requestUUID = bundleFromListView.getString(Constants.requestUuidIntentExtraName);
        }

        if(requestUUID != null && requestUUID.equals(""))
        {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();

        Bundle bundleForFragment = new Bundle();
        bundleForFragment.putString(Constants.requestUuidIntentExtraName,requestUUID);
        StudentDetailFragment studentFragment = new StudentDetailFragment();
        studentFragment.setArguments(bundleForFragment);

        fragmentManager.beginTransaction().add(R.id.patientRequestStudentAppliedFragment, studentFragment).commit();

        if(SharedPreferenceLogic.IsPatientViewDetailsFirstTime(getApplicationContext()))
        {
            Intent celebrationActivity = new Intent(getApplicationContext(), PatientCelebrationActivity.class);
            startActivity(celebrationActivity);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }




}
