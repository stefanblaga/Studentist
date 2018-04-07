package com.mario22gmail.vasile.studentist.account;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mario on 25/10/2017.
 */

public class ChooseCityFragment extends android.support.v4.app.Fragment implements ISlidePolicy {

    private String _choosedCity = "";
    @BindView(R.id.CreateaAccountProgressBar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_choose_city, container, false);
        ButterKnife.bind(this, mainView);

        return mainView;
    }


    @Override
    public boolean isPolicyRespected() {
        return false;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }

    public void CityPressedAction(String cityName, View view) {

        progressBar.setVisibility(View.VISIBLE);
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(view);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        _choosedCity = cityName;
        ICreateAccount mainActivity = (ICreateAccount) getActivity();
        mainActivity.CreateAccount(_choosedCity);
    }


    @OnClick({R.id.timisoaraImageView, R.id.timisoaraLabelTextView})
    public void TimisoaraClick(View view) {
        CityPressedAction(Constants.TimisoaraCity, view);
    }

    @OnClick({R.id.bucurestiImageView, R.id.bucurestiLabelTextView})
    public void BucurestCilck(View view) {
        CityPressedAction(Constants.BucurestiCity, view);
    }

    @OnClick({R.id.IasiImageView, R.id.IasiLabelTextView})
    public void IasiClick(View view) {
        CityPressedAction(Constants.IasiCity, view);
    }

    @OnClick({R.id.ClujImageView, R.id.ClujLabelTextView})
    public void ClujClick(View view) {
        CityPressedAction(Constants.ClujCity, view);
    }

    @OnClick({R.id.CraiovaImageView, R.id.CraiovaTextView})
    public void CraiovaClick(View view) {
        CityPressedAction(Constants.CraiovaCity, view);
    }

    @OnClick({R.id.SibiuImageView, R.id.SibiuTextView})
    public void SibiuClick(View view)
    {
        CityPressedAction(Constants.SibiuCity, view);
    }

    @OnClick({R.id.TgMuresImageView, R.id.TgMuresTextView})
    public void TgClick(View view){
        CityPressedAction(Constants.TgMuresCity, view);
    }

    @OnClick ({R.id.ConstantaImageView, R.id.ConstantaTextView})
    public void ConstantaClick(View view) {CityPressedAction(Constants.ConstantaCity, view);}


}
