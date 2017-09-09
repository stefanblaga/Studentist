package Helpers;

import com.mario22gmail.vasile.studentist.R;

/**
 * Created by mario on 14/04/2017.
 */

public class Constants {

    public static String LogKey="MMM_MMM";

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

    public static int GetIconValue(String typeOfRequest)
    {
        switch (typeOfRequest) {
            case "Protetica":
                return R.drawable.imgprotetica;
            case "Durere":
                return R.drawable.imgdurere;
            case "Igienizare":
                return R.drawable.imgigienizare;
            case "Control":
                return R.drawable.imgcontrol;
        }
        return R.drawable.imgcontrol;
    }

}
