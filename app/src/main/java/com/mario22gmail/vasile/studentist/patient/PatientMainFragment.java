package com.mario22gmail.vasile.studentist.patient;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;

import Helpers.FirebaseLogic;
import Helpers.UserApp;
import PatientComponent.PatientRequest;
import PatientComponent.PatientRequestAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientMainFragment extends Fragment {

    @BindView(R.id.fabAddPatientRequest)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.fabAddPatientRequestEmptyState)
    FloatingActionButton fabButtonEmptyState;

    @BindView(R.id.emptyStateConstraintLayout)
    ConstraintLayout emptyStateConstraintLayout;


    @BindView(R.id.patientRequestRecyclerView)
    RecyclerView requestListRecyclerView;

    private PatientRequestAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView =  inflater.inflate(R.layout.activity_patient_show_request_list, container, false);
            ButterKnife.bind(this, mainView);


        SetUpFabButtonClick();
        adapter = new PatientRequestAdapter(getContext(), getFragmentManager());
        getRequestsFromFirebase();
        return mainView;
    }


    public void SetUpFabButtonClick()
    {
        fabButtonEmptyState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRequestActivity = new Intent(getContext(), PatientChooseOptionsActivity.class);
                startActivity(addRequestActivity);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addRequestActivity = new Intent(getContext(), PatientChooseOptionsActivity.class);
                startActivity(addRequestActivity);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ShowEmptyState();
    }

    public void ShowFabButton() {
        if (emptyStateConstraintLayout.getVisibility() == View.VISIBLE) {
            fabButtonEmptyState.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.INVISIBLE);
        } else {
            fabButtonEmptyState.setVisibility(View.INVISIBLE);
            if (adapter.getItemCount() > 4) {
                floatingActionButton.setVisibility(View.INVISIBLE);
            } else {
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getRequestsFromFirebase() {
        DatabaseReference patientRequestNode = FirebaseLogic.getInstance().GetPatientRequestTableReference();
        requestListRecyclerView.setAdapter(adapter);
        requestListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String currentUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        patientRequestNode.getRef()
                .orderByChild("patientUid")
                .equalTo(currentUserUUID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                        if (request != null) {
                            adapter.AddOrUpdatePatientToList(request);
                            ShowFabButton();
                        }
                        ShowEmptyState();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                        if (request != null) {
                            adapter.AddOrUpdatePatientToList(request);
                            ShowFabButton();
                        }
                        ShowEmptyState();
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                        if (request != null) {
                            adapter.DeletePatientFromList(request);
                            ShowFabButton();
                        }
                        ShowEmptyState();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @OnClick(R.id.fabAddPatientRequest)
    public void AddPacientRequestButtonClick(View view) {
        Intent addRequestActivity = new Intent(getContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }


    @OnClick({R.id.fabAddPatientRequestEmptyState, R.id.patientEmptyStateImageView})
    public void AddPatientFromEmptyState(View view) {
        Intent addRequestActivity = new Intent(getContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }

    public void ShowEmptyState() {
        if (adapter.getItemCount() == 0) {
            requestListRecyclerView.setVisibility(View.INVISIBLE);
            emptyStateConstraintLayout.setAlpha(0.0f);
            emptyStateConstraintLayout.animate().alpha(1.0f).setDuration(600).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    emptyStateConstraintLayout.setVisibility(View.VISIBLE);
                    ShowFabButton();
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            emptyStateConstraintLayout.animate().alpha(0.0f).setDuration(400).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    emptyStateConstraintLayout.setVisibility(View.GONE);
                    requestListRecyclerView.setVisibility(View.VISIBLE);
                    ShowFabButton();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
}
