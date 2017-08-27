package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import Helpers.FirebaseLogic;
import Helpers.UserApp;
import PatientComponent.PatientRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientAddRequest extends AppCompatActivity {

    @BindView(R.id.add_request_img_type)
    ImageView categoryImageView;


    @BindView(R.id.add_request_type_description_label)
    TextView categoryDescriptionTextView;

    @BindView(R.id.add_request_pacient_tel_EditText)
    EditText pacientTelNumberEditText;

    @BindView(R.id.PatientAddRequestToolbar)
    Toolbar add_request_toolbar;

    FirebaseUser currentUser;

    String typeOfRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add_request);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        String categoryDescription = bundle.getString("type_desc");

        setSupportActionBar(add_request_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        categoryDescriptionTextView.setText(categoryDescription);
        typeOfRequest = categoryDescription;

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        int imageResourceName = bundle.getInt("type_img");
        categoryImageView.setImageResource(imageResourceName);


        add_request_toolbar.setTitle("Cerere");
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        add_request_toolbar.setTitleTextColor(white);
        categoryDescriptionTextView.requestFocus();
        FindTelNumber();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void FindTelNumber()
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().GetUserTableReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    UserApp userInfo = dataSnapshot.getValue(UserApp.class);
                    if(userInfo != null)
                    {
                        pacientTelNumberEditText.setText(userInfo.telephoneNumber);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @OnClick(R.id.pacientSendRequestButton)
    public void AddPatientRequest(View view) {
        String userEmail = currentUser.getEmail();
        String userUid = currentUser.getUid();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDateTime = df.format(c.getTime());
        String requestUUID = UUID.randomUUID().toString();

        PatientRequest request = new PatientRequest("ceva",typeOfRequest,userUid,requestUUID,currentDateTime);
        FirebaseLogic.getInstance().WriteRequestToTable(request);

        Snackbar snackbar = Snackbar.make(view, "Adaugat", Snackbar.LENGTH_LONG);
        snackbar.show();

        Intent listRequestActivity = new Intent(getApplicationContext(),PatientShowRequestListActivity.class);
        startActivity(listRequestActivity);
        finish();
    }



}
