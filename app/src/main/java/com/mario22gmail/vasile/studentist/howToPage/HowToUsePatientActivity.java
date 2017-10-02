package com.mario22gmail.vasile.studentist.howToPage;

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
import com.mario22gmail.vasile.studentist.MainNavigationActivity;
import com.mario22gmail.vasile.studentist.patient.PatientMainFragment;
import com.mario22gmail.vasile.studentist.R;

import Helpers.Constants;

public class HowToUsePatientActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SliderPage sliderPageOne = new SliderPage();
        sliderPageOne.setBgColor(Color.parseColor("#3CB6AA"));
        sliderPageOne.setTitle(getResources().getString(R.string.howToPatientPageOneTitle));
        sliderPageOne.setImageDrawable(R.drawable.howtopatientfirst);
        sliderPageOne.setDescription(getResources().getString(R.string.howToPatientOneDescription));

        SliderPage sliderPageTwo = new SliderPage();
        sliderPageTwo.setBgColor(Color.parseColor("#9C27B0"));
        sliderPageTwo.setTitle(getResources().getString(R.string.howToPatientPageTwoTitle));
        sliderPageTwo.setImageDrawable(R.drawable.howtopatientsecond );
        sliderPageTwo.setDescription(getResources().getString(R.string.howToPatientTwoDescription));


        SliderPage sliderPageThree = new SliderPage();
        sliderPageThree.setBgColor(Color.parseColor("#2196F3"));
        sliderPageThree.setTitle(getResources().getString(R.string.howToPatientPageThreeTitle));
        sliderPageThree.setImageDrawable(R.drawable.howtopatientthird);
        sliderPageThree.setDescription(getResources().getString(R.string.howToPatientThreeDescription));


        SliderPage sliderPageForth = new SliderPage();
        sliderPageForth.setBgColor(Color.parseColor("#8BC34A"));
        sliderPageForth.setTitle(getResources().getString(R.string.howToPatientPageFourTitle));
        sliderPageForth.setImageDrawable(R.drawable.howtopatientfourth);
        sliderPageForth.setDescription(getResources().getString(R.string.howToPatientForthDescription));

        addSlide(AppIntroFragment.newInstance(sliderPageOne));
        addSlide(AppIntroFragment.newInstance(sliderPageTwo));
        addSlide(AppIntroFragment.newInstance(sliderPageThree));
        addSlide(AppIntroFragment.newInstance(sliderPageForth));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#FFFFFF"));


        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setBarColor(Color.parseColor("#3F51B5"));

        setSkipText(getResources().getString(R.string.howToPageSkipText));
        setDoneText(getResources().getString(R.string.howToPageDoneText));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        // Do something when users tap on Skip button.
        Intent navActivity = new Intent(getApplicationContext(), MainNavigationActivity.class);
        navActivity.putExtra(Constants.UserTypeKey, "patient");
        startActivity(navActivity);

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        // Do something when users tap on Done button.
        //super.onDonePressed(currentFragment);

        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_HOW_TO, MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(Constants.DISPLAY_HOW_TO_PATIENT, false);
        pendingEdits.apply();

        Intent navActivity = new Intent(getApplicationContext(), MainNavigationActivity.class);
        navActivity.putExtra(Constants.UserTypeKey, "patient");
        startActivity(navActivity);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        if(newFragment != null) {
            ISlideBackgroundColorHolder appIntroFragment = (ISlideBackgroundColorHolder) newFragment;
            setBarColor(appIntroFragment.getDefaultBackgroundColor());
        }
    }
}
