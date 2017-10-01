package StudentComponent;

/**
 * Created by mario on 10/06/2017.
 */

public class StudentRequest {
    public String studentUUID;
    public String studentRequestDate;
    public String studentRequestUUID;
    public String studentTel;
    public String patientRequestUUID;
    public String patientName;
    public String patientRequestType;
    public RequestStatus status;
    public String patientUUID;

    public StudentRequest(String studentUUID,String studentTelephoneNumber, String studentRequestUUID,String requestDate, String patientRequestUUID, String patientName, String patientRequestType,String patientUUID)
    {
        this.studentUUID = studentUUID;
        this.studentTel = studentTelephoneNumber;
        this.studentRequestUUID = studentRequestUUID;
        this.studentRequestDate =  requestDate;
        this.patientRequestUUID = patientRequestUUID;
        this.patientName = patientName;
        this.patientRequestType = patientRequestType;
        this.patientUUID = patientUUID;
        this.status = RequestStatus.Waiting;
    }

    public StudentRequest()
    {

    }

}

