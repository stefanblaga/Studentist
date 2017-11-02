package com.mario22gmail.vasile.studentist.patient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mario22gmail.vasile.studentist.R;

import Helpers.SharedPreferenceLogic;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientCelebrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientcelebrationactivity);
        SharedPreferenceLogic.SetPatientViewDetailsFirstTime(getApplicationContext(), false);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.ViewNumberButton)
    public void ViewNumberButtonClick(View view){
        this.finish();
    }
}
