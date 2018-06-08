package com.mario22gmail.vasile.studentist.account;


import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.mario22gmail.vasile.studentist.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Helpers.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.customtabs.CustomTabsClient.getPackageName;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChooseCity2 extends android.support.v4.app.Fragment implements ISlidePolicy {
    @BindView(R.id.CitiesListView)
    ListView citiesListView;

    @BindView(R.id.CreateaAccountProgressBar)
    ProgressBar progressBar;

    CitiesListViewAdapter citiesAdapter;
    List<CityItem> citiesList;

    public FragmentChooseCity2() {
        // Required empty public constructor
    }

    @Override
    public boolean isPolicyRespected() {
        return false;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_choose_city2, container, false);
        ButterKnife.bind(this, mainView);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        citiesList = new ArrayList<CityItem>();
        String[] arrayOfCities = getResources().getStringArray(R.array.cityArray);
        for (String city: arrayOfCities) {
            citiesList.add(new CityItem(city, Constants.GetCityIconValue(city)));
        }
        SortCityItemsList();
        citiesAdapter = new CitiesListViewAdapter(this,getContext(),R.layout.city_item_view_holder, citiesList);
        citiesListView.setAdapter(citiesAdapter);
    }

    public boolean CompleteAccountRegistration(){
        progressBar.setVisibility(View.VISIBLE);
        if (!Constants.IsNetworkAvailable((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))) {
            progressBar.setVisibility(View.INVISIBLE);
            return false;
        }
        return true;
    }


    //sort the cities by name
    private void SortCityItemsList(){
        Collections.sort(citiesList, new Comparator<CityItem>() {
            @Override
            public int compare(CityItem o1, CityItem o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

    }
}
