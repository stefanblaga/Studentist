package com.mario22gmail.vasile.studentist.student.allRequestList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.R;

public class PatientRequestForStudentsViewHolder extends RecyclerView.ViewHolder {
    public TextView descriptionTextView;
    public TextView patientName;
    public TextView requestTitleTextView;
    public ImageView iconImageView;
    public TextView dateOfRequest;

    public Button buttonAdd;
    public final Context contextViewHolder;


    public PatientRequestForStudentsViewHolder(View itemView) {
        super(itemView);
//        textViewApproved = (TextView) itemView.findViewById(R.id.textViewApproved);
        requestTitleTextView = (TextView) itemView.findViewById(R.id.patientRequestStudentItemType);
        dateOfRequest = (TextView) itemView.findViewById(R.id.requestDateTextView);
        descriptionTextView = (TextView) itemView.findViewById(R.id.patientStudentRequestItemDescription);
        buttonAdd = (Button) itemView.findViewById(R.id.buttonApplyForRequest);
        patientName = (TextView) itemView.findViewById(R.id.patientNameTextView);
        iconImageView = (ImageView) itemView.findViewById(R.id.imageViewRequestForStudents);
        contextViewHolder = itemView.getContext();
    }
}
