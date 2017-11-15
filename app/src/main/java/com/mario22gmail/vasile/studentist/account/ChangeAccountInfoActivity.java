package com.mario22gmail.vasile.studentist.account;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.StartActivity;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.StudentUser;
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

    @BindView(R.id.spinner2)
    Spinner spinnerCity;

    @BindView(R.id.ChangeCityTextView)
    TextView ChangeCityAlert;

    String city = "";


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

        if (FirebaseLogic.CurrentUser == null) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        city = FirebaseLogic.CurrentUser.city;

        String capitalizeCity = city.substring(0, 1).toUpperCase() + city.substring(1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cityArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerCity.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(capitalizeCity);
        //set the default according to value
        spinnerCity.setSelection(spinnerPosition);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ChangeCityAlert.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
        Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startActivity);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
            startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startActivity);
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
                    if (userInfo.role.equals(Constants.StudentUserType)) {
                        StudentUser studentUserInfo = dataSnapshot.getValue(StudentUser.class);
                        studentUserInfo.telephoneNumber = patientTelNumberEditText.getText().toString();
                        studentUserInfo.name = patientNameEditText.getText().toString();
                        studentUserInfo.city = spinnerCity.getSelectedItem().toString().toLowerCase();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(patientNameEditText.getText().toString()).build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                        FirebaseLogic.getInstance().GetUserTableReference().child(userId).setValue(studentUserInfo);
                        userInfo = (UserApp) studentUserInfo;
                        FirebaseLogic.SetUserApp(userInfo);
                        Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startActivity);
                        finish();
                    } else {
                        userInfo.telephoneNumber = patientTelNumberEditText.getText().toString();
                        userInfo.name = patientNameEditText.getText().toString();
                        userInfo.city = spinnerCity.getSelectedItem().toString().toLowerCase();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(patientNameEditText.getText().toString()).build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                        FirebaseLogic.getInstance().GetUserTableReference().child(userId).setValue(userInfo);
                        FirebaseLogic.SetUserApp(userInfo);
                        Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
                        startActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(startActivity);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Constants.ShowErrorFragment(getSupportFragmentManager());
            }
        });

    }
}
