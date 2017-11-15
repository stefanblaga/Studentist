package com.mario22gmail.vasile.studentist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mario22gmail.vasile.studentist.account.ChangeAccountInfoActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUsePatientActivity;
import com.mario22gmail.vasile.studentist.howToPage.HowToUseStudent;
import com.mario22gmail.vasile.studentist.patient.PatientMainFragment;
import com.mario22gmail.vasile.studentist.student.StudentMainFragment;

import Helpers.Constants;
import Helpers.FirebaseLogic;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView userNameTextView;

    private Activity _thisActivity;

    @BindView(R.id.toolbar)
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main_navigation);
        ButterKnife.bind(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        if (FirebaseLogic.CurrentUser == null) {
            Constants.ShowErrorFragment(getSupportFragmentManager());
            return;
        }

        _thisActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Studentist");
        int white = ContextCompat.getColor(getApplicationContext(), R.color.white);
        toolbar.setTitleTextColor(white);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        userNameTextView = (TextView) header.findViewById(R.id.userNameNavigationDrawerTextView);

        GetRightFragment(FirebaseLogic.CurrentUser.role);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userNameTextView != null) {
            userNameTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void GetRightFragment(String userRole) {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (!Constants.IsNetworkAvailable((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Constants.DisplaySnackbarForInternetConnection(toolBar);
            return;
        }

        switch (userRole) {
            case "patient":
                PatientMainFragment patientMainFragment = new PatientMainFragment();
                fragmentManager.beginTransaction().add(R.id.frameLayoutNavigationDrawer, patientMainFragment).commit();
                break;
            case "student":
                StudentMainFragment studentMainFragment = new StudentMainFragment();
                fragmentManager.beginTransaction().add(R.id.frameLayoutNavigationDrawer, studentMainFragment).commit();
                break;
            default:
                Constants.ShowErrorFragment(fragmentManager);
        }
    }


    public void GetRightHowToPage(String userType) {
        switch (userType) {
            case "patient":
                Intent showHowToPagePatient = new Intent(getApplicationContext(), HowToUsePatientActivity.class);
                startActivity(showHowToPagePatient);
                break;
            case "student":
                Intent howToPageStudent = new Intent(getApplicationContext(), HowToUseStudent.class);
                startActivity(howToPageStudent);
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
            GetRightHowToPage(FirebaseLogic.CurrentUser.role);
        } else if (id == R.id.aboutAppMenuItem) {
            AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
            aboutDialogFragment.show(getSupportFragmentManager(), "about dialog");
        } else if (id == R.id.exitAppMenuItem) {
            finishAffinity();

        } else if (id == R.id.nav_share) {

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(getString(R.string.fb_page)))
                    .setQuote(getResources().getString(R.string.facebook_share_description))
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
            accountActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(accountActivity);
            finish();

        }
        item.setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
