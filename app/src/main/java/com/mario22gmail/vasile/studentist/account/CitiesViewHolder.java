package com.mario22gmail.vasile.studentist.account;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.R;

/**
 * Created by stefan.blaga on 5/11/2018.
 */

public class CitiesViewHolder extends ViewHolder {

    public TextView CityTextView;
    public ImageView CityIcon;

    public CitiesViewHolder(View itemView) {

        super(itemView);
        CityTextView = itemView.findViewById(R.id.textViewTitle);
        CityIcon = (ImageView) itemView.findViewById(R.id.cityImageView);
    }


}
