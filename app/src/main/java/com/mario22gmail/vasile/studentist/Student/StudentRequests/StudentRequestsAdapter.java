package com.mario22gmail.vasile.studentist.Student.StudentRequests;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.AllRequestList.PatientRequestForStudentsViewHolder;

import java.util.ArrayList;
import java.util.List;

import Helpers.Constants;
import PatientComponent.PatientRequest;
import StudentComponent.RequestStatus;
import StudentComponent.StudentRequest;

/**
 * Created by mario on 18/09/2017.
 */

public class StudentRequestsAdapter extends RecyclerView.Adapter<StudentAppliedRequestViewHolder> {



    LayoutInflater layoutInflater;
    List<StudentRequest> studentRequestList = new ArrayList<StudentRequest>();
    Context _context;


    public StudentRequestsAdapter(Context context) {

        _context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void AddPatientToList(StudentRequest request) {
        this.studentRequestList.add(request);
        notifyDataSetChanged();
    }

    public void DeletePatientFromList(final StudentRequest request) {
        for (StudentRequest studentRequestFromAdapter : studentRequestList) {
            if (studentRequestFromAdapter.studentRequestUUID.equals(request.studentRequestUUID)) {
                studentRequestList.remove(studentRequestFromAdapter);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void UpdatePatientToList(StudentRequest request) {

        for (StudentRequest requestFromList : studentRequestList) {
            if (requestFromList.studentRequestUUID.equals(request.studentRequestUUID)) {
                requestFromList.status = request.status;
                notifyDataSetChanged();
                return;
            }
        }

        studentRequestList.add(request);
        notifyDataSetChanged();
    }

    @Override
    public StudentAppliedRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.requesstudentapplied, parent, false);
        StudentAppliedRequestViewHolder viewHolder = new StudentAppliedRequestViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final StudentAppliedRequestViewHolder holder, int position) {

        final StudentRequest studentRequest = studentRequestList.get(position);
        holder.patientName.setText(studentRequest.patientName);
        holder.iconImageView.setImageResource(Constants.GetIconValue(studentRequest.patientRequestType));
        holder.dateOfRequest.setText(studentRequest.studentRequestDate);
        holder.requestTitleTextView.setText(studentRequest.patientRequestType);


        switch (studentRequest.status)
        {
            case Waiting:
                holder.statusTextView.setText("In asteptare");
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.alertColor));
                holder.buttonAdd.setVisibility(View.VISIBLE);
                break;
            case Rejected:
                holder.statusTextView.setText("Cerere respinsa");
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.redAlert));
                holder.buttonAdd.setVisibility(View.INVISIBLE);
                break;
            case Resolved:
                holder.statusTextView.setText("Rezolvat");
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.greenCall));
                holder.buttonAdd.setVisibility(View.INVISIBLE);
                break;
            case Deleted:
                holder.statusTextView.setText("Cerere stearsa");
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.redAlert));
                holder.buttonAdd.setVisibility(View.INVISIBLE);
                break;
            default:
                holder.statusTextView.setVisibility(View.INVISIBLE);
                holder.buttonAdd.setVisibility(View.INVISIBLE);
        }


        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientDetailRequest = new Intent(holder.contextViewHolder, StudentRequestDetailsActivity.class);
                patientDetailRequest.putExtra(Constants.requestUuidIntentExtraName, studentRequest.patientRequestUUID);
                patientDetailRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.contextViewHolder.startActivity(patientDetailRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentRequestList.size();
    }
}
