package PatientComponent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.patient.DeleteRequestDialog;
import com.mario22gmail.vasile.studentist.patient.PatientRequestDetails;
import com.mario22gmail.vasile.studentist.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.Constants;

/**
 * Created by mario on 09/04/2017.
 */

public class PatientRequestAdapter extends RecyclerView.Adapter<PatientRequestAdapter.PatientViewHolder> {

    LayoutInflater layoutInflater;
    List<PatientRequest> requestList = new ArrayList<PatientRequest>( );
    Context _context;
    FragmentManager _fragmentManager;

    public PatientRequestAdapter(Context context, FragmentManager fragmentManager){
        _context = context;
        this._fragmentManager = fragmentManager;
        layoutInflater = LayoutInflater.from(_context);
    }

    public void DeletePatientFromList(PatientRequest request)
    {
        for(PatientRequest patientRequest : requestList)
        {
            if(patientRequest.requestUid.equals(request.requestUid))
            {
                requestList.remove(patientRequest);
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void AddOrUpdatePatientToList(PatientRequest request){

        for(PatientRequest requestFromList : requestList){
            if(requestFromList.requestUid.equals(request.requestUid)) {
                    requestFromList.studentRequest = request.studentRequest;
                    notifyDataSetChanged();
                    return;
                }
        }

        requestList.add(request);
        notifyDataSetChanged();
    }



    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.requestitemforpatient,parent,false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(PatientViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(final PatientViewHolder holder, final int position) {
        final PatientRequest request = requestList.get(position);
        if(!request.description.equals(""))
        {
            holder.descriptionLabel.setVisibility(View.VISIBLE);
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            if(request.description.length() > 20)
            {
                String textToDisplay = request.description.substring(0,20) + "...";
                holder.descriptionTextView.setText(textToDisplay);
            }
            else
            {
                holder.descriptionTextView.setText(request.description);
            }
        }else
        {
            holder.descriptionLabel.setVisibility(View.GONE);
            holder.descriptionTextView.setVisibility(View.GONE);
        }
        holder.iconImageView.setImageResource(Constants.GetIconValue(request.typeOfRequest));
        holder.dateRequested.setText(request.dateRequestMade);
        holder.requestTypeTextView.setText(request.typeOfRequest);
        holder.telTextView.setText(request.telephoneNumber);
        holder.deleteRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRequestDialog deleteRequestDialog = new DeleteRequestDialog();

                Bundle bundleForFragment = new Bundle();
                bundleForFragment.putString(Constants.requestUuidIntentExtraName,request.requestUid);
                bundleForFragment.putString(Constants.requestCityInternal, request.city);
                if(request.studentRequest != null && request.studentRequest.studentRequestUUID != null)
                    bundleForFragment.putString(Constants.studentRequestUUIDExtraName, request.studentRequest.studentRequestUUID);
                deleteRequestDialog.setArguments(bundleForFragment);
                deleteRequestDialog.show(_fragmentManager, "delete dialog");
            }
        });

        if(request.studentRequest != null)
        {
            Log.i(Constants.LogKey, "student request uuid " + request.studentRequest.studentRequestUUID);
            holder.requestIsWaitingTextView.setVisibility(View.INVISIBLE);
            holder.studentAppliedTextView.setVisibility(View.VISIBLE);
            holder.viewStudentWhoAppliedButton.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.requestIsWaitingTextView.setVisibility(View.VISIBLE);
            holder.studentAppliedTextView.setVisibility(View.INVISIBLE);
            holder.viewStudentWhoAppliedButton.setVisibility(View.INVISIBLE);
        }
        holder.viewStudentWhoAppliedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientDetailRequest = new Intent(holder.contextViewHolder, PatientRequestDetails.class);
                patientDetailRequest.putExtra(Constants.requestUuidIntentExtraName, request.requestUid);
                patientDetailRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.contextViewHolder.startActivity(patientDetailRequest);
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }



    public class PatientViewHolder extends RecyclerView.ViewHolder {
        public CardView requestCardView;
        public TextView descriptionTextView;
        public TextView descriptionLabel;
        public TextView telTextView;
        TextView dateRequested;
        TextView requestTypeTextView;
        TextView studentAppliedTextView;
        TextView requestIsWaitingTextView;
        Button viewStudentWhoAppliedButton;
        ImageButton deleteRequestButton;
        ImageView iconImageView;

        public final Context contextViewHolder;


        public PatientViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.patientRequestItemDescription);
            descriptionLabel = (TextView) itemView.findViewById(R.id.patientRequestItemDescriptionLabel);
            iconImageView = (ImageView) itemView.findViewById(R.id.request_icon_description);
            dateRequested = (TextView) itemView.findViewById(R.id.patientRequestItemDate);
            requestTypeTextView = (TextView) itemView.findViewById(R.id.patientRequestItemType);
            deleteRequestButton = (ImageButton) itemView.findViewById(R.id.patientDeleteRequestButton);
            studentAppliedTextView = (TextView) itemView.findViewById(R.id.textViewStudentApplied);
            requestIsWaitingTextView = (TextView) itemView.findViewById(R.id.textViewRequestWaiting);
            viewStudentWhoAppliedButton = (Button) itemView.findViewById(R.id.buttonStudentsWhoApplied);
            telTextView = (TextView) itemView.findViewById(R.id.patientRequestItemTelNrTextView);
            contextViewHolder = itemView.getContext();
        }
    }
}