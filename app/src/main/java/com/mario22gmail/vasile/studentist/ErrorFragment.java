package com.mario22gmail.vasile.studentist;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends DialogFragment {


    public ErrorFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_error, null);
        builder.setView(dialogView);
        ButterKnife.bind(this, dialogView);

        return builder.create();
    }


    @OnClick(R.id.closeErrorDialogButton)
    public void ErrorCloseDialogButtonClick(View view)
    {
        getActivity().finish();
        this.dismiss();
    }

}
