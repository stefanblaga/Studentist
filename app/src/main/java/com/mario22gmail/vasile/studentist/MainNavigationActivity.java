package com.mario22gmail.vasile.studentist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mario22gmail.vasile.studentist.account.ChangeAccountInfoActivity;
import com.mario22gmail.vasile.studentist.account.LoginActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.patient.PatientMainFragment;
import com.mario22gmail.vasile.studentist.student.StudentMainFragment;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import Helpers.StudentUser;
import Helpers.UserApp;
import StudentComponent.StudentRequest;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.SwitchRoles)
    Switch swithRoles;

    TextView userNameTextView;


    private String _userType = "";
    private Activity _thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        ButterKnife.bind(this);
        _thisActivity = this;
        _userType = getIntent().getStringExtra(Constants.UserTypeKey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Studentist");
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);
        setSupportActionBar(toolbar);


        swithRoles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final DatabaseReference userTable = FirebaseLogic.getInstance().GetUserTableReference();
                    userTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserApp user = dataSnapshot.getValue(UserApp.class);
                            if (user != null) {
                                if (_userType.equals("patient")) {
                                    user.role = "student";
                                    userTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                                } else {
                                    StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                                    studentUser.NumberOfRequest = 0;
                                    studentUser.role = "patient";
                                    userTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(studentUser);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            Constants.ShowErrorFragment(getSupportFragmentManager());

        View header = navigationView.getHeaderView(0);
/*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        userNameTextView = (TextView) header.findViewById(R.id.userNameNavigationDrawerTextView);

        GetRightFragment(_userType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userNameTextView != null) {
            userNameTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
    }

    public void GetRightFragment(String userType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (userType) {
            case "patient":
                PatientMainFragment patientMainFragment = new PatientMainFragment();
                fragmentManager.beginTransaction().add(R.id.frameLayoutNavigationDrawer, patientMainFragment).commit();
                break;
            case "student":
                StudentMainFragment studentMainFragment = new StudentMainFragment();
                fragmentManager.beginTransaction().add(R.id.frameLayoutNavigationDrawer, studentMainFragment).commit();
                break;
            default:
                Constants.ShowErrorFragment(getSupportFragmentManager());
        }
    }


    public void GetRightHowToPage(String userType) {
        switch (userType) {
            case "patient":
                Intent showHowToPagePatient = new Intent(getApplicationContext(), HowToUsePatientActivity.class);
                startActivity(showHowToPagePatient);
                finish();
                break;
            case "student":
                Intent howToPageStudent = new Intent(getApplicationContext(), HowToUseStudent.class);
                startActivity(howToPageStudent);
                finish();
                break;
            default:
                Constants.ShowErrorFragment(getSupportFragmentManager());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.howToPagePatientMenuItem) {
            GetRightHowToPage(_userType);
        } else if (id == R.id.aboutAppMenuItem) {
            AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
            aboutDialogFragment.show(getSupportFragmentManager(), "about dialog");
        } else if (id == R.id.signOutMenuItem) {
            AuthUI.getInstance().signOut((FragmentActivity) _thisActivity).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            });

        } else if (id == R.id.exitAppMenuItem) {
            finishAffinity();

        } else if (id == R.id.nav_share) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(getString(R.string.fb_page)))
                    .build();
            ShareDialog.show(this, content);
        } else if (id == R.id.nav_fb) {
            PackageManager packageManager = this.getPackageManager();
            Uri uri = Uri.parse(this.getString(R.string.fb_page));
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.facebook.katana", 0);
                if (applicationInfo.enabled) {
                    uri = Uri.parse("fb://facewebmodal/f?href=" + uri);
                }
            } catch (PackageManager.NameNotFoundException ignored) {

            }
            Intent fbIntent = new Intent(Intent.ACTION_VIEW, uri);
            this.startActivity(fbIntent);

        } else if (id == R.id.nav_gmail) {
            String link = getResources().getString(R.string.gmail_account);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", link, null));
            startActivity(emailIntent);
        } else if (id == R.id.appSettingsMenuItem) {
            Intent accountActivity = new Intent(getApplicationContext(), ChangeAccountInfoActivity.class);
            startActivity(accountActivity);

        }
        item.setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}