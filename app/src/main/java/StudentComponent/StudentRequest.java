package StudentComponent;

/**
 * Created by mario on 10/06/2017.
 */

public class StudentRequest {
    public String studentUUID;
    public String studentRequestUUID;
    public String patientUUID;
    public String patientRequestUUID;
    public String patientTel;
    public String studentTel;
    public RequestStatus status;

    public StudentRequest(String studentUUID, String studentRequestUUID, String pacientUUID , String patientRequestUUID, RequestStatus status )
    {
        this.studentUUID = studentUUID;
        this.studentRequestUUID = studentRequestUUID;
        this.patientUUID = pacientUUID;
        this.patientRequestUUID = patientRequestUUID;
        this.status = status;
    }

    public StudentRequest()
    {

    }

}

