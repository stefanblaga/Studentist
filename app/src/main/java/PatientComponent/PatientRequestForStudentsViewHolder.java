package PatientComponent;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mario22gmail.vasile.studentist.R;

public class PatientRequestForStudentsViewHolder extends RecyclerView.ViewHolder {
    public TextView descriptionTextView;
    TextView patientName;
    public ImageView iconImageView;


    public Button buttonAdd;
    public TextView textViewApproved;


    public PatientRequestForStudentsViewHolder(View itemView) {
        super(itemView);
//        textViewApproved = (TextView) itemView.findViewById(R.id.textViewApproved);
        descriptionTextView = (TextView) itemView.findViewById(R.id.patientStudentRequestItemDescription);
        buttonAdd = (Button) itemView.findViewById(R.id.buttonApplyForRequest);
        iconImageView = (ImageView) itemView.findViewById(R.id.imageViewRequestForStudents);
    }
}
