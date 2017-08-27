package Helpers;

import java.util.List;

import PatientComponent.PatientRequest;
import StudentComponent.RequestStatus;

/**
 * Created by mario on 02/07/2017.
 */

public class StudentLogic {

    private  static StudentLogic instance  = null;

    public static StudentLogic getInstance()
    {
        if(instance == null)
            instance = new StudentLogic();

        return  instance;
    }

    public boolean CanStudentApply(List<PatientRequest> patientRequestList, String studentUUID){
        for(PatientRequest request : patientRequestList)
        {
            if(request.studentRequest == null)
                continue;

            if(!request.studentRequest.studentUUID.equals(studentUUID))
                continue;

            if(request.studentRequest.status.equals(RequestStatus.Waiting)
                    || request.studentRequest.status.equals(RequestStatus.Canceled))
                return false;
        }

        return true;
    }

}
