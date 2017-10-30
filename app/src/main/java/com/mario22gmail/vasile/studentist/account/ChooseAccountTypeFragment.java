package com.mario22gmail.vasile.studentist.account;


import android.animation.Animator;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAccountTypeFragment extends Fragment implements ISlidePolicy {

    @BindView(R.id.Password_TextLayout)
    TextInputLayout passwordTextInputLayout;

    @BindView(R.id.Password_EditText)
    AppCompatEditText passwordEditText;

    @BindView(R.id.ChooseSomethingTextView)
    TextView chooseSomethingErrorTextView;

    private String accountType = "";

    public ChooseAccountTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_choose_account_type, container, false);ButterKnife.bind(this, mainView);

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
                        Constants.DisplaySnackbarForInternetConnection(view);
                        return false;
                    }
                    DatabaseReference passwordTable = FirebaseLogic.getInstance().GetPasswordTableRefernce();
                    passwordTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists())
                            {
                                Constants.ShowErrorFragment(getFragmentManager());
                            }

                            String passwordFromFirebase = dataSnapshot.getValue(String.class);
                            String passwordFromUser = passwordEditText.getText().toString();
                            if(passwordFromFirebase.equals(passwordFromUser))
                            {
                                ICreateAccount mainActivity = (ICreateAccount) getActivity();
                                accountType = Constants.StudentUserType;
                                mainActivity.GoToCitySlide(accountType);
                                return;
                            }else {
                                passwordEditText.setError("Parola nu este corecta");
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    return true;
                }
                return false;
            }
        });

        return mainView;
    }

    @OnClick({R.id.PatientAccountTypeImageView, R.id.PatientLabelTextView})
    public void PatientClick(View view) {
        ShowPasswordEditText(false);
        accountType="";
        accountType = Constants.PatientUserType;
        ICreateAccount mainActivity = (ICreateAccount) getActivity();
        mainActivity.GoToCitySlide(accountType);
    }

    @OnClick({R.id.StudentAccountTypeImageView, R.id.StudentAccountDescriptionTextView, R.id.StudentLabelTextView})
    public void StudentClick(View view) {
        accountType="";
        ShowPasswordEditText(true);
    }

    public void ShowPasswordEditText(boolean showEditText) {
        if (showEditText) {
            passwordTextInputLayout.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(passwordEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            passwordTextInputLayout.setVisibility(View.INVISIBLE);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowPasswordEditText(false);
        accountType = "";
    }

    @Override
    public boolean isPolicyRespected() {
        if (accountType.equals(""))
            return false;

        return true;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        chooseSomethingErrorTextView.setVisibility(View.VISIBLE);
        chooseSomethingErrorTextView.setAlpha(0.0f);
        chooseSomethingErrorTextView.animate().alpha(1.0f).setDuration(400);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden)
        {
            chooseSomethingErrorTextView.setVisibility(View.INVISIBLE);
        }
    }
}
