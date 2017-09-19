package com.mario22gmail.vasile.studentist.Patient;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.ErrorFragment;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import PatientComponent.PatientRequest;
import StudentComponent.StudentRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class PatientRequestDetailStudentFoundFragment extends Fragment {


    String requestUUID = "";
    String _studentRequestUUID = "";
    PatientRequest _patientRequest;

    public PatientRequestDetailStudentFoundFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.studentNameRequestDetailTextView)
    TextView studentNameTextView;

    @BindView(R.id.studentTelTextView)
    TextView studentTelTextView;

    @BindView(R.id.mainIconPatientRequestDetail)
    ImageView mainIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_patient_request_detail_student_found, container, false);

        ButterKnife.bind(this, mainView);

        requestUUID = getArguments().getString(Constants.requestUuidIntentExtraName);

        if (requestUUID.equals("")) {
            getActivity().finish();
            return mainView;
        }

        DatabaseReference patientRequestReference = FirebaseLogic.getInstance()
                .GetPatientRequestTableReference();
        patientRequestReference.child(requestUUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            getActivity().finish();
                            return;
                        }

                        PatientRequest patientRequest= dataSnapshot.getValue(PatientRequest.class);
                        if(patientRequest == null) {
                            getActivity().finish();
                            return;
                        }

                        _patientRequest = patientRequest;

                        StudentRequest studentRequest = patientRequest.studentRequest;
                        if (studentRequest == null) {
                            getActivity().finish();
                            return;
                        }

                        String studentUUID = studentRequest.studentUUID;
                        _studentRequestUUID = studentRequest.studentRequestUUID;

                        FirebaseLogic.getInstance().GetUserTableReference()
                                .child(studentUUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    getActivity().finish();
                                    return;
                                }

                                UserApp student = dataSnapshot.getValue(UserApp.class);
                                if (student == null) {
                                    getActivity().finish();
                                    return;
                                }

                                studentNameTextView.setText(student.name);
                                studentTelTextView.setText(student.telephoneNumber);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        return mainView;
    }


    @OnClick(R.id.acceptButtonPatientRequestDetailsActivity)
    public void ResolvedButtonClicked(View view) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }


    @OnClick({R.id.callStudentImageButton, R.id.studentTelTextView})
    public void CallStudentButtonClick(View view) {
        String studentTelephone = studentTelTextView.getText().toString();
        if (studentTelephone == null || studentTelephone.equals("")) {
            getActivity().finish();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + studentTelephone));
        startActivity(intent);
////        startActivity(intent);
//        getActivity().finish();
    }

    @OnClick(R.id.sendMessageStudentImageButton)
    public void SendMessageStudentButtonClick(View view) {
        String studentTelephone = studentTelTextView.getText().toString();
        if (studentTelephone == null || studentTelephone.equals("")) {
            getActivity().finish();
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + studentTelephone));
        startActivity(sendIntent);

    }


    @OnClick(R.id.acceptButtonPatientRequestDetailsActivity)
    public void AcceptButtonClicked(View view) {
        if(!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            Constants.DisplaySnackbarForInternetConnection(mainIcon);
            return;
        }
        Log.i(Constants.LogKey,"Finish from accept");

        if (requestUUID.equals("") || _studentRequestUUID.equals("")) {
            ShowErrorPage();
            return;
        }

        FirebaseLogic.getInstance().PatientRequestResolved(requestUUID, _studentRequestUUID);
        Log.i(Constants.LogKey,"Finish from accept");
        getActivity().finish();
    }

    @OnClick(R.id.mainIconPatientRequestDetail)
    public void MainIconClick(View view)
    {
        Constants.DisplaySnackbarForInternetConnection(mainIcon);
    }


    @OnClick(R.id.rejectButtonPatientRequestDetailsActivity)
    public void RejectButtonClicked(View view) {
        if(!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            Constants.DisplaySnackbarForInternetConnection(mainIcon);
            return;
        }
        if(_patientRequest  == null || _studentRequestUUID.equals(""))
        {
            ShowErrorPage();
            return;
        }
        FirebaseLogic.getInstance().PatientRequestNotResolved(_patientRequest, _studentRequestUUID);
        getActivity().finish();
    }


    public void ShowErrorPage() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        ErrorFragment errorFragment = new ErrorFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.patientRequestStudentAppliedFragment, errorFragment)
                .commit();
    }

}
