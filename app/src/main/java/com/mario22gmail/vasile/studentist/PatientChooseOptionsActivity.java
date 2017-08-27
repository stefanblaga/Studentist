package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class PatientChooseOptionsActivity extends AppCompatActivity {

    @BindView(R.id.toolbarChooseRequestType)
    Toolbar toolbar;

    @BindView(R.id.textViewPatientChooseRequestTypeMainTextView)
    TextView textViewMainDescription;

    @BindView(R.id.controlOptionLabelTextView)
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
        toolbar.setTitle("Cerere");

        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);

//        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/1.ttf");
//        textViewMainDescription.setTypeface(custom_font);
//        controlLabel.setTypeface(custom_font);
//        durereLabel.setTypeface(custom_font);
//        igienizareLabel.setTypeface(custom_font);
//        proteticaLabel.setTypeface(custom_font);


    }



    @OnClick({R.id.controlOptionImageView, R.id.controlOptionLabelTextView})
    public void ClickControlCategory(View view)
    {

        Intent addRequestActivity = new Intent(getApplicationContext(),PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgcontrol);
        addRequestActivity.putExtra("type_desc", "Control");
        startActivity(addRequestActivity);
        finish();
    }

    @OnLongClick({R.id.controlOptionImageView, R.id.controlOptionLabelTextView})
    public boolean LongClickControl(View view)
    {
        Snackbar snackbar = Snackbar.make(view,"Controlul description", Snackbar.LENGTH_LONG);
        snackbar.show();
        return true;
    }

    @OnClick({R.id.durereRequestLabelTextView, R.id.DurereOptionImageView})
    public void ClickDurereCategory(View view)
    {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgigienizare);
        addRequestActivity.putExtra("type_desc", "Durere");
        startActivity(addRequestActivity);
        finish();
    }

    @OnClick({R.id.IgienizareOptionImageView, R.id.IgienizareRequestLabelTextView})
    public void ClickIgienizareCategory(View view)
    {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgigienizare);
        addRequestActivity.putExtra("type_desc", "Igienizare");
        startActivity(addRequestActivity);
        finish();
    }

    @OnClick({R.id.ProteticaOptionImageView, R.id.ProteticaRequestLabelTextView})
    public void ClickProteticaCategory(View view)
    {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientAddRequest.class);
        addRequestActivity.putExtra("type_img", R.drawable.imgprotetica);
        addRequestActivity.putExtra("type_desc", "Protetica");
        startActivity(addRequestActivity);
        finish();
    }
}
