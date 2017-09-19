package Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.mario22gmail.vasile.studentist.R;

/**
 * Created by mario on 14/04/2017.
 */

public class Constants {

    public static String studentRequestUUIDExtraName = "studentRequestUUID";

    public static String StudentUUIDKey = "studentUUID";

    public static String LogKey = "MMM_MMM";

    public static String DISPLAY_HOW_TO = "display_howTo";

    public static String DISPLAY_HOW_TO_PATIENT = "display_howTo_patient_key";

    public static String PatientRequestStudentsApplied = "studentRequest";

    public static String StudentRequestPacientTel = "patientTel";

    public static String StudentRequestStudentTel = "studentTel";

    public static String StudentRequestStatus = "status";

    public static String PatientRequestIsActive = "isActive";

    public static String FbUserDetailsName = "name";

    public static String FbUserDetailsBirthday = "birthday";

    public static String FbUserDetailsGender = "gender";

    public static String FbFields = "fields";

    public static String requestUuidIntentExtraName = "requestUID";

    public static String MaleGender = "male";

    public static String FeamaleGender = "female";

    public static String PatientUserType = "patient";

    public static String StudentUserType = "student";


    public static int GetIconValue(String typeOfRequest) {
        switch (typeOfRequest) {
            case "Lucrari":
                return R.drawable.imgprotetica;
            case "Durere":
                return R.drawable.imgdurere;
            case "Igienizare":
                return R.drawable.imgigienizare;
            case "Carii":
                return R.drawable.imgcontrol;
        }
        return R.drawable.imgcontrol;
    }

    public static boolean IsNetworkAvailable(ConnectivityManager connectivityManager) {
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void DisplaySnackbarForInternetConnection(View view) {
            Snackbar.make(view, "Verifica conexiunea la internet", Snackbar.LENGTH_LONG).show();
    }

}
