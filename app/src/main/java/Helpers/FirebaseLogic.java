package Helpers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import PatientComponent.PatientRequest;
import StudentComponent.RequestStatus;
import StudentComponent.StudentRequest;

/**
 * Created by mario on 16/04/2017.
 */

public class FirebaseLogic {

    private static FirebaseLogic instance = null;

    public static FirebaseLogic getInstance() {
        if (instance == null)
            instance = new FirebaseLogic();

        return instance;
    }

    public static String StudentAlreadyAppliedForThat = "Already applied";
    public static String StudentIsNotApplied = "NotFound";

    public static String RoleStudent = "student";

    public static String RolePatient = "patient";

    public static String FireBaseRolesTable = "roles";

    public static String FirebaseUsersTable = "users";

    public static String FirebasePatientRequestTable = "patientRequests";

    public static String FirebaseStudentsRequestTable = "studentsRequest";


    private FirebaseDatabase firebaseDatabase;


    public DatabaseReference GetPatientRequestTableReference() {
        DatabaseReference requestDb = firebaseDatabase.getReference(FirebaseLogic.FirebasePatientRequestTable);
        return requestDb;
    }

    public DatabaseReference GetStudentsRequestTableReference() {
        DatabaseReference studentsRequestDb = firebaseDatabase.getReference(FirebaseLogic.FirebaseStudentsRequestTable);
        return studentsRequestDb;
    }

    public DatabaseReference GetUserTableReference() {
        DatabaseReference userTable = firebaseDatabase.getReference(FirebaseLogic.FirebaseUsersTable);
        return userTable;
    }

    protected FirebaseLogic() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void WriteUserToFirebase(UserApp user) {
        DatabaseReference usersTable = GetUserTableReference();
        usersTable.child(user.uid).setValue(user);
        Log.i("MMMM", "User added");
    }

    public void WriteRequestToTable(PatientRequest request) {
        DatabaseReference patientsRequestTable = GetPatientRequestTableReference();
        patientsRequestTable.child(request.requestUid).setValue(request);
        patientsRequestTable.push();

    }

    public void DeletePatientRequest(PatientRequest request) {
        DatabaseReference patientsRequestTable = GetPatientRequestTableReference();
        patientsRequestTable.child(request.requestUid).removeValue();
    }

    public String ApplyForPatientRequest(PatientRequest patientRequest, String studentUUID) {
        if (patientRequest.studentRequest != null && patientRequest.studentRequest.studentUUID.equals(studentUUID))
            return StudentAlreadyAppliedForThat;

        UUID studentRequestUUID = UUID.randomUUID();

        StudentRequest studentRequest = new StudentRequest(studentUUID, studentRequestUUID.toString(), patientRequest.patientUid, patientRequest.requestUid, RequestStatus.Waiting);

        DatabaseReference patientsRequestTable = GetPatientRequestTableReference();

        //order metter!!!!
        patientsRequestTable.child(patientRequest.requestUid).child(Constants.PatientRequestIsActive).setValue(false);
        patientsRequestTable.child(patientRequest.requestUid).child(Constants.PatientRequestStudentsApplied).setValue(studentRequest);



        DatabaseReference studentsRequestTable = GetStudentsRequestTableReference();
        studentsRequestTable.child(studentRequestUUID.toString()).setValue(studentRequest);

        return "OK";
    }

    public void PatientRequestNotResolved(PatientRequest patientRequest, String studentUUID) {

        RemoveApplyForPatient(patientRequest, studentUUID);

    }

    public void PatientRequestResolved(final PatientRequest patientRequest) {

        if (patientRequest.studentRequest == null)
            return;

        if (!patientRequest.studentRequest.status.equals(RequestStatus.Waiting))
            return;

        if (patientRequest.studentRequest.studentUUID == "" || patientRequest.studentRequest.studentUUID == null)
            return;

        DeletePatientRequest(patientRequest);

        final DatabaseReference studentsRequestTable = GetStudentsRequestTableReference();
        studentsRequestTable.child(patientRequest.studentRequest.studentRequestUUID)
                .child(Constants.StudentRequestStatus).setValue(RequestStatus.Approved);


        final DatabaseReference userPatientDatabaseReference = GetUserTableReference();

        ValueEventListener patientUserEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;

                UserApp patient = dataSnapshot.getChildren().iterator().next().getValue(UserApp.class);


                studentsRequestTable.child(patientRequest.studentRequest.studentRequestUUID)
                        .child(Constants.StudentRequestPacientTel)
                        .setValue(patient.telephoneNumber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        userPatientDatabaseReference.getRef()
                .orderByChild("uid")
                .equalTo(patientRequest.patientUid)
                .addListenerForSingleValueEvent(patientUserEventListener);


        final DatabaseReference userStudentDbRef = GetUserTableReference();
        ValueEventListener studentUserValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists())
                    return;
                UserApp student = dataSnapshot.getChildren().iterator().next().getValue(UserApp.class);


                studentsRequestTable.child(patientRequest.studentRequest.studentRequestUUID)
                        .child(Constants.StudentRequestStudentTel)
                        .setValue(student.telephoneNumber);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userStudentDbRef.getRef()
                .orderByChild("uid")
                .equalTo(patientRequest.studentRequest.studentUUID)
                .addListenerForSingleValueEvent(studentUserValueEventListener);

    }



    public String RemoveApplyForPatient(PatientRequest patientRequest, String studentRequestUIID) {
        if (patientRequest == null)
            return "Request is null";

        if (patientRequest.studentRequest == null)
            return "There is no apply";

        if (!patientRequest.studentRequest.studentRequestUUID.equals(studentRequestUIID))
            return "Permission denied";


        DatabaseReference studentRequestCollection = GetStudentsRequestTableReference();
        studentRequestCollection.child(patientRequest.studentRequest.studentRequestUUID)
                .child(Constants.StudentRequestStatus).setValue(RequestStatus.Rejected);


        DatabaseReference patientRequestTable = GetPatientRequestTableReference();
        patientRequest.studentRequest = null;
        patientRequest.isActive = true;

        Map<String, Object> map = new HashMap<>();
        map.put(patientRequest.requestUid, patientRequest);
        patientRequestTable.updateChildren(map);

        return "OK";
    }


}
