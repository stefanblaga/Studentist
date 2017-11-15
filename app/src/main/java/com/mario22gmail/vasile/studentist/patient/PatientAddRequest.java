package com.mario22gmail.vasile.studentist.patient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mario22gmail.vasile.studentist.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.SharedPreferenceLogic;
import PatientComponent.PatientRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientAddRequest extends AppCompatActivity {

    @BindView(R.id.add_request_img_type)
    ImageView categoryImageView;

    @BindView(R.id.add_request_type_description_label)
    TextView categoryDescriptionTextView;

    @BindView(R.id.add_request_pacient_tel_EditText)
    EditText patientTelNumberEditText;

    @BindView(R.id.add_request_Name_EditText)
    EditText patientNameEditText;

    @BindView(R.id.PatientAddRequestToolbar)
    Toolbar add_request_toolbar;

    @BindView(R.id.add_request_Info_EditText)
    EditText requestInfoEditText;

    FirebaseUser currentUser;

    String typeOfRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add_request);
        ButterKnife.bind(this);


        Bundle bundle = getIntent().getExtras();
        String categoryDescription = bundle.getString("type_desc");

        add_request_toolbar.setTitle("Adauga cerere");
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        add_request_toolbar.setTitleTextColor(white);

        setSupportActionBar(add_request_toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        categoryDescriptionTextView.setText(categoryDescription);
        typeOfRequest = categoryDescription;

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        if (FirebaseLogic.CurrentUser == null) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }


        int imageResourceName = bundle.getInt("type_img");
        categoryImageView.setImageResource(imageResourceName);
        requestInfoEditText.setHorizontallyScrolling(false);
        requestInfoEditText.setMaxLines(Integer.MAX_VALUE);

        FindUserInformation();
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

    private void FindUserInformation() {
        if (FirebaseLogic.CurrentUser != null) {
            patientTelNumberEditText.setText(FirebaseLogic.CurrentUser.telephoneNumber);
            patientNameEditText.setText(FirebaseLogic.CurrentUser.name);
            if(FirebaseLogic.CurrentUser.name != null && !FirebaseLogic.CurrentUser.name.equals("") &&
                    FirebaseLogic.CurrentUser.telephoneNumber != null && !FirebaseLogic.CurrentUser.telephoneNumber.equals(""))
            {
                requestInfoEditText.requestFocus();
            }
        }
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


    @OnClick(R.id.pacientSendRequestButton)
    public void AddPatientRequest(View view) {

        if (!ValidateForm(view))
            return;

        if(FirebaseLogic.CurrentUser == null)
        {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        String patientName = patientNameEditText.getText().toString();
        String userUid = currentUser.getUid();
        String requestInfo = requestInfoEditText.getText().toString();
        String telephoneNumber = patientTelNumberEditText.getText().toString();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDateTime = df.format(c.getTime());

        String requestUUID = UUID.randomUUID().toString();

        PatientRequest request = new PatientRequest(patientName, requestInfo, typeOfRequest, userUid,
                requestUUID, currentDateTime, telephoneNumber, FirebaseLogic.CurrentUser.city);
        FirebaseLogic.getInstance().WriteRequestToTable(request);

        if (SharedPreferenceLogic.IsPatientFirstTime(getApplicationContext())) {
            FirebaseLogic.getInstance().UpdateUserNameAndTelephone(currentUser.getUid(), patientName, telephoneNumber);
            SharedPreferenceLogic.SetPatientFirstTime(getApplicationContext(), false);
        }
        finish();
    }


}
