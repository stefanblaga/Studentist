package com.mario22gmail.vasile.studentist.patient;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientChooseOptionsActivity extends AppCompatActivity {

    @BindView(R.id.toolbarChooseRequestType)
    Toolbar toolbar;

    @BindView(R.id.textViewPatientChooseRequestTypeMainTextView)
    TextView textViewMainDescription;

    @BindView(R.id.cariiOptionLabelTextView)
    TextView controlLabel;

    @BindView(R.id.durereRequestLabelTextView)
    TextView durereLabel;

    @BindView(R.id.IgienizareRequestLabelTextView)
    TextView igienizareLabel;

    @BindView(R.id.ProteticaRequestLabelTextView)
    TextView proteticaLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_choose_options);
        ButterKnife.bind(this);

        toolbar.setTitle("Adauga cerere");
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }



    @OnClick({R.id.cariiOptionImageView, R.id.cariiOptionLabelTextView})
    public void ClickCariiCategory(View view)
    {

        Intent addRequestActivity = new Intent(getApplicationContext(),PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgcontrol);
        addRequestActivity.putExtra("type_desc", getResources().getString(R.string.requestCariType));
        startActivity(addRequestActivity);
        finish();
    }

    @OnClick({R.id.durereRequestLabelTextView, R.id.DurereOptionImageView})
    public void ClickDurereCategory(View view)
    {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgdurere);
        addRequestActivity.putExtra("type_desc", getResources().getString(R.string.requestPainType));
        startActivity(addRequestActivity);
        finish();
    }

    @OnClick({R.id.IgienizareOptionImageView, R.id.IgienizareRequestLabelTextView})
    public void ClickIgienizareCategory(View view)
    {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgigienizare);
        addRequestActivity.putExtra("type_desc",getResources().getString(R.string.requestCleaningType));
        startActivity(addRequestActivity);
        finish();
    }

    @OnClick({R.id.ProteticaOptionImageView, R.id.ProteticaRequestLabelTextView})
    public void ClickProteticaCategory(View view)
    {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgprotetica);
        addRequestActivity.putExtra("type_desc", "Lucrari");
        startActivity(addRequestActivity);
        finish();
    }
}
