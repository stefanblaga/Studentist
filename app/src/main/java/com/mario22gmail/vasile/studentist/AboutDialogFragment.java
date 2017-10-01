package com.mario22gmail.vasile.studentist;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutDialogFragment extends DialogFragment {


    @BindView(R.id.fbLinkTextView)
    TextView fbLinkTextView;


    public AboutDialogFragment() {
        // Required empty public constructor
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_about_dialog, null);
        builder.setView(dialogView);
        ButterKnife.bind(this, dialogView);

        return builder.create();
    }


    @OnClick(R.id.aboutCloseDialogButton)
    public void AboutExitButtonClick(View view)
    {
        this.dismiss();
    }

    @OnClick(R.id.gmailLinkTextView)
    public void GmailLinkClick(View view)
    {
        TextView gmailLink = (TextView) view;
        String  link = gmailLink.getText().toString();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",link, null));
        startActivity(emailIntent);

    }

    @OnClick(R.id.fbLinkTextView)
    public void FbLinkClick(View view)
    {
        PackageManager packageManager = getActivity().getPackageManager();
        Uri uri = Uri.parse(getActivity().getString(R.string.fb_page));
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=" + uri);
            }
        } catch (PackageManager.NameNotFoundException ignored) {

        }
        Intent fbIntent =new Intent(Intent.ACTION_VIEW, uri);
        getActivity().startActivity(fbIntent);
    }
}
