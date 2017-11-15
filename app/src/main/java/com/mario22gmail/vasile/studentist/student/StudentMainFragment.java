package com.mario22gmail.vasile.studentist.student;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mario22gmail.vasile.studentist.account.CreateProfileActivity;
import com.mario22gmail.vasile.studentist.R;
import com.mario22gmail.vasile.studentist.student.allRequestList.AllRequestsFragment;
import com.mario22gmail.vasile.studentist.student.studentRequests.StudentRequestsFragment;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.UserApp;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentMainFragment extends Fragment {

    @BindView(R.id.studentActivityTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.studentListActivityViewPager)
    ViewPager mainViewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mainView = inflater.inflate(R.layout.activity_student_request_list, container, false);
        ButterKnife.bind(this, mainView);

        StudentListAdapter adapter = new StudentListAdapter(getFragmentManager());
        mainViewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(mainViewPager);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return null;
        }

        if (FirebaseLogic.CurrentUser == null) {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return null;
        }

        String userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseLogic.getInstance().UpdateStudentProfileForAddingRequest(userUUID);
        CheckIfUserHasNameAndPhone();
        return mainView;
    }

    public void CheckIfUserHasNameAndPhone() {
        if (FirebaseLogic.CurrentUser == null) {
            Constants.ShowErrorFragment(getActivity().getSupportFragmentManager());
            return;
        }

        UserApp currentUser = FirebaseLogic.CurrentUser;
        if (currentUser.name == null || currentUser.name.equals("") ||
                currentUser.telephoneNumber == null || currentUser.telephoneNumber.equals("")) {
            Intent finishStudentProfile = new Intent(getContext(), CreateProfileActivity.class);
            getActivity().startActivity(finishStudentProfile);
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckIfUserHasNameAndPhone();
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
