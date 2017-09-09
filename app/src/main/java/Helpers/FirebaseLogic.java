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
import java.util.concurrent.TimeUnit;

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

    public void ApplyForPatientRequest(final PatientRequest patientRequest,final String studentUUID) {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                Log.i("MMMM", "OFFSET: " + offset);
                final double estimatedServerTimeMs = System.currentTimeMillis() + offset;
                Log.i("MMMM", "Server Time: " + estimatedServerTimeMs);

                final DatabaseReference usersTableRef = GetUserTableReference();
                usersTableRef.child(studentUUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                            if(studentUser != null && studentUser.role.equals(Constants.StudentUserType) && studentUser.NumberOfRequest < 2) {
                                studentUser.LastRequestTime = estimatedServerTimeMs+"";
                                studentUser.NumberOfRequest ++;
                                usersTableRef.child(studentUUID).setValue(studentUser);


                                if (patientRequest.studentRequest != null && patientRequest.studentRequest.studentUUID.equals(studentUUID))
                                    return;

                                UUID studentRequestUUID = UUID.randomUUID();

                                final StudentRequest studentRequest = new StudentRequest(studentUUID,studentUser.telephoneNumber, studentRequestUUID.toString(), patientRequest.patientUid, patientRequest.requestUid, RequestStatus.Waiting);

                                DatabaseReference patientsRequestTable = GetPatientRequestTableReference();

                                //order matter!!!!
                                patientsRequestTable.child(patientRequest.requestUid).child(Constants.PatientRequestIsActive).setValue(false);
                                patientsRequestTable.child(patientRequest.requestUid).child(Constants.PatientRequestStudentsApplied).setValue(studentRequest);


                                DatabaseReference studentsRequestTable = GetStudentsRequestTableReference();
                                studentsRequestTable.child(studentRequestUUID.toString()).setValue(studentRequest);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void UpdateStudentProfileForAddingRequest(final String studentUUID)
    {

        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                final double estimatedServerTimeMs = System.currentTimeMillis() + offset;

                final DatabaseReference usersTableRef = GetUserTableReference();
                usersTableRef.child(studentUUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                            if(studentUser != null && studentUser.role.equals(Constants.StudentUserType)) {
                                if(studentUser.LastRequestTime != null) {
                                    double lastRequestTime = Double.parseDouble(studentUser.LastRequestTime);
                                    long days = TimeUnit.MILLISECONDS.toDays(Math.abs((long) estimatedServerTimeMs - (long) lastRequestTime));
                                    if (days >= 2) {
                                        studentUser.NumberOfRequest = 0;
                                        usersTableRef.child(studentUUID).setValue(studentUser);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
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
                .child(Constants.StudentRequestStatus).setValue(RequestStatus.Resolved);
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
