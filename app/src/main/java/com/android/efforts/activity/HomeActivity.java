package com.android.efforts.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.fragment.AttendanceFragment;
import com.android.efforts.fragment.CompetitorFragment;
import com.android.efforts.fragment.HomeFragment;
import com.android.efforts.fragment.NewsFragment;
import com.android.efforts.fragment.ReportFragment;
import com.android.efforts.fragment.SelloutFragment;
import com.android.efforts.fragment.TaskFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Response.Listener<JSONObject>, Response.ErrorListener {

    @BindView(R.id.dell_image_logo)
    ImageView dell_image_logo;
    @BindView(R.id.level_label)
    TextView level_label;

    private String qrCodeRes;
    private Runnable mRunnable;
    private boolean doubleBackClick;
    private Handler mHandler = new Handler();
    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFirstFragment();

        SharedPreferences sharedPref = getSharedPreferences("userCred", Context.MODE_PRIVATE);
        Log.d("sharedPref", sharedPref.getString("jwt", "NoToken"));
        String fullName = sharedPref.getString("full_name", "John Did");
        String email = sharedPref.getString("email", "nomail@reply.com");
        String token = sharedPref.getString("access_token", "noToken");

        //update label name and email
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_sr);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        assert navigationView != null;
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.full_name_menu);
        nav_user.setText(fullName);
        TextView nav_email = (TextView) hView.findViewById(R.id.email_menu);
        nav_email.setText(email);

        //update level label
        level_label.setText(sharedPref.getString("level", "No Level"));

        //iniaite runnable
        mRunnable = new Runnable() {
            @Override
            public void run() {
                doubleBackClick = false;
            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //if (doubleBackClick) {
        super.onBackPressed();
        //}

//        this.doubleBackClick = true;
//        Toast.makeText(this, "Mohon klik kembali sekali lagi untuk keluar dari applikasi", Toast.LENGTH_SHORT).show();
//        mHandler.postDelayed(mRunnable, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            //destory all handler
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //get the fragment manager here!
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new Fragment();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_dashboard) {
            fragment = new HomeFragment();
        }else if (id == R.id.nav_attendance) {
            fragment = new AttendanceFragment();
        } else if (id == R.id.nav_report) {
            fragment = new ReportFragment();
        } else if (id == R.id.nav_task) {
            fragment = new TaskFragment();
        } else if (id == R.id.nav_news) {
            fragment = new NewsFragment();
        } else if (id == R.id.nav_competitor) {
            fragment = new CompetitorFragment();
        } else if(id == R.id.nav_logout) {
            logout();
        }

        //finish the fragment transaction
        level_label.setVisibility(View.GONE);
        dell_image_logo.setVisibility(View.GONE);
        transaction.replace(R.id.home_main_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Log.d("QR CODE ACTIVITY", result.getContents());
                qrCodeRes = result.getContents();
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d("onResponse", "JSON Response: "+response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("onErrorResponse", "JSON Response: "+error);
    }

    private void logout() {
        //clear all shared preferences first
        SharedPreferences sharedPref = getSharedPreferences("userCred", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        //start intent, and kill the previous activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        //add some Toast to help undertand better
        Toast.makeText(this, "Kamu telah berhasil keluar", Toast.LENGTH_SHORT).show();
    }

    public String getQRCodeContent() {
        return qrCodeRes;
    }

    public void setFirstFragment() {
        //hide the item in layout
        level_label.setVisibility(View.GONE);
        dell_image_logo.setVisibility(View.GONE);

        boolean boolIntent = checkforBundle();
        Fragment fragment = new ReportFragment();
        Log.d("boolIntent", String.valueOf(boolIntent));
//        if(boolIntent) {
//            //fragment = new ForumFragment();
//            Log.d("whichFrag", "forum one");
//        }
//        else {
//            //fragment = new HomeFragment();
//            Log.d("whichFrag", "home one");
//        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.home_main_frame, fragment);
        transaction.commit();
    }

    public void changeFragment(Fragment fragment) {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.home_main_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean checkforBundle() {
        boolean value = false;
        // You can be pretty confident that the intent will not be null here.
        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("fragment")) {
                String fragment = extras.getString("fragment");
                if(fragment.equals("forum")) {
                    value = true;
                    Log.d("checkNotif", "IT IS TRUE");
                }
                else {
                    Log.d("checkNotif", "IT IS FALSE");
                }
            }
        }

        return value;
    }
}
