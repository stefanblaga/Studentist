package com.mario22gmail.vasile.studentist.student.studentRequests;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.R;

/**
 * Created by mario on 19/09/2017.
 */

public class StudentAppliedRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView statusTextView;
    public TextView patientName;
    public TextView requestTitleTextView;
    public ImageView iconImageView;
    public TextView dateOfRequest;

    public Button buttonAdd;
    public final Context contextViewHolder;


    public StudentAppliedRequestViewHolder(View itemView) {
        super(itemView);
//        textViewApproved = (TextView) itemView.findViewById(R.id.textViewApproved);
        requestTitleTextView = (TextView) itemView.findViewById(R.id.patientRequestStudentItemType);
        dateOfRequest = (TextView) itemView.findViewById(R.id.requestDateTextView);
        statusTextView = (TextView) itemView.findViewById(R.id.patientStudentRequestItemStatus);
        buttonAdd = (Button) itemView.findViewById(R.id.buttonApplyForRequest);
        patientName = (TextView) itemView.findViewById(R.id.patientNameTextView);
        iconImageView = (ImageView) itemView.findViewById(R.id.imageViewRequestForStudents);
        contextViewHolder = itemView.getContext();
    }
}
