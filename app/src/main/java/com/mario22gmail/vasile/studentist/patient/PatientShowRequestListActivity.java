package com.mario22gmail.vasile.studentist.patient;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.AboutDialogFragment;
import com.mario22gmail.vasile.studentist.account.DeleteAccountFragment;
import com.mario22gmail.vasile.studentist.account.LoginActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.R;

import Helpers.FirebaseLogic;
import Helpers.UserApp;
import PatientComponent.PatientRequest;
import PatientComponent.PatientRequestAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientShowRequestListActivity extends AppCompatActivity {

    @BindView(R.id.fabAddPatientRequest)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.PatientRequestListToolbar)
    Toolbar toolbar;

    @BindView(R.id.textViewToolBarPatientRequestList)
    TextView textViewToolbar;

    @BindView(R.id.emptyStateConstraintLayout)
    ConstraintLayout emptyStateConstraintLayout;


    @BindView(R.id.patientRequestRecyclerView)
    RecyclerView requestListRecyclerView;

    @BindView(R.id.SwitchRoles)
    Switch swithRoles;

    private Menu mainMenu;
    private PatientRequestAdapter adapter;
    private Context _context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_show_request_list);
        ButterKnife.bind(this);

        _context = this;
        AddThreeDotsMenu(toolbar);

        swithRoles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final DatabaseReference userTable = FirebaseLogic.getInstance().GetUserTableReference();
                    userTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserApp user = dataSnapshot.getValue(UserApp.class);
                            if (user != null) {
                                user.role = "student";
                                userTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        adapter = new PatientRequestAdapter(this);
        getRequestsFromFirebase();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ShowEmptyState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patientdotsmenu, menu);
        mainMenu = menu;
        return true;
    }

    //Menu press should open 3 dot menu
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mainMenu.performIdentifierAction(R.id.empty, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void AddThreeDotsMenu(Toolbar toolbar) {
        Drawable threeDotsMenu = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert);
        toolbar.setOverflowIcon(threeDotsMenu);
        toolbar.inflateMenu(R.menu.patientdotsmenu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.howToPagePatientMenuItem:
                        Intent howToStartPatient = new Intent(getApplicationContext(), HowToUsePatientActivity.class);
                        startActivity(howToStartPatient);
                        finish();
                        return true;
                    case R.id.aboutAppMenuItem:
                        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
                        aboutDialogFragment.show(getSupportFragmentManager(), "about dialog");
                        return true;
                    case R.id.signOutMenuItem:
                        AuthUI.getInstance().signOut((FragmentActivity) _context).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(loginActivity);
                                finish();
                            }
                        });
                        return true;
                    case R.id.deleteAccountMenuItem:
                        DeleteAccountFragment deleteAccountFragment = new DeleteAccountFragment();
                        deleteAccountFragment.show(getSupportFragmentManager(), "delete account dialog");
                        return true;
                    case R.id.exitAppMenuItem:
                        finishAffinity();
                        return true;
                }
                return true;
            }
        });

    }

    public void ShowFabButton() {
        if (adapter.getItemCount() > 4) {
            floatingActionButton.setVisibility(View.INVISIBLE);
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    public void getRequestsFromFirebase() {
        DatabaseReference patientRequestNode = FirebaseLogic.getInstance().GetPatientRequestTableReference();
        requestListRecyclerView.setAdapter(adapter);
        requestListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }


    @OnClick({R.id.fabAddPatientRequestEmptyState, R.id.patientEmptyStateImageView})
    public void AddPatientFromEmptyState(View view) {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }

    public void ShowEmptyState() {
        if (adapter.getItemCount() == 0) {
            requestListRecyclerView.setVisibility(View.INVISIBLE);
            ShowFabButton();
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
        } else {
            ShowFabButton();
            emptyStateConstraintLayout.animate().alpha(0.0f).setDuration(400).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    emptyStateConstraintLayout.setVisibility(View.GONE);
                    requestListRecyclerView.setVisibility(View.VISIBLE);
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
