package PatientComponent;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.mario22gmail.vasile.studentist.R;

import java.util.ArrayList;
import java.util.List;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import StudentComponent.RequestStatus;

/**
 * Created by mario on 14/04/2017.
 */

public class PatientRequestForStundentsAdapter extends RecyclerView.Adapter<PatientRequestForStudentsViewHolder> {

    LayoutInflater layoutInflater;
    List<PatientRequest> patientRequestList = new ArrayList<PatientRequest>();
    Context _context;
    boolean canStudentApply = false;

    public void StudentCanApply(boolean canApply)
    {
        canStudentApply = canApply;
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
        for(PatientRequest patientFromAdapter : patientRequestList)
        {
            if(patientFromAdapter.requestUid.equals(request.requestUid))
            {
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
    public void onBindViewHolder(PatientRequestForStudentsViewHolder holder, final int position) {
        final PatientRequest request = patientRequestList.get(position);
        final String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.descriptionTextView.setText(request.description);
        holder.iconImageView.setImageResource(Constants.GetIconValue(request.typeOfRequest));
        if (request.studentRequest != null) {
            if (request.studentRequest.status.equals(RequestStatus.Resolved)) {
                holder.textViewApproved.setText("Approved by patient");
            }
            if (request.studentRequest.status.equals(RequestStatus.Rejected)) {
                holder.textViewApproved.setText("Rejected by patient");
            }
        }


//        boolean canStudentApply = StudentLogic.getInstance().CanStudentApply(this.patientRequestList, userUUID);
        if (canStudentApply) {
//            holder.buttonAdd.setBackgroundColor(ContextCompat.getColor(_context, R.color.lightGreen));
            holder.buttonAdd.setText("Detalii");
            holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseLogic.getInstance().ApplyForPatientRequest(request, userUUID);
                }
            });
        } else {
//            holder.buttonAdd.setBackgroundColor(ContextCompat.getColor(_context, R.color.redAlert));
            holder.buttonAdd.setVisibility(View.INVISIBLE);
//            holder.buttonAdd.setText("Nu mai poti aplica");
//            holder.buttonAdd.setOnClickListener(null);
        }

    }


    @Override
    public int getItemCount() {
        return patientRequestList.size();
    }


    public int GetIconValue(String typeOfRequest) {
        switch (typeOfRequest) {
            case "Protetica":
                return R.drawable.imgprotetica;
            case "Durere":
                return R.drawable.imgdurere;
            case "Igienizare":
                return R.drawable.imgigienizare;
            case "Control":
                return R.drawable.imgcontrol;
        }
        return R.drawable.imgcontrol;
    }

}
