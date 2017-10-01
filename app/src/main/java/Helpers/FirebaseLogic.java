package Helpers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.account.LoginActivity;
import com.mario22gmail.vasile.studentist.patient.PatientShowRequestListActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import PatientComponent.PatientRequest;
import StudentComponent.RequestStatus;
import StudentComponent.StudentRequest;

import static com.facebook.FacebookSdk.getApplicationContext;

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
        return firebaseDatabase.getReference(FirebaseLogic.FirebasePatientRequestTable);
    }

    public DatabaseReference GetStudentsRequestTableReference() {
        return firebaseDatabase.getReference(FirebaseLogic.FirebaseStudentsRequestTable);
    }

    public DatabaseReference GetUserTableReference() {
        return firebaseDatabase.getReference(FirebaseLogic.FirebaseUsersTable);
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

    public void DeletePatientRequest(String requestUUID, String studentRequestUUID) {
        DatabaseReference patientsRequestTable = GetPatientRequestTableReference();
        patientsRequestTable.child(requestUUID).removeValue();

        if (studentRequestUUID != null && !studentRequestUUID.equals("")) {
            DatabaseReference studentRequestCollection = GetStudentsRequestTableReference();
            studentRequestCollection.child(studentRequestUUID)
                    .child(Constants.StudentRequestStatus).setValue(RequestStatus.Deleted);

        }
    }

    public void DeleteStudentRequest(final String requestUUID, final String studentUUID) {
        final DatabaseReference studentRequestCollection = GetStudentsRequestTableReference();
        studentRequestCollection.child(requestUUID).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    return;
                StudentRequest request = dataSnapshot.getValue(StudentRequest.class);
                if (request == null)
                    return;

                if (request.status != RequestStatus.Waiting && request.studentUUID.equals(studentUUID)) {
                    studentRequestCollection.child(requestUUID).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void ApplyForPatientRequest(final PatientRequest patientRequest, final String studentUUID) {
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
                        if (dataSnapshot.exists()) {
                            StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                            if (studentUser != null && studentUser.role.equals(Constants.StudentUserType) && studentUser.NumberOfRequest < 2) {
                                studentUser.LastRequestTime = estimatedServerTimeMs + "";
                                studentUser.NumberOfRequest++;
                                usersTableRef.child(studentUUID).setValue(studentUser);


                                if (patientRequest.studentRequest != null && patientRequest.studentRequest.studentUUID.equals(studentUUID))
                                    return;

                                UUID studentRequestUUID = UUID.randomUUID();
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String currentDateTime = df.format(c.getTime());
                                final StudentRequest studentRequest = new StudentRequest(studentUUID,
                                        studentUser.telephoneNumber, studentRequestUUID.toString(),
                                        currentDateTime, patientRequest.requestUid,
                                        patientRequest.patientName, patientRequest.typeOfRequest, patientRequest.patientUid);
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


    public void DeleteAllDataForPatient(final Context context) {
        final FragmentActivity fragmentActivity = (FragmentActivity) context;
        String patientUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userTable = GetUserTableReference();
        userTable.child(patientUUID).getRef().removeValue();

        DatabaseReference patientRequestTable = GetPatientRequestTableReference();
        patientRequestTable.getRef()
                .orderByChild("patientUid")
                .startAt(patientUUID)
                .endAt(patientUUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot patientRequest : dataSnapshot.getChildren()) {
                            patientRequest.getRef().removeValue();
                        }

                        AuthUI.getInstance()
                                .delete(fragmentActivity)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                                            context.startActivity(loginActivity);
                                            PatientShowRequestListActivity patientActivity = (PatientShowRequestListActivity) context;
                                            patientActivity.finishAffinity();
                                        } else {
                                            Constants.ShowErrorFragment(((FragmentActivity) context).getSupportFragmentManager());
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

//
//    public void DeleteAccountExperimental()
//    {
//        AccessToken token = AccessToken.getCurrentAccessToken();
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        String providerName =  currentUser.getProviderData().
//                get(currentUser.getProviderData().size() -1).getProviderId();
//        if(providerName.equals("facebook.com"))
//        {
//            AuthCredential fbAuthCredentials = FacebookAuthProvider.getCredential(currentUser.getUid());
//            currentUser.reauthenticate(fbAuthCredentials).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()) {
//                        FirebaseAuth.getInstance().getCurrentUser().delete();
//                        AuthUI.getInstance()
//                                .delete((FragmentActivity) mainActivity)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
//                                            mainActivity.startActivity(loginActivity);
//                                            StudentRequestListActivity studentActivity = (StudentRequestListActivity) mainActivity;
//                                            studentActivity.finishAffinity();
//                                        } else {
//                                            //// TODO: 27/09/2017 error page show
//                                            // Deletion failed
//                                        }
//                                    }
//                                });
//                    }else
//                    {
//                        Log.i("MMM", "error");
//                    }
//                }
//            });
//        }
//        else
//        {
//            AuthCredential googleAuthCredentials = GoogleAuthProvider.getCredential(currentUser.getUid(),null);
//            currentUser.reauthenticate(googleAuthCredentials).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful())
//                    {
//                        FirebaseAuth.getInstance().getCurrentUser().delete();
//                        AuthUI.getInstance()
//                                .delete((FragmentActivity) mainActivity)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
//                                            mainActivity.startActivity(loginActivity);
//                                            StudentRequestListActivity studentActivity = (StudentRequestListActivity) mainActivity;
//                                            studentActivity.finishAffinity();
//                                        } else {
//                                            //// TODO: 27/09/2017 error page show
//                                            // Deletion failed
//                                        }
//                                    }
//                                });
//                    }else
//                    {
//                        Log.i("MMM", "error");
//                    }
//                }
//            });
//        }
//    }

    public void DeleteAllDataForStudent(final Context context)
    {
        final FragmentActivity fragmentActivity = (FragmentActivity) context;
        String studentUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userTable = GetUserTableReference();
        userTable.child(studentUUID).getRef().removeValue();


        DatabaseReference patientRequestTable = GetStudentsRequestTableReference();
        patientRequestTable.getRef()
                .orderByChild("studentUUID")
                .startAt(studentUUID)
                .endAt(studentUUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot studentRequest : dataSnapshot.getChildren()) {
                            studentRequest.getRef().removeValue();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void UpdateStudentProfileForAddingRequest(final String studentUUID) {

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
                        if (dataSnapshot.exists()) {
                            Long currentTimeSpan = (long) estimatedServerTimeMs;
                            Calendar calendarCurrentTimeSpan = Calendar.getInstance();
                            calendarCurrentTimeSpan.setTimeInMillis(currentTimeSpan);

                            StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                            if (studentUser != null && studentUser.role.equals(Constants.StudentUserType)) {
                                if (studentUser.LastRequestTime != null) {
                                    double lastRequestTimeDb = Double.parseDouble(studentUser.LastRequestTime);
                                    Long lasrRequestLong = (long) lastRequestTimeDb;
                                    Calendar calendarTimeFromDb = Calendar.getInstance();
                                    calendarTimeFromDb.setTimeInMillis(lasrRequestLong);

                                    if (CalendarHasMoreDays(calendarCurrentTimeSpan, calendarTimeFromDb)) {
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

    public boolean CalendarHasMoreDays(Calendar currentCalendar, Calendar serverCalendar) {

        int currentTimeYear = currentCalendar.get(Calendar.YEAR);
        int currentTimeMonth = currentCalendar.get(Calendar.MONTH);
        int currentTimeDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        Log.i(Constants.LogKey, "Current date is: " + currentTimeYear + " month: "
                + currentTimeMonth + " day: " + currentTimeDay);

        int serverTimeYear = serverCalendar.get(Calendar.YEAR);
        int serverTimetMonth = serverCalendar.get(Calendar.MONTH);
        int serverTimetDay = serverCalendar.get(Calendar.DAY_OF_MONTH);

        Log.i(Constants.LogKey, "Server date is: " + serverTimeYear + " month: "
                + serverTimetMonth + " day: " + serverTimetDay);

        if (currentTimeYear > serverTimeYear)
            return true;

        if (currentTimeYear == serverTimeYear) {
            if (currentTimeMonth > serverTimetMonth) {
                return true;
            }
            if (currentTimeMonth == serverTimetMonth) {
                if (currentTimeDay > serverTimetDay) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void PatientRequestNotResolved(PatientRequest patientRequest, String studentRequestUUID) {
        if (patientRequest == null)
            return;
        if (patientRequest.studentRequest == null)
            return;

        if (!patientRequest.studentRequest.studentRequestUUID.equals(studentRequestUUID))
            return;


        DatabaseReference studentRequestCollection = GetStudentsRequestTableReference();
        studentRequestCollection.child(patientRequest.studentRequest.studentRequestUUID)
                .child(Constants.StudentRequestStatus).setValue(RequestStatus.Rejected);


        DatabaseReference patientRequestTable = GetPatientRequestTableReference();
        patientRequestTable.child(patientRequest.requestUid).child("studentRequest").setValue(null);
        patientRequestTable.child(patientRequest.requestUid).child("isActive").setValue(true);
//        patientRequest.studentRequest = null;
//        patientRequest.isActive = true;
//
//        Map<String, Object> map = new HashMap<>();
//        map.put(patientRequest.requestUid, patientRequest);
//        patientRequestTable.updateChildren(map);

    }

    public void PatientRequestResolved(final String patientRequestUUID, final String studentRequestUUID) {

        if (patientRequestUUID == null || patientRequestUUID.equals(""))
            return;


        DeletePatientRequest(patientRequestUUID, null);


        if (studentRequestUUID == null || studentRequestUUID.equals(""))
            return;

        //todo check with wrong student request id
        final DatabaseReference studentsRequestTable = GetStudentsRequestTableReference();
        studentsRequestTable.child(studentRequestUUID)
                .child(Constants.StudentRequestStatus).setValue(RequestStatus.Resolved);
    }


}
