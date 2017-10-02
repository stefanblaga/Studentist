package com.mario22gmail.vasile.studentist.student;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.AboutDialogFragment;
import com.mario22gmail.vasile.studentist.account.LoginActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.student.allRequestList.AllRequestsFragment;
import com.mario22gmail.vasile.studentist.student.studentRequests.StudentRequestsFragment;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.StudentUser;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentMainFragment extends Fragment {

    @BindView(R.id.studentActivityTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.studentListActivityViewPager)
    ViewPager mainViewPager;

    @BindView(R.id.userNameNavigationDrawerTextView)
    TextView UserNameNavTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mainView =  inflater.inflate(R.layout.activity_student_request_list, container, false);
        ButterKnife.bind(this, mainView);

        StudentListAdapter adapter = new StudentListAdapter(getFragmentManager());
        mainViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mainViewPager);



        String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().UpdateStudentProfileForAddingRequest(userUUID);

        return mainView;
    }

//
//    public void SetToolbarMenu(Toolbar toolbar)
//    {
//        Drawable threeDotsMenu = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_more_vert);
//        toolbar.setOverflowIcon(threeDotsMenu);
//        toolbar.inflateMenu(R.menu.patientdotsmenu);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.howToPagePatientMenuItem:
//                        Intent howToStartPatient = new Intent(getApplicationContext(), HowToUseStudent.class);
//                        startActivity(howToStartPatient);
//                        finish();
//                        return true;
//                    case R.id.aboutAppMenuItem:
//                        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
//                        aboutDialogFragment.show(getSupportFragmentManager(), "about dialog");
//                        return true;
//                    case R.id.signOutMenuItem:
//                        AuthUI.getInstance().signOut((FragmentActivity) thisContext).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivity(loginActivity);
//                                finish();
//                            }
//                        });
//                        return true;
//                    case R.id.exitAppMenuItem:
//                        finishAffinity();
//                        return true;
//                }
//                return true;
//            }
//        });
//    }

    class StudentListAdapter extends FragmentStatePagerAdapter {

        public StudentListAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        private String[] tabTitles = new String[]{"Lista pacienti", "Pacientii mei"};

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
