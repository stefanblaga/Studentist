package com.mario22gmail.vasile.studentist.Patient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.R;

import Helpers.FirebaseLogic;
import PatientComponent.PatientRequest;
import PatientComponent.PatientRequestAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientShowRequestListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

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


    private PatientRequestAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_show_request_list);
        ButterKnife.bind(this);
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
                }
                return true;
            }
        });


        adapter = new PatientRequestAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.patientRequestRecyclerView);
        getRightView(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowEmptyState();
    }

    private Menu mainMenu;

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


    public void ShowFabButton() {
        if (adapter.getItemCount() > 4) {
            floatingActionButton.setVisibility(View.INVISIBLE);
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    public void ShowEmptyState() {
        if (adapter.getItemCount() <= 0) {
            requestListRecyclerView.setVisibility(View.INVISIBLE);
            emptyStateConstraintLayout.setVisibility(View.VISIBLE);
        } else {
            requestListRecyclerView.setVisibility(View.VISIBLE);
            emptyStateConstraintLayout.setVisibility(View.INVISIBLE);
        }

    }

    public void getRightView(final String _uid) {

        DatabaseReference patientRequestNode = FirebaseLogic.getInstance().GetPatientRequestTableReference();

        ChildEventListener childEventListener = new ChildEventListener() {
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
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientRequestNode.getRef()
                .orderByChild("patientUid")
                .startAt(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .endAt(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(childEventListener);
    }


    @OnClick(R.id.fabAddPatientRequest)
    public void AddPacientRequestButtonClick(View view) {
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }
}
