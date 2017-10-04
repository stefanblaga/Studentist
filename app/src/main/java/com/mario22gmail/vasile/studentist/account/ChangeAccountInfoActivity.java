package com.mario22gmail.vasile.studentist.account;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeAccountInfoActivity extends AppCompatActivity {

    @BindView(R.id.tel_EditText)
    EditText patientTelNumberEditText;

    @BindView(R.id.Name_EditText)
    EditText patientNameEditText;

    @BindView(R.id.ChangeProfileToolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account_info);
        ButterKnife.bind(this);

        toolbar.setTitle(getResources().getString(R.string.change_account_label_toolbar));
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FindUserInfo();
    }


    private void FindUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().GetUserTableReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserApp userInfo = dataSnapshot.getValue(UserApp.class);
                    if (userInfo != null) {
                        patientTelNumberEditText.setText(userInfo.telephoneNumber);
                        patientNameEditText.setText(userInfo.name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public boolean ValidateForm(View view) {
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(view);
            return false;
        }

        String telephoneNumber = patientTelNumberEditText.getText().toString();
        if (telephoneNumber.equals("")) {
            patientTelNumberEditText.setError("Adauga un numar de telefon valid");
            return false;
        }

        if (telephoneNumber.length() <= Constants.PhoneNumberMinLength) {
            patientTelNumberEditText.setError("Numarul de telefon prea scurt");
            return false;
        }

        String patientName = patientNameEditText.getText().toString();
        if (patientName == null || patientName.equals("") || patientName.trim().length() <= 0) {
            patientNameEditText.setError("Adauga numele tau");
            return false;
        }
        return true;
    }

    @OnClick(R.id.saveAccountButton)
    public void SaveButtonClick(View view) {
        if (!ValidateForm(view))
            return;

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().GetUserTableReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserApp userInfo = dataSnapshot.getValue(UserApp.class);
                    if (userInfo == null) {
                        Constants.ShowErrorFragment(getSupportFragmentManager());
                    }

                    userInfo.telephoneNumber = patientTelNumberEditText.getText().toString();
                    userInfo.name = patientNameEditText.getText().toString();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(patientNameEditText.getText().toString()).build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                    FirebaseLogic.getInstance().GetUserTableReference().child(userId).setValue(userInfo);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Constants.ShowErrorFragment(getSupportFragmentManager());
            }
        });

    }
}
