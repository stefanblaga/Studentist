package com.mario22gmail.vasile.studentist.student.studentRequests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mario22gmail.vasile.studentist.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.Constants;
import StudentComponent.StudentRequest;

/**
 * Created by mario on 18/09/2017.
 */

public class StudentRequestsAdapter extends RecyclerView.Adapter<StudentAppliedRequestViewHolder> {


    LayoutInflater layoutInflater;
    List<StudentRequest> studentRequestList = new ArrayList<StudentRequest>();
    Context _context;
    FragmentManager _fragmentManager;


    public StudentRequestsAdapter(Context context, FragmentManager fragmentManager) {

        _context = context;
        _fragmentManager = fragmentManager;
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

        return new StudentAppliedRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StudentAppliedRequestViewHolder holder, int position) {

        final StudentRequest studentRequest = studentRequestList.get(position);
        holder.patientName.setText(studentRequest.patientName);
        holder.iconImageView.setImageResource(Constants.GetIconValue(studentRequest.patientRequestType));
        holder.dateOfRequest.setText(studentRequest.studentRequestDate);
        holder.requestTitleTextView.setText(studentRequest.patientRequestType);


        switch (studentRequest.status) {
            case Waiting:
                String statusTextWaiting = _context.getResources().getString(R.string.waitingStatusCard);
                holder.statusTextView.setText(statusTextWaiting);
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.blue));
                SetButtonDetailsAction(holder, studentRequest);
                break;
            case Rejected:

                String statusTextRejected = _context.getResources().getString(R.string.requestRejectedStatusText);
                holder.statusTextView.setText(statusTextRejected);
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.alertColor));
                SetButtonDeleteAction(holder, studentRequest);
                break;
            case Resolved:
                String statusRequestResolved = _context.getResources().getString(R.string.requestResolvedStatusText);
                holder.statusTextView.setText(statusRequestResolved);
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.greenCall));
                SetButtonDeleteAction(holder, studentRequest);
                break;
            case Deleted:
                String statusTextDeletedRequest = _context.getResources().getString(R.string.requestDeletedStatusText);
                holder.statusTextView.setText(statusTextDeletedRequest);
                holder.statusTextView.setTextColor(_context.getResources().getColor(R.color.alertColor));
                SetButtonDeleteAction(holder, studentRequest);
                break;
            default:
                holder.statusTextView.setVisibility(View.INVISIBLE);
                holder.buttonAdd.setVisibility(View.INVISIBLE);
                holder.buttonAdd.setOnClickListener(null);
                break;
        }
    }


    public void SetButtonDeleteAction(StudentAppliedRequestViewHolder holder, final StudentRequest studentRequest) {
        holder.buttonAdd.setText(_context.getText(R.string.deleteRequestButtonText));
        holder.buttonAdd.setTextColor(_context.getResources().getColor(R.color.white));
        holder.buttonAdd.setBackground(ContextCompat.getDrawable(_context, R.drawable.roundbuttondelete));

        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundleForFragment = new Bundle();
                bundleForFragment.putString(Constants.studentRequestUUIDExtraName, studentRequest.studentRequestUUID);
                bundleForFragment.putString(Constants.studentUUIDBundleKey, studentRequest.studentUUID);
                DeleteStudentRequestDialog deleteRequestDialog = new DeleteStudentRequestDialog();
                deleteRequestDialog.setArguments(bundleForFragment);
                deleteRequestDialog.show(_fragmentManager, "delete dialog");

            }
        });
        holder.buttonAdd.setVisibility(View.VISIBLE);
    }

    public void SetButtonDetailsAction(final StudentAppliedRequestViewHolder holder, final StudentRequest studentRequest) {
        holder.buttonAdd.setText(_context.getResources().getText(R.string.detailsButtonsText));
        holder.buttonAdd.setTextColor(_context.getResources().getColor(R.color.white));
        holder.buttonAdd.setBackground(ContextCompat.getDrawable(_context, R.drawable.roundbuttoncategoryiconsstyle));

        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientDetailRequest = new Intent(holder.contextViewHolder, StudentRequestDetailsActivity.class);
                patientDetailRequest.putExtra(Constants.requestUuidIntentExtraName, studentRequest.patientRequestUUID);
                patientDetailRequest.putExtra(Constants.requestCityInternal, studentRequest.patientCity);
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
