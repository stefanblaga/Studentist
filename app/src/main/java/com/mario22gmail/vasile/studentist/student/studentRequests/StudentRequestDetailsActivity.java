package com.mario22gmail.vasile.studentist.student.studentRequests;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mario22gmail.vasile.studentist.patient.PatientRequestDetailFragment;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentRequestDetailsActivity extends AppCompatActivity {

    @BindView(R.id.studentRequestPatientDetailsToolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_request_details);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.detailRequestForStudentToolbarLabel);
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //        guideline.setp
        String requestUUID = "";
        Bundle bundleFromListView = getIntent().getExtras();
        if (bundleFromListView.getString(Constants.requestUuidIntentExtraName) != null) {
            requestUUID = bundleFromListView.getString(Constants.requestUuidIntentExtraName);
        }

        if (requestUUID.equals("")) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundleForFragment = new Bundle();
        bundleForFragment.putString(Constants.requestUuidIntentExtraName, requestUUID);

        PatientRequestDetailFragment requestDetailFragment = new PatientRequestDetailFragment();
        requestDetailFragment.setArguments(bundleForFragment);

        fragmentManager.beginTransaction().add(R.id.patient_request_details_for_student, requestDetailFragment).commit();
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

