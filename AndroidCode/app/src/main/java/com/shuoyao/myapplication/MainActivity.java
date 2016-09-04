package com.shuoyao.myapplication;

import android.app.Activity;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.job.JobScheduler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    //private Tasks t1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        JobScheduler mJobScheduler = (JobScheduler) getSystemService( getApplicationContext().JOB_SCHEDULER_SERVICE );
        JobInfo.Builder builder = new JobInfo.Builder( 1,
                new ComponentName( getPackageName(),
                        UpdateFreeTimes.class.getName()) );
        JobInfo.Builder builder2 = new JobInfo.Builder( 2,
                new ComponentName( getPackageName(),
                        CheckManualFrequency.class.getName() ) );
        builder.setPeriodic(60*1000);
        builder2.setPeriodic(60*1000);
        if (mJobScheduler.schedule(builder.build()) <= 0 || mJobScheduler.schedule(builder2.build()) <= 0) {
            Log.e(TAG, "Build Not Working");
        }

        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    /*
        if (t1 == null)  {
            t1 = new Tasks(MainActivity.this);
            t1.execute();
        }
        */
        if (!CircleApp.isLoggedIn()) {
            /* Redirect to login page */
            ParseUser.logOut();

            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Notif"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));

        tabLayout.getTabAt(0).setIcon(R.drawable.people);
        tabLayout.getTabAt(1).setIcon(R.drawable.notification);
        tabLayout.getTabAt(2).setIcon(R.drawable.rewind);
        tabLayout.getTabAt(3).setIcon(R.drawable.settings);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(4);




        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabselected position is :" + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }




    @Override
    protected void onResume() {
        super.onResume();

        if (!CircleApp.isLoggedIn()) {
            /* Redirect to login page */
            ParseUser.logOut();

            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!CircleApp.isLoggedIn()) {
            /* Redirect to login page */
            ParseUser.logOut();

            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!CircleApp.isLoggedIn()) {
            /* Redirect to login page */
            ParseUser.logOut();

            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
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
            Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.help) {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}