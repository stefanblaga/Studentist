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

    public static enum RequestType
    {
        Carii, Lucrari, Durere, Igienizare
    }

    public static int PhoneNumberMinLength = 8;

    public static final String TimisoaraCity ="timisoara";

    public static final String ClujCity = "cluj";

    public static final String BucurestiCity = "bucuresti";

    public static final String IasiCity = "iasi";

    public static final String SibiuCity = "sibiu";

    public static final String CraiovaCity = "craiova";

    public static final String TgMuresCity = "tgmures";

    public static final String ConstantaCity = "constanta";

    public static final String OradeaCity = "oradea";

    public static final String AradCity = "arad";

    public static String UserTypeKey = "userTypeKey";

    public static String studentRequestUUIDExtraName = "studentRequestUUID";

    public static String studentUUIDBundleKey = "studentUUID";

    public static String LogKey = "xxx_MMM_MMM";

    public static String DISPLAY_HOW_TO = "display_howTo";

    public static String DISPLAY_Memorium = "display_memorium";

    public static String DISPLAY_MemoriumBool = "display_memorium_key";

    public static String DISPLAY_HOW_TO_PATIENT = "display_howTo_patient_key";

    public static String DISPLAY_HOW_TO_STUDENT = "display_howTo_student_key";

    public static String PatientRequestStudentsApplied = "studentRequest";

    public static String StudentRequestStatus = "status";

    public static String PatientRequestIsActive = "isActive";

    public static String FbUserDetailsName = "name";

    public static String FbUserDetailsBirthday = "birthday";

    public static String FbUserDetailsGender = "gender";

    public static String FbFields = "fields";

    public static String requestUuidIntentExtraName = "requestUID";

    public static String requestCityInternal = "requestCity";

    public static final String PatientUserType = "patient";

    public static final String StudentUserType = "student";

    public static String APP_VERSION = "1.3.0";



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

    public static String GetTypeOfRequstDisplayValue(String typeOfRequestFromDb)
    {
        switch (typeOfRequestFromDb) {
                case "Lucrari":
                    return "Lucrări";
                case "Durere":
                    return "Durere";
                case "Igienizare":
                    return "Igienizare";
                case "Carii":
                    return "Carii";
                default:
                    return "Carii";
        }
    }

    public static int GetCityIconValue(String cityName){
        switch (cityName){
            case "Timișoara":
                return R.drawable.timisoara;
            case "București":
                return  R.drawable.bucuresti;
            case "Cluj-Napoca":
                return R.drawable.cluj;
            case "Iași":
                return R.drawable.iasi;
            case "Sibiu":
                return R.drawable.sibiu;
            case "Târgu Mureș":
                return R.drawable.tgmures;
            case "Craiova":
                return  R.drawable.craiova;
            case "Constanța":
                return R.drawable.constanta;
            default:
                return R.drawable.timisoara;
        }
    }

    public static String GetCityDbValueFromDisplayValue(String cityDisplayValue)
    {
        switch (cityDisplayValue){
            case "Timișoara": return Constants.TimisoaraCity;
            case "Craiova": return Constants.CraiovaCity;
            case "București": return Constants.BucurestiCity;
            case "Cluj-Napoca" : return Constants.ClujCity;
            case "Iași": return Constants.IasiCity;
            case "Sibiu": return Constants.SibiuCity;
            case "Târgu Mureș": return Constants.TgMuresCity;
            case "Constanța": return  Constants.ConstantaCity;
            case "Oradea": return Constants.OradeaCity;
            case "Arad": return Constants.AradCity;

            default: return Constants.TimisoaraCity;
        }
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
