package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

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



    private PatientRequestAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_show_request_list);
        ButterKnife.bind(this);

//        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/2.ttf");
//        textViewToolbar.setTypeface(custom_font);


        adapter = new PatientRequestAdapter(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.patientRequestRecyclerView);
        getRightView(FirebaseAuth.getInstance().getCurrentUser().getUid());

//        PatientListAdapterTabLayout adapter = new PatientListAdapterTabLayout(getSupportFragmentManager());
//        patientViewPager.setAdapter(adapter);
//        patientTabLayout.setupWithViewPager(patientViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void ShowFabButton()
    {
        if(adapter.getItemCount() > 4)
        {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }else
        {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    public void getRightView(final String _uid) {

        DatabaseReference patientRequestNode = FirebaseLogic.getInstance().GetPatientRequestTableReference();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null){
                    adapter.AddOrUpdatePatientToList(request);
                    ShowFabButton();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null){
                    adapter.AddOrUpdatePatientToList(request);
                    ShowFabButton();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                PatientRequest request = dataSnapshot.getValue(PatientRequest.class);
                if(request != null){
                    adapter.DeletePatientFromList(request);
                    ShowFabButton();
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
        patientRequestNode.getRef()
                .orderByChild("patientUid")
                .startAt(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .endAt(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(childEventListener);
    }



    @OnClick(R.id.fabAddPatientRequest)
    public void AddPacientRequestButtonClick(View view){
        Intent addRequestActivity = new Intent(getApplicationContext(), PatientChooseOptionsActivity.class);
        startActivity(addRequestActivity);
    }
}
