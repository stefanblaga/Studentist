package com.mario22gmail.vasile.studentist.HowToPage.Patient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.mario22gmail.vasile.studentist.Patient.PatientShowRequestListActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.StudentRequestListActivity;

import Helpers.Constants;

public class HowToUseStudent extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPageOne = new SliderPage();
        sliderPageOne.setBgColor(Color.parseColor("#3CB6AA"));
        sliderPageOne.setTitle("Bine ai venit !");
        sliderPageOne.setImageDrawable(R.drawable.howtostudentfirst);
        sliderPageOne.setDescription(getResources().getString(R.string.howToStudentOneDescription));

        SliderPage sliderPageTwo = new SliderPage();
        sliderPageTwo.setBgColor(Color.parseColor("#FF6E40"));
        sliderPageTwo.setTitle("Alege un pacient");
        sliderPageTwo.setImageDrawable(R.drawable.howtostudentsecond);
        sliderPageTwo.setDescription(getResources().getString(R.string.howToStudentTwoDescription));

        SliderPage sliderPageThree = new SliderPage();
        sliderPageThree.setBgColor(Color.parseColor("#9C27B0"));
        sliderPageThree.setTitle("Contacteaza pacientul");
        sliderPageThree.setImageDrawable(R.drawable.howtostudentthird);
        sliderPageThree.setDescription(getResources().getString(R.string.howToStudentThreeDescription));

        SliderPage sliderPageFourth = new SliderPage();
        sliderPageFourth.setBgColor(Color.parseColor("#03A9F4"));
        sliderPageFourth.setTitle("Nu-i lua pe toti !");
        sliderPageFourth.setImageDrawable(R.drawable.howtostudentforth);
        sliderPageFourth.setDescription(getResources().getString(R.string.howToStudentForthDescription));

        addSlide(AppIntroFragment.newInstance(sliderPageOne));
        addSlide(AppIntroFragment.newInstance(sliderPageTwo));
        addSlide(AppIntroFragment.newInstance(sliderPageThree));
        addSlide(AppIntroFragment.newInstance(sliderPageFourth));


        showSkipButton(true);
        setProgressButtonEnabled(true);
        setBarColor(Color.parseColor("#3CB6AA"));

        setSeparatorColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_HOW_TO, MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(Constants.DISPLAY_HOW_TO_STUDENT, false);
        pendingEdits.apply();

        Intent studentListActivity = new Intent(getApplicationContext(), StudentRequestListActivity.class);
        startActivity(studentListActivity);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        Intent studentListActivity = new Intent(getApplicationContext(), StudentRequestListActivity.class);
        startActivity(studentListActivity);
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
