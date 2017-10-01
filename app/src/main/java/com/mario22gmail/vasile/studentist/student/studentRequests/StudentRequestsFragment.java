package com.mario22gmail.vasile.studentist.student.studentRequests;


import android.animation.Animator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import StudentComponent.StudentRequest;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentRequestsFragment extends Fragment {


    @BindView(R.id.recyclerViewStudentRequests)
    RecyclerView studentRequestRecycleView;

    @BindView(R.id.emptyStateStudentRequestConstraintLayout)
    ConstraintLayout emptyStateConstraintLayout;

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
        this.adapter = new StudentRequestsAdapter(getActivity());
        studentRequestRecycleView.setAdapter(adapter);
        studentRequestRecycleView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        GetAllStudentRequests();
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        showEmptyState();
    }

    public void GetAllStudentRequests() {
        final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference studentRequests = FirebaseLogic.getInstance().GetStudentsRequestTableReference();
        studentRequests.orderByChild(Constants.StudentUUIDKey).equalTo(userUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                StudentRequest studentRequest = dataSnapshot.getValue(StudentRequest.class);
                if(studentRequest == null)
                    return;

                adapter.AddPatientToList(studentRequest);
                showEmptyState();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                StudentRequest studentRequest = dataSnapshot.getValue(StudentRequest.class);
                if(studentRequest == null)
                    return;

                adapter.UpdatePatientToList(studentRequest);
                showEmptyState();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                StudentRequest studentRequest = dataSnapshot.getValue(StudentRequest.class);
                if(studentRequest == null)
                    return;

                adapter.DeletePatientFromList(studentRequest);
                showEmptyState();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                showEmptyState();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showEmptyState();
            }
        });
    }

    public void showEmptyState()
    {
        if(adapter.getItemCount() == 0)
        {
            studentRequestRecycleView.setVisibility(View.INVISIBLE);
            emptyStateConstraintLayout.setAlpha(0.0f);
            emptyStateConstraintLayout.animate().alpha(1.0f).setDuration(600).setListener(new Animator.AnimatorListener() {
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
        }else {
            emptyStateConstraintLayout.animate().alpha(0.0f).setDuration(600).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    emptyStateConstraintLayout.setVisibility(View.GONE);
                    studentRequestRecycleView.setVisibility(View.VISIBLE);
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
