package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import Helpers.FirebaseLogic;

public class PatientFirstActivity extends AppCompatActivity {



    private String logMessage = "XXX_Mario_XXX";
    private String _uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _uid = FirebaseAuth.getInstance().getCurrentUser().getUid();;
        getRightView(_uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getRightView(final String _uid) {

        DatabaseReference patientRequestNode = FirebaseLogic.getInstance().GetPatientRequestTableReference();

        ValueEventListener requestListeners = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Intent patientRequestListActivity = new Intent(getApplicationContext(),PatientShowRequestListActivity.class);
                    startActivity(patientRequestListActivity);
                    finish();
                }else {
                    Intent addRequestActivity = new Intent(getApplicationContext(),PatientChooseOptionsActivity.class);
                    startActivity(addRequestActivity);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        patientRequestNode.getRef()
                .orderByChild("patientUid")
                .startAt(_uid)
                .endAt(_uid)
                .addListenerForSingleValueEvent(requestListeners);
    }




    public void AddRequestFabClick(View view) {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }


//ButtonActions





}
