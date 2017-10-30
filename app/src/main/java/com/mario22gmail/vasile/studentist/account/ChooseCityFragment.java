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
import android.widget.ProgressBar;
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
 * Created by mario on 25/10/2017.
 */

public class ChooseCityFragment extends android.support.v4.app.Fragment implements ISlidePolicy{

    private String _choosedCity = "";
    @BindView(R.id.CreateaAccountProgressBar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_choose_city,container,false);
        ButterKnife.bind(this,mainView);

        return mainView;
    }



    @Override
    public boolean isPolicyRespected() {
        return false;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }

    public void CityPressedAction(String cityName, View view)
    {

        progressBar.setVisibility(View.VISIBLE);
        if(!Constants.IsNetworkAvailable((ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)))
        {
            Constants.DisplaySnackbarForInternetConnection(view);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        _choosedCity = cityName;
        ICreateAccount mainActivity = (ICreateAccount) getActivity();
        mainActivity.CreateAccount(_choosedCity);
    }



    @OnClick({R.id.timisoaraImageView, R.id.timisoaraLabelTextView})
    public void TimisoaraClick(View view)
    {
        CityPressedAction(Constants.TimisoaraCity, view);
    }

    @OnClick({R.id.bucurestiImageView, R.id.bucurestiLabelTextView})
    public void BucurestCilck(View view)
    {
        CityPressedAction(Constants.BucurestiCity, view);
    }

    @OnClick({R.id.IasiImageView, R.id.IasiLabelTextView})
    public void IasiClick(View view)
    {
        CityPressedAction(Constants.IasiCity, view);
    }

    @OnClick({R.id.ClujImageView, R.id.ClujLabelTextView})
    public void ClujClick(View view)
    {
        CityPressedAction(Constants.ClujCity, view);
    }


}
