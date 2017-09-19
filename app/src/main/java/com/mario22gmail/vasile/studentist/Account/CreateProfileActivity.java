package com.mario22gmail.vasile.studentist.Account;

import android.app.DatePickerDialog;
import android.content.Intent;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mario22gmail.vasile.studentist.Patient.PatientFirstActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.StudentMainActivity;

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
import butterknife.OnClick;
import butterknife.Optional;

public class CreateProfileActivity extends AppCompatActivity {

    @BindView(R.id.user_create_name_EditText)
    AppCompatEditText nameEditText;

    @BindView(R.id.user_create_tel_nr_EditText)
    AppCompatEditText telephoneNumberEditText;


    @BindView(R.id.mmBirthDateEditText)
    EditText mmBirthEditText;

    @BindView(R.id.yyyyBirthdateEdittext)
    EditText yyyyBirthDayEditText;

    @BindView(R.id.ddBirthdateEditText)
    EditText ddBirthDateEditText;

    @BindView(R.id.radioPatient)
    RadioButton radioPatient;

    @BindView(R.id.radioStudent)
    RadioButton radioStudent;



    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);
        calendar = Calendar.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String provider = user.getProviderId();
        List<String> providerList = user.getProviders();

    }


    @Override
    protected void onResume() {
        super.onResume();
        GetUserDetailFromFacebook();
    }



    public void GetUserDetailFromFacebook()
    {
        AccessToken token = AccessToken.getCurrentAccessToken();

        if(token.isExpired())
        {
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

                            if(!birthDateString.equals(""))
                            {
                                String[] dateAsArray = birthDateString.split("/");
                                mmBirthEditText.setText(dateAsArray[0]);
                                ddBirthDateEditText.setText(dateAsArray[1]);
                                yyyyBirthDayEditText.setText(dateAsArray[2]);
                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("xxx_mario_xxx","response from fb: "  + response.getJSONArray());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString(Constants.FbFields, Constants.FbUserDetailsName + "," + Constants.FbUserDetailsBirthday + "," + Constants.FbUserDetailsGender);
        request.setParameters(parameters);
        request.executeAsync();
    }

    public boolean ValidateInformation() {
        String name = nameEditText.getText().toString();
        if (name.equals("") || name.trim().length() <= 0){
            nameEditText.setError("Introduceti numele");
            return false;
        }

        String telNumber =  telephoneNumberEditText.getText().toString();
        if(telNumber.equals("") || telNumber.trim().length() <=0)
        {
            telephoneNumberEditText.setError("Introduceti numarul de telefon");
            return false;
        }

        String dd = ddBirthDateEditText.getText().toString();
        if(dd.equals("") || dd.trim().length() <=0)
        {
            ddBirthDateEditText.setError("Introduceti ziua");
            return false;
        }

        String mm = mmBirthEditText.getText().toString();
        if(mm.equals("") || mm.trim().length() <= 0)
        {
            mmBirthEditText.setError("Introduceti luna");
            return false;
        }

        String yyyy = yyyyBirthDayEditText.getText().toString();
        if(yyyy.equals("") || yyyy.trim().length() <= 0)
        {
            yyyyBirthDayEditText.setError("Introduceti anul");
            return false;
        }
        return true;
    }


    @OnClick(R.id.createProfileButton)
    public void CreateProfileButtonClick(final View view)
    {
        if(!ValidateInformation())
            return;

        final UserApp user = new UserApp();
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        user.deviceToken = deviceToken;
        user.name = nameEditText.getText().toString();
        user.telephoneNumber = telephoneNumberEditText.getText().toString();
        user.birthDate=  new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());
        if(radioPatient.isChecked())
            user.role = Constants.PatientUserType;
        else if(radioStudent.isChecked())
            user.role = Constants.StudentUserType;

        final String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.uid = uuid;


        DatabaseReference usersTable = FirebaseLogic.getInstance().GetUserTableReference();
        usersTable.child(user.uid).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                {
                    Snackbar.make(view, "A aparut o eroare, asta e. Incearca mai tarziu te rog", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(user.role.equals(Constants.PatientUserType))
                {
                    Intent patientActivitity = new Intent(getApplicationContext(), PatientFirstActivity.class);
                    patientActivitity.putExtra("uid", user.uid);
                    startActivity(patientActivitity);
                    finish();
                    return;
                }else if (user.role.equals(Constants.StudentUserType))
                {
                    Intent studentActivity = new Intent(getApplicationContext(), StudentMainActivity.class);
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

    @Optional
    @OnClick({R.id.mmBirthDateEditText, R.id.ddBirthdateEditText, R.id.yyyyBirthdateEdittext})
    public void BirthDateEditTextClicked(View view)
    {
        DatePickerDialog.OnDateSetListener  dateSelectListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    mmBirthEditText.setText(DateFormatSymbols.getInstance().getMonths()[month]);
                    ddBirthDateEditText.setText(dayOfMonth + "");
                    yyyyBirthDayEditText.setText(year + "");
            }
        };

        new DatePickerDialog(this,dateSelectListener,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


}
