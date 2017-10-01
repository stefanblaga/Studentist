package com.mario22gmail.vasile.studentist.account;


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
public class DeleteAccountStudentFragment extends DialogFragment {

    @BindView(R.id.deleteRequestToolbar)
    Toolbar toolbar;

    public DeleteAccountStudentFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_delete_account, null);
        builder.setView(dialogView);
        ButterKnife.bind(this, dialogView);

        toolbar.setTitle(R.string.deleteAccountToolbar);
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

        FirebaseLogic.getInstance().DeleteAllDataForStudent(getActivity());
        this.dismiss();
    }
}
