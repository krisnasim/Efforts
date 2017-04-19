package com.android.efforts.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsResult;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    @BindView(R.id.splash_text) TextView splash_text;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3500;
    static final Integer LOCATION = 0x1;
    //static final Integer CALL = 0x2;
    static final Integer WRITE_EXST = 0x2;
    //static final Integer READ_EXST = 0x4;
    static final Integer CAMERA = 0x3;
    static final Integer ACCOUNTS = 0x6;
    static final Integer GPS_SETTINGS = 0x7;
    static final String TAG = "MainActivity";
    private boolean isLocationGranted = false;
    private boolean isMediaGranted = false;
    private boolean isCameraGranted = false;
    GoogleApiClient client;
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        //change typeface
        Typeface face= Typeface.createFromAsset(getAssets(), "MOAM91.otf");
        splash_text.setTypeface(face);

        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        //Log.d("onCreate", "creating splash activity");
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    isLocationGranted = true;
                    Log.d("locationGranted!", "location is granted!");
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                    break;
                //Write external Storage
                case 2:
                    isMediaGranted = true;
                    Log.d("mediaGranted!", "media is granted!");
                    askForPermission(Manifest.permission.CAMERA,CAMERA);
                    break;
                //Camera
                case 3:
                    isCameraGranted = true;
                    Log.d("cameraGranted!", "camera is granted!");
                    checkAllPermissions();
                    break;
            }
            //Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_DENIED){
            switch (requestCode) {
                //Location
                case 1:
                    showMessageOKCancel("Kami perlu akses lokasi anda untuk beberapa fitur dalam aplikasi Verificare. Mohon mengaktifkan agar aplikasi bisa berjalan dengan baik.", this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("testLocation", "Asking for location permission");
                            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
                        }
                    }, this);
                    break;
                //Write external Storage
                case 2:
                    showMessageOKCancel("Kami perlu akses data anda untuk dapat menulis data dari aplikasi Verificare. Mohon mengaktifkan agar aplikasi bisa berjalan dengan baik", this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("testExternal", "Asking for external permission");
                            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                        }
                    }, this);
                    break;
                //Camera
                case 3:
                    showMessageOKCancel("Kami perlu akses kamera anda untuk dapat mengambil gambar dengan aplikasi Verificare. Mohon mengaktifkan agar aplikasi bisa berjalan dengan baik.", this, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("testCamera", "Asking for camera permission");
                            askForPermission(Manifest.permission.CAMERA,CAMERA);
                        }
                    }, this);
                    break;
            }
        } else{
            //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            //Log.d("askPermission", "permission is not granted for "+permission);
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();

            //check for request code that has been accepted. check for those who are not accepted yet
            if(requestCode == LOCATION) {
                isLocationGranted = true;
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXST);
                Log.d("locationCheck", "location is granted!");
                checkAllPermissions();
            }

            if(requestCode == WRITE_EXST) {
                isMediaGranted = true;
                askForPermission(Manifest.permission.CAMERA,CAMERA);
                Log.d("locationCheck", "media external is granted!");
                checkAllPermissions();
            }

            if(requestCode == CAMERA) {
                isCameraGranted = true;
                Log.d("locationCheck", "camera is granted!");
                checkAllPermissions();
            }
        }
    }

    private void checkAllPermissions() {
        //check if three boolean are all true
        Log.d("isLocationGranted", String.valueOf(isLocationGranted));
        Log.d("isMediaGranted", String.valueOf(isMediaGranted));
        Log.d("isCameraGranted", String.valueOf(isCameraGranted));
        if (isLocationGranted && isMediaGranted && isCameraGranted) {
            callTimer();
        }
    }

    private static void showMessageOKCancel(String message, Activity activity, DialogInterface.OnClickListener okListener,
                                            DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(activity).setMessage(message).setPositiveButton("OK", okListener).create().show();
    }

    private void callTimer() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                //check for sharedPreferences before deciding which activity to go
                //checkforSharedPreferences();
                //instead call the intent here. dont check preferences first
                //Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                Intent intent = new Intent(SplashActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkforSharedPreferences() {
        SharedPreferences sharedPref = this.getSharedPreferences("userCred", Context.MODE_PRIVATE);
        String token = sharedPref.getString("jwt", "empty token");
        String level = sharedPref.getString("level", "empty level");
        //Log.d("tokenPrint", token);
        if(token.matches("empty token")) {
            //well, no user is logged in. go to login activity
            //Intent intent = new Intent(this, LoginActivity.class);
            //startActivity(intent);
            finish();
        } else {
            //get the expired timestamp in epoch
            String expired = sharedPref.getString("expired_timestamp", "empty");
            long expired_con = Long.parseLong(expired);
            long currentTime = System.currentTimeMillis() / 1000L;
            //currentTime = currentTime + 100000;
            Log.d("currentDate", String.valueOf(currentTime));
            Log.d("expiredDate", String.valueOf(expired_con));

            if(currentTime > expired_con) {
                //the token is expired. user must logged in again
                Toast.makeText(this, "Sesi anda telah habis. Silahkan login kembali", Toast.LENGTH_LONG).show();
                //clear all shared preferences first
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
                //get out from here
                //Intent intent = new Intent(this, LoginActivity.class);
                //startActivity(intent);
                finish();
            }
            else {
                //no login required. GO AWAY FROM HERE
                //but wait. check which level is the user. don't just throw them into wrong level
                if(level.equals("sales_representative") || level.equals("area_manager") || level.equals("promoter") || level.equals("admin")) {
                    //Intent intent = new Intent(this, HomeSRActivity.class);
                    //startActivity(intent);
                    finish();
                }
                else if (level.equals("merchandiser"))  {
                    //Intent intent = new Intent(this, HomeMDSActivity.class);
                    //startActivity(intent);
                    finish();
                }
            }
        }
    }
}
