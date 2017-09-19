package com.mario22gmail.vasile.studentist.HowToPage.Patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.mario22gmail.vasile.studentist.Patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.StudentRequestListActivity;

import Helpers.Constants;

public class HowToUsePatientActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_how_to_use_patient);


        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SliderPage sliderPageOne = new SliderPage();
        sliderPageOne.setBgColor(Color.parseColor("#3CB6AA"));
        sliderPageOne.setImageDrawable(R.drawable.howtopatientfirst);
        sliderPageOne.setDescription(getResources().getString(R.string.howToPatientOneDescription));

        SliderPage sliderPageTwo = new SliderPage();
        sliderPageTwo.setBgColor(Color.parseColor("#E91E63"));
        sliderPageTwo.setImageDrawable(R.drawable.howtopatientsecond );
        sliderPageTwo.setDescription(getResources().getString(R.string.howToPatientTwoDescription));


        SliderPage sliderPageThree = new SliderPage();
        sliderPageThree.setBgColor(Color.parseColor("#FF5722"));
        sliderPageThree.setImageDrawable(R.drawable.howtopatientthird);
        sliderPageThree.setDescription(getResources().getString(R.string.howToPatientThreeDescription));


        SliderPage sliderPageForth = new SliderPage();
        sliderPageForth.setBgColor(Color.parseColor("#8BC34A"));
        sliderPageForth.setImageDrawable(R.drawable.howtopatientfourth);
        sliderPageForth.setDescription(getResources().getString(R.string.howToPatientForthDescription));

//        HowToPatientFragment fragment1 = new HowToPatientFragment();
//        HowToPatientSecondFragment fragment2 = new HowToPatientSecondFragment();
//        addSlide(fragment1);
//        addSlide(fragment2);

        addSlide(AppIntroFragment.newInstance(sliderPageOne));
        addSlide(AppIntroFragment.newInstance(sliderPageTwo));
        addSlide(AppIntroFragment.newInstance(sliderPageThree));
        addSlide(AppIntroFragment.newInstance(sliderPageForth));

//        HowToPatientFragment fragment = new HowToPatientFragment();
//        addSlide(fragment);

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#FFFFFF"));


        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setBarColor(Color.parseColor("#3F51B5"));

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        // Do something when users tap on Skip button.
        Intent patientListActivity = new Intent(getApplicationContext(), PatientShowRequestListActivity.class);
        startActivity(patientListActivity);
       // super.onSkipPressed(currentFragment);

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        // Do something when users tap on Done button.
        //super.onDonePressed(currentFragment);

        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_HOW_TO, MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(Constants.DISPLAY_HOW_TO_PATIENT, false);
        pendingEdits.apply();

        Intent patientListActivity = new Intent(getApplicationContext(), PatientShowRequestListActivity.class);
        startActivity(patientListActivity);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        if(newFragment != null) {
            ISlideBackgroundColorHolder appIntroFragment = (ISlideBackgroundColorHolder) newFragment;
            setBarColor(appIntroFragment.getDefaultBackgroundColor());
        }

        // Do something when the slide changes.
    }
}
