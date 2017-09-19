package com.mario22gmail.vasile.studentist.Student;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.StudentUser;
import PatientComponent.PatientRequest;
import com.mario22gmail.vasile.studentist.Student.AllRequestList.PatientRequestForStundentsAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentMainActivity extends AppCompatActivity {
    private FirebaseDatabase database;

    private RecyclerView recyclerView;
    private PatientRequestForStundentsAdapter adapter;

    @BindView(R.id.toolbarRequestsForStudents)
    Toolbar mainToolBar;

    @BindView(R.id.emptyStateConstraintLayout)
    ConstraintLayout emptyStateConstraintLayout;

    @BindView(R.id.recyclerViewPatientsRequestsForStudents)
    RecyclerView studentsRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerViewPatientsRequestsForStudents);
        this.adapter = new PatientRequestForStundentsAdapter(getApplicationContext());
        String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().UpdateStudentProfileForAddingRequest(userUUID);

        DatabaseReference userNodeChanges = FirebaseLogic.getInstance().GetUserTableReference().child(userUUID);
        userNodeChanges.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                    if(studentUser != null && studentUser.role.equals(Constants.StudentUserType))
                    {
                        if(studentUser.NumberOfRequest >= 2)
                        {
                            adapter.StudentCanApply(false);
                        }else
                        {
                            adapter.StudentCanApply(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GetRightView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showEmptyState();
    }

    public void showEmptyState()
    {
        if(adapter.getItemCount() == 0)
        {
            emptyStateConstraintLayout.setVisibility(View.VISIBLE);
            studentsRecycleView.setVisibility(View.INVISIBLE);

        }else {
            studentsRecycleView.setVisibility(View.VISIBLE);
            emptyStateConstraintLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void GetRightView()
    {
        DatabaseReference patientRequestTable = FirebaseLogic.getInstance().GetPatientRequestTableReference();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientRequestTable.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null && request.isActive)
                    adapter.AddPatientToList(request);
                showEmptyState();


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
                showEmptyState();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}
