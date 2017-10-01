package com.mario22gmail.vasile.studentist.account;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.student.StudentRequestListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateProfileActivity extends AppCompatActivity {

    @BindView(R.id.user_create_name_EditText)
    AppCompatEditText nameEditText;

    @BindView(R.id.user_create_tel_nr_EditText)
    AppCompatEditText telephoneNumberEditText;

    @BindView(R.id.radioPatient)
    AppCompatRadioButton radioPatient;

    @BindView(R.id.radioStudent)
    AppCompatRadioButton radioStudent;


    @BindView(R.id.radioGroupType)
    RadioGroup radioGroup;

    @BindView(R.id.userTypeErrorTextView)
    TextView userTypeErrorTextView;

    @BindView(R.id.progressBarCreateProfile)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(userName != null && !userName.equals(""))
            nameEditText.setText(userName);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    public void GetUserDetailFromFacebook() {
        AccessToken token = AccessToken.getCurrentAccessToken();

        if (token.isExpired()) {
            AccessToken.refreshCurrentAccessTokenAsync();
            token = AccessToken.getCurrentAccessToken();
        }
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            String name = response.getJSONObject().get(Constants.FbUserDetailsName).toString();
                            nameEditText.setText(name);

                            String birthDateString = response.getJSONObject().get(Constants.FbUserDetailsBirthday).toString();

                            if (!birthDateString.equals("")) {
                                String[] dateAsArray = birthDateString.split("/");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("xxx_mario_xxx", "response from fb: " + response.getJSONArray());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(Constants.FbFields, Constants.FbUserDetailsName + "," + Constants.FbUserDetailsBirthday + "," + Constants.FbUserDetailsGender);
        request.setParameters(parameters);
        request.executeAsync();
    }


    @OnClick({R.id.radioPatient, R.id.radioStudent})
    public void RadioSelected(View view) {
        userTypeErrorTextView.setText("");
    }

    public boolean ValidateInformation() {
        String name = nameEditText.getText().toString();
        if (name.equals("") || name.trim().length() <= 0) {
            nameEditText.setError("Introduceti numele");
            return false;
        }

        String telNumber = telephoneNumberEditText.getText().toString();
        if (telNumber.equals("")) {
            telephoneNumberEditText.setError("Introduceti numarul de telefon");
            return false;
        }

        if(telNumber.trim().length() <= Constants.PhoneNumberMinLength)
        {
            telephoneNumberEditText.setError("Numarul de telefon prea scurt");
            return false;
        }

        if (!radioPatient.isChecked() && !radioStudent.isChecked()) {
            userTypeErrorTextView.setText("Alege o varianta !");
            return false;
        }

        return true;
    }


    @OnClick(R.id.createProfileButton)
    public void CreateProfileButtonClick(final View view) {
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(view);
            return;
        }


        if (!ValidateInformation())
            return;

        progressBar.setVisibility(View.VISIBLE);

        final UserApp user = new UserApp();
        user.appVersion = Constants.APP_VERSION;
        user.deviceToken = FirebaseInstanceId.getInstance().getToken();
        user.name = nameEditText.getText().toString();
        user.telephoneNumber = telephoneNumberEditText.getText().toString();
        if (radioPatient.isChecked())
            user.role = Constants.PatientUserType;
        else if (radioStudent.isChecked())
            user.role = Constants.StudentUserType;

        user.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference usersTable = FirebaseLogic.getInstance().GetUserTableReference();
        usersTable.child(user.uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Constants.ShowErrorFragment(getSupportFragmentManager());
                    return;
                }
                final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_HOW_TO, MODE_PRIVATE);
                if (user.role.equals(Constants.PatientUserType)) {
                    boolean showHowToPage = sp.getBoolean(Constants.DISPLAY_HOW_TO_PATIENT, true);
                    if (showHowToPage) {
                        Intent howToPatientActivity = new Intent(getApplicationContext(), HowToUsePatientActivity.class);
                        startActivity(howToPatientActivity);
                        finish();
                        return;
                    }
                    Intent patientActivitity = new Intent(getApplicationContext(), PatientShowRequestListActivity.class);
                    patientActivitity.putExtra("uid", user.uid);
                    startActivity(patientActivitity);
                    finish();
                    return;
                } else if (user.role.equals(Constants.StudentUserType)) {
                    boolean showHowToStudentPage = sp.getBoolean(Constants.DISPLAY_HOW_TO_STUDENT, true);
                    if (showHowToStudentPage) {
                        Intent howToStudentActivity = new Intent(getApplicationContext(), HowToUseStudent.class);
                        startActivity(howToStudentActivity);
                        finish();
                        return;
                    }
                    Intent studentActivity = new Intent(getApplicationContext(), StudentRequestListActivity.class);
                    studentActivity.putExtra("uid", user.uid);
                    startActivity(studentActivity);
                    finish();
                    return;
                }

                Constants.ShowErrorFragment(getSupportFragmentManager());
                progressBar.setVisibility(View.INVISIBLE);
            }


        });
    }




}
