package com.mario22gmail.vasile.studentist.Account;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUseStudent;
import com.mario22gmail.vasile.studentist.Patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.StudentRequestListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Optional;

public class CreateProfileActivity extends AppCompatActivity {

    @BindView(R.id.user_create_name_EditText)
    AppCompatEditText nameEditText;

    @BindView(R.id.user_create_tel_nr_EditText)
    AppCompatEditText telephoneNumberEditText;

    @BindView(R.id.radioPatient)
    RadioButton radioPatient;

    @BindView(R.id.radioStudent)
    RadioButton radioStudent;


    @BindView(R.id.radioGroupType)
    RadioGroup radioGroup;

    @BindView(R.id.userTypeErrorTextView)
    TextView userTypeErrorTextView;

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
        if (telNumber.equals("") || telNumber.trim().length() <= 0) {
            telephoneNumberEditText.setError("Introduceti numarul de telefon");
            return false;
        }

        if (!radioPatient.isChecked() && !radioStudent.isChecked()) {
            userTypeErrorTextView.setText("Alege un raspuns !");
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

        final UserApp user = new UserApp();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        user.deviceToken = deviceToken;
        user.name = nameEditText.getText().toString();
        user.telephoneNumber = telephoneNumberEditText.getText().toString();
        if (radioPatient.isChecked())
            user.role = Constants.PatientUserType;
        else if (radioStudent.isChecked())
            user.role = Constants.StudentUserType;

        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.uid = uuid;


        DatabaseReference usersTable = FirebaseLogic.getInstance().GetUserTableReference();
        usersTable.child(user.uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Snackbar.make(view, "A aparut o eroare, asta e. Incearca mai tarziu te rog", Snackbar.LENGTH_LONG).show();
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

                Snackbar.make(view, "A aparut o eroare, asta e. Incearca mai tarziu te rog", Snackbar.LENGTH_LONG).show();
                return;
            }


        });
    }




}
