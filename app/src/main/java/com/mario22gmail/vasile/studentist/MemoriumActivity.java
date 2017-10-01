package com.mario22gmail.vasile.studentist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import Helpers.Constants;

public class MemoriumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorium);
        final SharedPreferences sp = getSharedPreferences(Constants.DISPLAY_Memorium, MODE_PRIVATE);
        final SharedPreferences.Editor pendingEdits = sp.edit().putBoolean(Constants.DISPLAY_MemoriumBool, false);
        pendingEdits.apply();
        final int interval = 5000; // 1 Second
         Handler handler = new Handler();
         Runnable runnable = new Runnable(){
            public void run() {
                Intent startActivity = new Intent(getApplicationContext(), StartActivity.class);
                startActivity(startActivity);
                finish();
            }
        };
        handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        handler.postDelayed(runnable, interval);
    }
}
