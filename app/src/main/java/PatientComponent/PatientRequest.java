package PatientComponent;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

import StudentComponent.StudentRequest;

/**
 * Created by mario on 29/03/2017.
 */
@IgnoreExtraProperties
public class PatientRequest {
    public String description;
    public String typeOfRequest;
    public String patientUid;
    public String requestUid;
    public String dateRequestMade;
    public Boolean isActive;
    public StudentRequest studentRequest;

    public PatientRequest(String description, String typeOfRequest, String patientUid, String requestUid, String deteRequestMade) {
        this.description = description;
        this.typeOfRequest = typeOfRequest;
        this.patientUid = patientUid;
        this.requestUid = requestUid;
        this.dateRequestMade = deteRequestMade;
        this.isActive = true;
    }

    public PatientRequest() {
    }


    public StudentRequest getStudentRequest() {
        return studentRequest;
    }

    @Exclude
    public void setStudentRequest(StudentRequest studentRequest) {
        this.studentRequest = studentRequest;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("dateRequestMade", dateRequestMade);
        result.put("patientUid", patientUid);
        result.put("requestUid", requestUid);
//        result.put("studentRequest/studentUUID", studentRequest.studentUUID);
//        result.put("studentRequest/isAccepted", studentRequest.isAccepted);
        result.put("typeOfRequest", typeOfRequest);

        HashMap<String, Object> finalResponse = new HashMap<>();
        finalResponse.put(requestUid, result);
        return finalResponse;
    }
}

