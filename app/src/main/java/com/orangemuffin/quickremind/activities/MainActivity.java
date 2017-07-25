package com.orangemuffin.quickremind.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.tabmanagers.ReminderManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager mFragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Calendar calendar = Calendar.getInstance();
        String pattern = "EEE, dd MMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        String today = dateFormat.format(calendar.getTime());
        getSupportActionBar().setTitle("Today is " + today);

        mFragmentManager = getSupportFragmentManager();
        fragmentTransaction = mFragmentManager.beginTransaction();

        ReminderManager reminderManager = new ReminderManager();

        //set up fragment when coming from notification
        Bundle data = new Bundle();
        if (getIntent().getBooleanExtra("switchPage", false)) {
            data.putBoolean("switchPage", true);
        } else {
            data.putBoolean("switchPage", false);
        }
        reminderManager.setArguments(data);

        fragmentTransaction.add(R.id.containerView, reminderManager).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReminderAddActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.anim_stay);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left, R.anim.anim_stay);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
