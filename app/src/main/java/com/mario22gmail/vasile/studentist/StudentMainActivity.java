package com.mario22gmail.vasile.studentist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Helpers.FirebaseLogic;
import PatientComponent.PatientRequest;
import PatientComponent.PatientRequestForStundentsAdapter;

public class StudentMainActivity extends AppCompatActivity {
    private FirebaseDatabase database;

    private RecyclerView recyclerView;
    private PatientRequestForStundentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        database = FirebaseDatabase.getInstance();

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPatientsRequestsForStudents);
        this.adapter = new PatientRequestForStundentsAdapter(getApplicationContext());
        GetRightView();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void GetRightView()
    {
        DatabaseReference patientRequestTable = FirebaseLogic.getInstance().GetPatientRequestTableReference();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null && request.isActive)
                    adapter.AddPatientToList(request);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null && request.isActive){
                    adapter.UpdatePatientToList(request);
                    return;
                }
                else if(request != null && !request.isActive)
                {
                    adapter.DeletePatientFromList(request);
                    return;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null){
                    adapter.DeletePatientFromList(request);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientRequestTable.addChildEventListener(childEventListener);

    }




}
