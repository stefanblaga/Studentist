package Helpers;

import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.mario22gmail.vasile.studentist.ErrorFragment;
import com.mario22gmail.vasile.studentist.R;

/**
 * Created by mario on 14/04/2017.
 */

public class Constants {

    public static int PhoneNumberMinLength = 8;

    public static String TimisoaraCity ="timisoara";

    public static String ClujCity = "cluj";

    public static String BucurestiCity = "bucuresti";

    public static String IasiCity = "iasi";

    public static String UserTypeKey = "userTypeKey";

    public static String studentRequestUUIDExtraName = "studentRequestUUID";

    public static String GoogleProviderKey ="GoogleProvider";

    public static String FbProviderKey = "FacebookProvider";

    public static String studentUUIDBundleKey = "studentUUID";

    public static String StudentUUIDKey = "studentUUID";

    public static String LogKey = "xxx_MMM_MMM";

    public static String DISPLAY_HOW_TO = "display_howTo";

    public static String DISPLAY_Memorium = "display_memorium";

    public static String DISPLAY_MemoriumBool = "display_memorium_key";

    public static String DISPLAY_HOW_TO_PATIENT = "display_howTo_patient_key";

    public static String DISPLAY_HOW_TO_STUDENT = "display_howTo_student_key";

    public static String PatientRequestStudentsApplied = "studentRequest";

    public static String StudentRequestPacientTel = "patientTel";

    public static String StudentRequestStudentTel = "studentTel";

    public static String StudentRequestStatus = "status";

    public static String PatientRequestIsActive = "isActive";

    public static String FbUserDetailsName = "name";

    public static String AccountUserNameKey = "nameKey";

    public static String AccountUserPhoneKey = "phoneKey";

    public static String FbUserDetailsBirthday = "birthday";

    public static String FbUserDetailsGender = "gender";

    public static String FbFields = "fields";

    public static String requestUuidIntentExtraName = "requestUID";

    public static String requestCityInternal = "requestCity";

    public static String MaleGender = "male";

    public static String FeamaleGender = "female";

    public static String PatientUserType = "patient";

    public static String StudentUserType = "student";

    public static String APP_VERSION = "1.2.0";





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
        if(connectivityManager == null)
            return false;
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static void DisplaySnackbarForInternetConnection(View view) {
            String alertText = view.getResources().getString(R.string.internet_fail_connected_text);
            Snackbar.make(view, alertText, Snackbar.LENGTH_LONG).show();
    }


    public static void ShowErrorFragment(FragmentManager fragmentManager)
    {
        ErrorFragment errorFragment = new ErrorFragment();
        errorFragment.show(fragmentManager, "delete account dialog");
    }

}
