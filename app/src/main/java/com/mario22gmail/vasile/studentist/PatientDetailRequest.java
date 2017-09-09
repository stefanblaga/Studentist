package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import PatientComponent.PatientRequest;
import StudentComponent.RequestStatus;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientDetailRequest extends AppCompatActivity {

    @BindView(R.id.acceptButtonPatientRequestDetailsActivity)
    Button acceptButton;
    @BindView(R.id.rejectButtonPatientRequestDetailsActivity)
    Button rejectButton;



    @BindView(R.id.mainIconPatientRequestDetail)
    ImageView mainIconImageView;

    @BindView(R.id.studentPartPatientRequestDetailConstraintLayout)
    ConstraintLayout studentDetailConstraintLayout;

    @BindView(R.id.StudentNameRequestDetailTextView)
    TextView studentNameTextView;

    @BindView(R.id.textViewStudentTel)
    TextView studentTelTextView;

    @BindView(R.id.patientRequestDetailToolbar)
    Toolbar toolbar;

    private String requestUUID;
    private PatientRequest _patientRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail_request);
        ButterKnife.bind(this);

        toolbar.setTitle("Cererea mea");
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(Constants.requestUuidIntentExtraName) != null) {
            requestUUID = bundle.getString(Constants.requestUuidIntentExtraName);
        }
        ShowStudentPartInRequestDetail(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ConfigureActivityView(requestUUID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }



    public void SetViewProperly(final String requestUUID)
    {
        DatabaseReference patientRequestTable = FirebaseLogic.getInstance().
                GetPatientRequestTableReference().child(requestUUID);

        patientRequestTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    finish();

                PatientRequest patientRequest = dataSnapshot.getValue(PatientRequest.class);
                if(patientRequest == null)
                    finish();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void ConfigureActivityView(final String requestUUID) {
        final DatabaseReference patientRequestNode = FirebaseLogic.getInstance()
                .GetPatientRequestTableReference().child(requestUUID);

        ValueEventListener requestListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    finish();
                }
                ShowStudentPartInRequestDetail(false);


                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);

                _patientRequest = request;

                mainIconImageView.setImageResource(Constants.GetIconValue(request.typeOfRequest));

                if (request.studentRequest == null) {
                    ShowStudentPartInRequestDetail(false);
                    return;
                }

                if (request.studentRequest.status == null)
                    return;

                if (request.studentRequest.status.equals(RequestStatus.Canceled) ||
                        request.studentRequest.status.equals(RequestStatus.Rejected))
                    return;

                ShowStudentPartInRequestDetail(true);

                if (request.studentRequest.status.equals(RequestStatus.Waiting)) {
                    acceptButton.setVisibility(View.VISIBLE);
                    rejectButton.setVisibility(View.VISIBLE);
                    studentTelTextView.setVisibility(View.VISIBLE);
                } else if (request.studentRequest.status.equals(RequestStatus.Resolved)) {
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);

                    studentTelTextView.setVisibility(View.VISIBLE);
                    studentTelTextView.setText("Am trimis numarul tau studentului." +
                            " Te va contacta prin telefon. Numarul de telefon al studentului este:"
                            + request.studentRequest.studentTel);
                }

                DatabaseReference accountsDb = FirebaseLogic.getInstance().GetUserTableReference();
                accountsDb.getRef().equalTo(request.studentRequest.studentUUID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists())
                                    return;

                                UserApp user = dataSnapshot.getValue(UserApp.class);
                                if (user == null)
                                    return;

                                studentNameTextView.setText(user.name);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        patientRequestNode.addListenerForSingleValueEvent(requestListener);
    }


    public void ShowStudentPartInRequestDetail(boolean visible) {
        if (visible)
            studentDetailConstraintLayout.setVisibility(View.VISIBLE);
        else
            studentDetailConstraintLayout.setVisibility(View.GONE);
    }


    @OnClick(R.id.acceptButtonPatientRequestDetailsActivity)
    public void AcceptButtonClicked(View view) {
        if (_patientRequest == null)
            return;

        FirebaseLogic.getInstance().PatientRequestResolved(_patientRequest);
        finish();
    }

    @OnClick(R.id.rejectButtonPatientRequestDetailsActivity)
    public void RejectButtonClicked(View view) {
        FirebaseLogic.getInstance().PatientRequestNotResolved(_patientRequest, _patientRequest.studentRequest.studentRequestUUID);
        finish();
    }


}
