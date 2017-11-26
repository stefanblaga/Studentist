package com.mario22gmail.vasile.studentist.student.allRequestList;


import android.animation.Animator;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    @BindView(R.id.emptyStateStudentAllRequestContainer)
    ScrollView emptyStateConstraintLayout;


    @BindView(R.id.parrentConstraintLayout)
    ConstraintLayout parrentContraintLayout;


    @BindView(R.id.studentCantApplyConstraintLayout)
    ConstraintLayout studentCantApplyConstraintLayout;

    @BindView(R.id.facebookEmptyStateShareButton)
    ShareButton facebookShareButton;

    private PatientRequestForStundentsAdapter adapter;
    private String userUUID = "";

    public AllRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_all_requests, container, false);
        ButterKnife.bind(this, fragmentView);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(getString(R.string.fb_page)))
                .setQuote(getResources().getString(R.string.facebook_share_description))
                .build();
        facebookShareButton.setShareContent(content);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null)
        {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return fragmentView;
        }

        if(FirebaseLogic.CurrentUser == null)
        {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return fragmentView;
        }

        this.adapter = new PatientRequestForStundentsAdapter(getActivity().getApplicationContext());
        this.userUUID = currentUser.getUid();

        requestRecycleView.setAdapter(adapter);
        requestRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        GetPatientRequestsFromFirebase(FirebaseLogic.CurrentUser.city);
        return fragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        showEmptyState();
        if (userUUID == null || userUUID.equals(""))
        {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return;
        }

        DatabaseReference userNodeChanges = FirebaseLogic.getInstance().GetUserTableReference().child(userUUID);
        userNodeChanges.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                    if (studentUser != null && studentUser.role.equals(Constants.StudentUserType)) {
                        if (studentUser.NumberOfRequest >= 2) {
                            studentCantApplyConstraintLayout.setAlpha(0.0f);
                            studentCantApplyConstraintLayout.animate().alpha(1.0f).setDuration(800).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    studentCantApplyConstraintLayout.setVisibility(View.VISIBLE);
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
                            adapter.StudentCanApply(false);
                        } else {
                            studentCantApplyConstraintLayout.animate().alpha(0.0f).setDuration(800).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    studentCantApplyConstraintLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
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


    private void GetPatientRequestsFromFirebase(String studentCity) {
        DatabaseReference patientRequestTable = FirebaseLogic.getInstance().GetPatientRequestTableReference(studentCity);
        patientRequestTable.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if (request != null && request.isActive)
                    adapter.AddPatientToList(request);
                showEmptyState();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if (request != null && request.isActive) {
                    adapter.UpdatePatientToList(request);
                    showEmptyState();
                } else if (request != null) {
                    adapter.DeletePatientFromList(request);
                    showEmptyState();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if (request != null) {
                    adapter.DeletePatientFromList(request);
                    if(request.isActive)
                    {
                        showEmptyState();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(Constants.LogKey, "show empty from moved");
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null && request.isActive)
                {
                    showEmptyState();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(Constants.LogKey, "show empty from canceled");
                showEmptyState();
            }
        });
    }

    public void showEmptyState() {
        if (adapter.getItemCount() == 0) {
            Log.i(Constants.LogKey,"Enter in adapter < 0");
            requestRecycleView.setVisibility(View.INVISIBLE);
            emptyStateConstraintLayout.setAlpha(0.0f);
            emptyStateConstraintLayout.animate().alpha(1.0f).setDuration(400).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    emptyStateConstraintLayout.setVisibility(View.VISIBLE);
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
                    requestRecycleView.setVisibility(View.VISIBLE);
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
