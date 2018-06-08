package com.mario22gmail.vasile.studentist.account;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.R;

import java.util.List;

import Helpers.Constants;
import butterknife.OnClick;

import static Helpers.Constants.*;

/**
 * Created by stefan.blaga on 5/9/2018.
 */

public class CitiesListViewAdapter extends ArrayAdapter<CityItem>{

    private int listItemLayout;
    private Context _context;
    private FragmentChooseCity2 _parentFragment;

    public CitiesListViewAdapter(FragmentChooseCity2 parentFragment, @NonNull Context context, int resource, @NonNull List<CityItem> objects) {
        super(context, resource, objects);
        _context = context;
        listItemLayout = resource;
        _parentFragment = parentFragment;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final CityItem item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        final CitiesViewHolder viewHolder;
        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(listItemLayout, parent, false);
            viewHolder = new CitiesViewHolder(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (CitiesViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.CityTextView.setText(item.getName());
        viewHolder.CityIcon.setImageResource(item.getImage());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ICreateAccount createAccountActivity = (ICreateAccount) _context;
                Activity mainActivity= (Activity) _context;
                if(!_parentFragment.CompleteAccountRegistration()) {
                    Constants.DisplaySnackbarForInternetConnection(v);
                    return;
                }
                String _choosedCity = Constants.GetCityDbValueFromDisplayValue(item.getName());
                createAccountActivity.CreateAccount(_choosedCity);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }



}
