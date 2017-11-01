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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mario22gmail.vasile.studentist.MainNavigationActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.patient.PatientMainFragment;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.student.StudentMainFragment;

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

    @BindView(R.id.progressBarCreateProfile)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null)
        {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        String userName = currentUser.getDisplayName();
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

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.createProfileButton)
    public void CreateProfileButtonClick(final View view) {
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(view);
            return;
        }


        if (!ValidateInformation())
            return;

        FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        if(authUser == null)
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        progressBar.setVisibility(View.VISIBLE);
        final String userId = authUser.getUid();
        FirebaseLogic.getInstance().GetUserTableReference().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserApp userInfo = dataSnapshot.getValue(UserApp.class);
                    if (userInfo == null) {
                        Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    userInfo.telephoneNumber = telephoneNumberEditText.getText().toString();
                    userInfo.name = nameEditText.getText().toString();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nameEditText.getText().toString()).build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                    FirebaseLogic.getInstance().GetUserTableReference().child(userId).setValue(userInfo);
                    FirebaseLogic.CurrentUser = userInfo;

                    Intent mainActivity = new Intent(getApplicationContext(), MainNavigationActivity.class);
                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainActivity);
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
