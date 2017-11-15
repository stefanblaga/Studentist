package Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mario on 15/10/2017.
 */

public class SharedPreferenceLogic {

    public static final String DISPLAY_HOW_TO = "display_howTo";
    public static final String DISPLAY_HOW_TO_PATIENT = "display_howTo_patient_key";
    public static final String DISPLAY_HOW_TO_STUDENT = "display_howTo_student_key";
    public static final String PatientSharedPrefenceKey = "patientPreference";
    public static final String PatientKeyAnonymousUserUUID = "patientKeyAnonymousUUID";
    public static final String PatientAnonymousName = "patientAnonymousName";
    public static final String PatientAnonymousTel = "patientAnonymousTel";
    public static final String PatientRequestList = "patientRequestList";
    public static final String PatientFirstTime = "patientFirstTime";
    public static final String PatientViewDetailsFirstTime = "patientViewDetailsFirstTime";


    public static String GetAnonymousPatientUUID(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        return settings.getString(PatientKeyAnonymousUserUUID, "");
    }


    public static String GetAnonymousPatientName(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        return settings.getString(PatientAnonymousName, "");
    }

    public static String SetAnonymousPatientName(Context context, String name)
    {
        SharedPreferences settings =  context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = settings.edit().putString(PatientAnonymousName, name);
        pendingEdits.apply();
        return settings.getString(PatientAnonymousName, "");
    }

    public static String GetAnonymousPatientTel(Context context)
    {
        SharedPreferences setting = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        return setting.getString(PatientAnonymousTel, "");
    }

    public static String SetAnonymousPatientTel(Context context, String patientAnonymousTel)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = settings.edit().putString(PatientAnonymousTel, patientAnonymousTel);
        pendingEdits.apply();

        return settings.getString(PatientAnonymousTel, "");
    }


    public static void WriteRequestInSharedPreference(Context context, String requestUUID)
    {
        HashSet<String> requestList = GetPatientRequestList(context);
        requestList.add(requestUUID);
        SharedPreferences settings  = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = settings.edit().putStringSet(PatientRequestList, requestList);
        pendingEdits.commit();
        pendingEdits.apply();
    }


    public static HashSet<String> GetPatientRequestList(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        Set<String> requestList = settings.getStringSet(PatientRequestList, new HashSet<String>());
        return new HashSet<String>(requestList);
    }

    public static boolean IsPatientFirstTime(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        return settings.getBoolean(PatientFirstTime, true);
    }

    public static boolean SetPatientFirstTime(Context context, boolean isPatientFirstTime)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = settings.edit().putBoolean(PatientFirstTime, isPatientFirstTime);
        pendingEdits.apply();

        return settings.getBoolean(PatientFirstTime, true);
    }

    public static boolean IsPatientViewDetailsFirstTime(Context context)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        return settings.getBoolean(PatientViewDetailsFirstTime, true);
    }

    public static boolean SetPatientViewDetailsFirstTime(Context context, boolean value)
    {
        SharedPreferences settings = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = settings.edit().putBoolean(PatientViewDetailsFirstTime, value);
        pendingEdits.apply();

        return settings.getBoolean(PatientViewDetailsFirstTime, true);
    }


    public static void DeletePatientRequestFromList(Context context,String patientRequestUUID)
    {
        HashSet<String> requestList = GetPatientRequestList(context);
        requestList.remove(patientRequestUUID);
        SharedPreferences settings  = context.getSharedPreferences(PatientSharedPrefenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = settings.edit().putStringSet(PatientRequestList, requestList);
        pendingEdits.commit();
        pendingEdits.apply();
    }









    public static boolean ShowHowToPagePatient(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(DISPLAY_HOW_TO, MODE_PRIVATE);
        return sp.getBoolean(DISPLAY_HOW_TO_PATIENT, true);
    }

    public static boolean SetHowToPagePatient(Context context, boolean showHowToPage) {
        SharedPreferences sp = context.getSharedPreferences(DISPLAY_HOW_TO, MODE_PRIVATE);
        SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(DISPLAY_HOW_TO_PATIENT, showHowToPage);
        pendingEdits.apply();

        return sp.getBoolean(DISPLAY_HOW_TO_PATIENT, true);
    }



    public static boolean ShowHowToPageStudent(Context context)
    {
        final SharedPreferences sp = context.getSharedPreferences(DISPLAY_HOW_TO, MODE_PRIVATE);
        return sp.getBoolean(DISPLAY_HOW_TO_STUDENT, true);
    }

    public static boolean SetHowToPageStudent(Context context, boolean showHowToPage)
    {
        final SharedPreferences sp = context.getSharedPreferences(DISPLAY_HOW_TO, MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(DISPLAY_HOW_TO_STUDENT, showHowToPage);
        pendingEdits.apply();

        return sp.getBoolean(DISPLAY_HOW_TO_STUDENT, true);
    }



}
