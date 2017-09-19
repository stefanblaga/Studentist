package com.mario22gmail.vasile.studentist.Student;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.HowToPage.Patient.HowToUseStudent;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.Student.AllRequestList.AllRequestsFragment;
import com.mario22gmail.vasile.studentist.Student.StudentRequests.StudentRequestsFragment;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentRequestListActivity extends AppCompatActivity {

    @BindView(R.id.studentActivityTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.studentListActivityViewPager)
    ViewPager mainViewPager;


    @BindView(R.id.toolbarRequestsForStudentsList)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_request_list);
        ButterKnife.bind(this);

        StudentListAdapter adapter = new StudentListAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mainViewPager);

        String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().UpdateStudentProfileForAddingRequest(userUUID);

        Drawable threeDotsMenu = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert);
        toolbar.setOverflowIcon(threeDotsMenu);
        toolbar.inflateMenu(R.menu.patientdotsmenu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.howToPagePatientMenuItem:
                        Intent howToStartPatient = new Intent(getApplicationContext(), HowToUseStudent.class);
                        startActivity(howToStartPatient);
                        finish();
                        return true;
                }
                return true;
            }
        });
    }

    class StudentListAdapter extends FragmentStatePagerAdapter {

        public StudentListAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        private String[] tabTitles = new String[]{"Lista cereri", "Cererile mele"};

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new AllRequestsFragment();
            }
            if (position == 1) {
                return new StudentRequestsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
