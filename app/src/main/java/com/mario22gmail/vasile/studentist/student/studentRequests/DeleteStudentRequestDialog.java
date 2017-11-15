package com.mario22gmail.vasile.studentist.student.studentRequests;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteStudentRequestDialog extends DialogFragment {


    public DeleteStudentRequestDialog() {
        // Required empty public constructor
    }


    String studentRequestUUID = "";
    String studentUUID = "";

    @BindView(R.id.deleteRequestToolbar)
    Toolbar toolbar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View dialogView = layoutInflater.inflate(R.layout.fragment_delete_student_request_dialog,null);
        builder.setView(dialogView);
        ButterKnife.bind(this,dialogView);

        studentRequestUUID = getArguments().getString(Constants.studentRequestUUIDExtraName);
        studentUUID = getArguments().getString(Constants.studentUUIDBundleKey);

        toolbar.setTitle("Stergere cerere");
        int white = ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);

        return builder.create();
    }

    @OnClick(R.id.noDeleteRequestButton)
    public void NoDeleteRequestButtonClick(View view) {
        this.dismiss();
    }

    @OnClick(R.id.yesDeleteRequestButton)
    public void YesButtonClicked(View view)
    {
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(view);
            return;
        }
        if(studentRequestUUID == null || studentRequestUUID.equals("") || studentUUID == null || studentUUID.equals(""))
        {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return;
        }
        FirebaseLogic.getInstance().DeleteStudentRequest(studentRequestUUID, studentUUID);
        this.dismiss();
    }


}
