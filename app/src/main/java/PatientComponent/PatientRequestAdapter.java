package PatientComponent;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.PatientDetailRequest;
import com.mario22gmail.vasile.studentist.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import StudentComponent.StudentRequest;

/**
 * Created by mario on 09/04/2017.
 */

public class PatientRequestAdapter extends RecyclerView.Adapter<PatientRequestAdapter.PatientViewHolder> {

    LayoutInflater layoutInflater;
    List<PatientRequest> requestList = new ArrayList<PatientRequest>( );
    Context _context;

    public PatientRequestAdapter(Context context){
        _context = context;
        layoutInflater = LayoutInflater.from(context);
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
        return;
    }



    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.requestitemforpatient,parent,false);
        PatientViewHolder patientViewHolder = new PatientViewHolder(view);

        return patientViewHolder;
    }

    @Override
    public void onBindViewHolder(final PatientViewHolder holder, final int position) {
        final PatientRequest request = requestList.get(position);

        holder.descriptionTextView.setText(request.description);
        holder.iconImageView.setImageResource(Constants.GetIconValue(request.typeOfRequest));
        holder.dateRequested.setText(request.dateRequestMade);
        holder.requestTypeTextView.setText(request.typeOfRequest);
        holder.deleteRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseLogic.getInstance().DeletePatientRequest(request);
            }
        });

        holder.viewStudentWhoAppliedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent patientDetailRequest = new Intent(holder.contextViewHolder, PatientDetailRequest.class);
                patientDetailRequest.putExtra(Constants.requestUuidIntentExtraName, request.requestUid);
                patientDetailRequest.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.contextViewHolder.startActivity(patientDetailRequest);

            }
        });



        StudentRequest numberOfAppliants = request.getStudentRequest();
        if(numberOfAppliants != null)
            holder.numberOfApplyTextView.setText( "new student applied");
        else
            holder.numberOfApplyTextView.setText("");
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }



    public class PatientViewHolder extends RecyclerView.ViewHolder {
        public CardView requestCardView;
        public TextView descriptionTextView;
        public TextView telTextView;
        TextView dateRequested;
        TextView requestTypeTextView;
        TextView numberOfApplyTextView;
        Button viewStudentWhoAppliedButton;
        ImageButton deleteRequestButton;
        ImageView iconImageView;

        public final Context contextViewHolder;


        public PatientViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.patientRequestItemDescription);
            iconImageView = (ImageView) itemView.findViewById(R.id.request_icon_description);
            dateRequested = (TextView) itemView.findViewById(R.id.patientRequestItemDate);
            requestTypeTextView = (TextView) itemView.findViewById(R.id.patientRequestItemType);
            deleteRequestButton = (ImageButton) itemView.findViewById(R.id.patientDeleteRequestButton);
            numberOfApplyTextView = (TextView) itemView.findViewById(R.id.textViewNrApplied);
            viewStudentWhoAppliedButton = (Button) itemView.findViewById(R.id.buttonStudentsWhoApplied);
            contextViewHolder = itemView.getContext();

        }
    }
}