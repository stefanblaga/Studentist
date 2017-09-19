package com.mario22gmail.vasile.studentist.Patient;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import PatientComponent.PatientRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class PatientRequestDetailFragment extends Fragment {


    @BindView(R.id.patientNameRequestDetailTextView)
    TextView patientNameTextView;

    @BindView(R.id.requestTelTextView)
    TextView telTextView;

    @BindView(R.id.dateRequestTextView)
    TextView dateOfRequestTextView;

    @BindView(R.id.requestDescriptionLabelTextView)
    TextView descriptionLabelTextView;

    @BindView(R.id.requestDescriptionTextView)
    TextView requestDescriptionTextView;

    @BindView(R.id.requestTypeTextView)
    TextView requestTypeTextView;

    @BindView(R.id.mainIconPatientRequestDetail)
    ImageView requestTypeIcon;

    String requestUUID;

    public PatientRequestDetailFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_patient_request_detail, container, false);
        ButterKnife.bind(this, mainView);
        final Fragment thisFragment = this;
        requestUUID = getArguments().getString(Constants.requestUuidIntentExtraName);

        if(requestUUID.equals(""))
            getActivity().finish();

        DatabaseReference patientRequestCollection = FirebaseLogic.getInstance().GetPatientRequestTableReference();
        patientRequestCollection.child(requestUUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commit();
                    getActivity().finish();
                }

                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request == null)
                {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commit();
                    getActivity().finish();
                }

                patientNameTextView.setText(request.patientName);
                telTextView.setText(request.telephoneNumber);
                dateOfRequestTextView.setText(request.dateRequestMade);
                requestTypeIcon.setImageResource(Constants.GetIconValue(request.typeOfRequest));
                requestTypeTextView.setText(request.typeOfRequest);
                if(!request.description.equals(""))
                {
                    descriptionLabelTextView.setVisibility(View.VISIBLE);
                    requestDescriptionTextView.setText(request.description);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commit();
                getActivity().finish();
            }
        });



        return mainView;
    }

    @OnClick({R.id.callPatientImageButton, R.id.requestTelTextView})
    public void CallStudentClick(View view)
    {
        String studentTelephone = telTextView.getText().toString();
        if (studentTelephone == null || studentTelephone.equals("")) {
            getActivity().finish();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + studentTelephone));
        startActivity(intent);
    }

    @OnClick(R.id.sendMessagePatientImageButton)
    public void SendMessageStudentButtonClick(View view) {
        String studentTelephone = telTextView.getText().toString();
        if (studentTelephone == null || studentTelephone.equals("")) {
            getActivity().finish();
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + studentTelephone));
        startActivity(sendIntent);

    }


    @OnClick(R.id.backToListButton)
    public void BackToListButtonClick(View view)
    {
        getActivity().finish();
    }



}
