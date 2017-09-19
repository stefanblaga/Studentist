package com.mario22gmail.vasile.studentist.Student.AllRequestList;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.StudentRequests.StudentRequestDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import PatientComponent.PatientRequest;

/**
 * Created by mario on 14/04/2017.
 */

public class PatientRequestForStundentsAdapter extends RecyclerView.Adapter<PatientRequestForStudentsViewHolder> {

    LayoutInflater layoutInflater;
    List<PatientRequest> patientRequestList = new ArrayList<PatientRequest>();
    Context _context;
    boolean canStudentApply = false;

    public void StudentCanApply(boolean canApply) {
        canStudentApply = canApply;
        notifyDataSetChanged();
    }


    public PatientRequestForStundentsAdapter(Context context, List<PatientRequest> patientRequestList) {
        _context = context;
        layoutInflater = LayoutInflater.from(context);
        this.patientRequestList = patientRequestList;
    }

    public PatientRequestForStundentsAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this._context = context;
    }


    public void AddPatientToList(PatientRequest request) {
        this.patientRequestList.add(request);
        notifyDataSetChanged();
    }

    public void DeletePatientFromList(final PatientRequest request) {
        for (PatientRequest patientFromAdapter : patientRequestList) {
            if (patientFromAdapter.requestUid.equals(request.requestUid)) {
                patientRequestList.remove(patientFromAdapter);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void UpdatePatientToList(PatientRequest request) {

        for (PatientRequest requestFromList : patientRequestList) {
            if (requestFromList.requestUid.equals(request.requestUid)) {
                requestFromList.studentRequest = request.studentRequest;
                notifyDataSetChanged();
                return;
            }
        }

        patientRequestList.add(request);
        notifyDataSetChanged();
    }

    @Override
    public PatientRequestForStudentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.requestitemforstudents, parent, false);
        PatientRequestForStudentsViewHolder viewHolder = new PatientRequestForStudentsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PatientRequestForStudentsViewHolder holder, final int position) {
        final PatientRequest request = patientRequestList.get(position);
        final String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.patientName.setText(request.patientName);
        if (!request.description.equals("")) {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            if (request.description.length() > 20) {
                String textToDisplay = request.description.substring(0, 20) + "...";
                holder.descriptionTextView.setText(textToDisplay);
            } else {
                holder.descriptionTextView.setText(request.description);
            }
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
        }
        holder.iconImageView.setImageResource(Constants.GetIconValue(request.typeOfRequest));
        holder.dateOfRequest.setText(request.dateRequestMade);
        holder.requestTitleTextView.setText(request.typeOfRequest);

        if (canStudentApply) {
            holder.buttonAdd.setVisibility(View.VISIBLE);
            holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Constants.IsNetworkAvailable((ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE)))
                    {
                        Constants.DisplaySnackbarForInternetConnection(holder.buttonAdd);
                        return;
                    }

                    FirebaseLogic.getInstance().ApplyForPatientRequest(request, userUUID);

                    Intent patientDetailRequest = new Intent(holder.contextViewHolder, StudentRequestDetailsActivity.class);
                    patientDetailRequest.putExtra(Constants.requestUuidIntentExtraName, request.requestUid);
                    patientDetailRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.contextViewHolder.startActivity(patientDetailRequest);
                }
            });
        } else {
            holder.buttonAdd.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return patientRequestList.size();
    }

}
