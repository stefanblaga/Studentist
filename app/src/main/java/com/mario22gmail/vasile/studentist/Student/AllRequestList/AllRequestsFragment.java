package com.mario22gmail.vasile.studentist.Student.AllRequestList;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import Helpers.StudentUser;
import PatientComponent.PatientRequest;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllRequestsFragment extends Fragment {



    @BindView(R.id.recyclerViewPatientsRequestsForStudents)
    RecyclerView requestRecycleView;

    @BindView(R.id.emptyStateStudentAllRequestConstraintLayout)
    ConstraintLayout emptyStateConstraintLayout;


    @BindView(R.id.parrentConstraintLayout)
    ConstraintLayout parrentContraintLayout;


    @BindView(R.id.studentCantApplyConstraintLayout)
    ConstraintLayout studentCantApplyConstraintLayout;

    private PatientRequestForStundentsAdapter adapter;

    public AllRequestsFragment() {
        // Required empty public constructor
    }

    String userUUID = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_all_requests, container, false);
        ButterKnife.bind(this, fragmentView);

        this.adapter = new PatientRequestForStundentsAdapter(getActivity().getApplicationContext());
        this.userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GetRightView();
        return fragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(userUUID.equals(""))
            return;
        Log.i(Constants.LogKey, "Request List on resume");

        DatabaseReference userNodeChanges = FirebaseLogic.getInstance().GetUserTableReference().child(userUUID);
        userNodeChanges.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Log.i(Constants.LogKey, "entered on user node");
                    StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                    if(studentUser != null && studentUser.role.equals(Constants.StudentUserType))
                    {
                        if(studentUser.NumberOfRequest >= 2)
                        {
                            studentCantApplyConstraintLayout.setVisibility(View.VISIBLE);
                            adapter.StudentCanApply(false);
                        }else
                        {
                            studentCantApplyConstraintLayout.setVisibility(View.GONE);
                            adapter.StudentCanApply(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void GetRightView()
    {
        DatabaseReference patientRequestTable = FirebaseLogic.getInstance().GetPatientRequestTableReference();
        requestRecycleView.setAdapter(adapter);
        requestRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
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
                    showEmptyState();
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

    public void showEmptyState()
    {
        if(adapter.getItemCount() == 0)
        {
            emptyStateConstraintLayout.setVisibility(View.VISIBLE);
            requestRecycleView.setVisibility(View.INVISIBLE);

        }else {
            requestRecycleView.setVisibility(View.VISIBLE);
            emptyStateConstraintLayout.setVisibility(View.INVISIBLE);
        }
    }
}
