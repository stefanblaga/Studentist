package com.mario22gmail.vasile.studentist.patient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
 * Created by mario on 10/09/2017.
 */

public class DeleteRequestDialog extends DialogFragment {

    @BindView(R.id.deleteRequestToolbar)
    Toolbar toolbar;

    String requestUUID;
    String studentRequestUUID = "";
    String requestCity = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.delete_request_dialog, null);
        builder.setView(dialogView);
        ButterKnife.bind(this, dialogView);

        requestUUID = getArguments().getString(Constants.requestUuidIntentExtraName);
        studentRequestUUID = getArguments().getString(Constants.studentRequestUUIDExtraName);
        requestCity = getArguments().getString(Constants.requestCityInternal);

        toolbar.setTitle(R.string.deleteRequestToolbar);
        int white = ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);

        return builder.create();
    }

    @OnClick(R.id.noDeleteRequestButton)
    public void NoDeleteRequestButtonClick(View view) {
        this.dismiss();
    }

    @OnClick(R.id.yesDeleteRequestButton)
    public void YesDeleteButtonClick(View view) {
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(view);
            return;
        }
        if (requestUUID == null || requestUUID.equals(""))
        {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return;
        }

        FirebaseLogic.getInstance().DeletePatientRequest(requestUUID, studentRequestUUID, requestCity);
        this.dismiss();
    }

}
