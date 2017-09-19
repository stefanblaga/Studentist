package com.mario22gmail.vasile.studentist.Student.StudentRequests;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import PatientComponent.PatientRequest;
import StudentComponent.StudentRequest;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentRequestsFragment extends Fragment {


    @BindView(R.id.recyclerViewStudentRequests)
    RecyclerView studentRequestRecycleView;

    StudentRequestsAdapter adapter;

    public StudentRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_student_requests, container, false);
        ButterKnife.bind(this, mainView);
        this.adapter = new StudentRequestsAdapter(getActivity().getApplicationContext());
        studentRequestRecycleView.setAdapter(adapter);
        studentRequestRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        GetAllStudentRequests();
        return mainView;
    }

    public void GetAllStudentRequests() {
        final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference patientRequests = FirebaseLogic.getInstance().GetPatientRequestTableReference();
//        patientRequests.orderByChild(Constants.PatientRequestIsActive).equalTo(false).
//                addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot inactiveRequestsDataSnapshot : dataSnapshot.getChildren()) {
//
//                            }
//                        }
//                    }
//
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        DatabaseReference studentRequests = FirebaseLogic.getInstance().GetStudentsRequestTableReference();
        studentRequests.orderByChild(Constants.StudentUUIDKey).equalTo(userUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                StudentRequest studentRequest = dataSnapshot.getValue(StudentRequest.class);
                if(studentRequest == null)
                    return;

                adapter.AddPatientToList(studentRequest);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                StudentRequest studentRequest = dataSnapshot.getValue(StudentRequest.class);
                if(studentRequest == null)
                    return;

                adapter.UpdatePatientToList(studentRequest);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                StudentRequest studentRequest = dataSnapshot.getValue(StudentRequest.class);
                if(studentRequest == null)
                    return;

                adapter.DeletePatientFromList(studentRequest);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//.getRef().equalTo("a902bfb9-7cbb-4533-8ba5-3ef503f68a47", "studentRequestUUID").

    }

}
