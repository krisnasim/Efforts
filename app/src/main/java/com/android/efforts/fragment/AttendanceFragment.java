package com.android.efforts.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.activity.QRCodeActivity;
import com.android.efforts.customclass.CustomRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AttendanceFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String qrCodeRes;
    private Location currentLocation;
    private LocationManager locManager;
    private LocationListener locListener;
    private SharedPreferences sharedPref;
    private ProgressDialog progressDialog;

    // LogCat tag
    //private static final String TAG = MainActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @BindView(R.id.qr_code_button)
    Button qr_code_button;
    @BindView(R.id.qr_code_result)
    TextView qr_code_result;
    @BindView(R.id.submitAttendanceButton) Button submitAtt;
    @BindView(R.id.attendance_type)
    Spinner attendance_type;
    @BindView(R.id.remark_attendance_input)
    EditText remark_attendance_input;

    @OnClick(R.id.location_button)
    public void findLocation() {
        displayLocation();
    }

    @OnClick(R.id.submitAttendanceButton)
    public void submitAttendance() {
//        if(currentLocation == null) {
        if(mLastLocation == null) {
            //cannot proceed if location can't get latitude and longitude
            Toast.makeText(getActivity(), "Mohon tunggu sementara kami mengumpulkan informasi lokasi Anda", Toast.LENGTH_SHORT).show();
        }
        else {
            if(qrCodeRes == null) {
                //cannot proceed if qr code has not been scanned yet
                Toast.makeText(getActivity(), "Kami tidak bisa mengirim data karena anda belum memindai QR Code toko. Silahkan pindai terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Mohon tunggu...");
                progressDialog.show();

                sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
                Log.d("SharedPref", sharedPref.getString("jwt", "NoToken"));
                String token = sharedPref.getString("jwt", "empty token");
                String url = getString(R.string.create_attendance);
                int position = attendance_type.getSelectedItemPosition();
                Log.d("attendancePos", String.valueOf(position));

                if(position == 1) {
                    position = 2;
                }
                else if(position == 2) {
                    position = 3;
                }
                else if(position == 3) {
                    position = 0;
                }
                else if(position == 0) {
                    position = 1;
                }

                //set headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", token);
                //set params
                HashMap<String, String> params = new HashMap<>();
                params.put("absence_type", position+"");
                //params.put("latitude", currentLocation.getLatitude()+"");
                //params.put("longitude", currentLocation.getLongitude()+"");
                params.put("latitude", mLastLocation.getLatitude()+"");
                params.put("longitude", mLastLocation.getLongitude()+"");
                params.put("store_uid", qrCodeRes);
//                params.put("store_uid", "STR-139");
                if(!remark_attendance_input.getText().toString().matches("")) {
                    //this condition is to check whether the text box is filled or not
                    params.put("remark", remark_attendance_input.getText().toString());
                    Log.d("Attendance", "the remark column is filled");
                } else {
                    Log.d("Attendance", "the remark column is empty");
                }

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, this, this);
                jsObjRequest.setHeaders(headers);

                try {
                    Map<String, String> test = jsObjRequest.getHeaders();
                    Log.d("headers", test.get("Content-Type"));
                    Log.d("headers", test.get("Authorization"));
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
                //send the request
                requestQueue.add(jsObjRequest);
            }
        }
    }

    @OnClick(R.id.qr_code_button)
    public void scanQRCode() {
        Intent intent = new Intent(getActivity(), QRCodeActivity.class);
        intent.putExtra("PROMPT_MESSAGE", "test");
        startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
    }

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        ButterKnife.bind(this, view);
        //set title
        getActivity().setTitle(R.string.title_attendance_fragment);
        //Toast.makeText(getActivity(), "BANGUN DARI TIDUR", Toast.LENGTH_SHORT).show();

        //set location manager and stuffs
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //initiate location listener
        initiateLocationListener();

        //////////////////////////////////////////////////////
        //trying to implement new version of location getter//
        //////////////////////////////////////////////////////
        // First we need to check availability of play services
        if (checkPlayServices()) {
            //Toast.makeText(getActivity(), "Play Services is available!", Toast.LENGTH_SHORT).show();
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        // Show location button click listener
//        btnShowLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayLocation();
//            }
//        });


        //set new adapter
        ArrayAdapter arrays = ArrayAdapter.createFromResource(getActivity(), R.array.attendance, R.layout.spinner_item);
        arrays.setDropDownViewResource(R.layout.spinner_layout);
        attendance_type.setAdapter(arrays);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        //Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(locManager != null) {
            locManager.removeUpdates(locListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(locManager != null) {
            locManager.removeUpdates(locListener);
        }

        //Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //re-initiate the listener
        initiateLocationListener();
        checkPlayServices();
        //displayLocation();

        //Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Once connected with google api, get the location
        //displayLocation();
        //Toast.makeText(getActivity(), "4", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("uhhWow", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        Toast.makeText(getActivity(), "5", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.dismiss();
        Log.d("onErrorResponse", "JSON Response: " + error);
        Log.d("onErrorResponse", "JSON Error: "+error.getLocalizedMessage());
        Log.d("onErrorResponse", "JSON Error: "+error.getMessage());
    }

    @Override
    public void onResponse(JSONObject response) {
        //set string var for store id
        String store_id = "";
        progressDialog.dismiss();
        try {
            Log.d("onResponse", "JSON Response: " + response.toString(2));
            JSONObject storeObj = response.getJSONObject("data").getJSONObject("store");
            Log.d("storeObj", storeObj.getString("id"));
            store_id = storeObj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //add store id to shared preferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("store_id", store_id);
        editor.apply();
        Toast.makeText(getActivity(), "Absen berhasil!", Toast.LENGTH_LONG).show();
        //change fragment
        String level = sharedPref.getString("level", "NoLevel");
        //remove location updates
        if(locManager != null) {
            locManager.removeUpdates(locListener);
        }
        if(level.equals("sales_representative") || level.equals("area_manager") || level.equals("promoter") || level.equals("admin")) {
            //HomeActivity act = (HomeActivity) getActivity();
            //act.changeFragment(new HomeFragment());
        } else if(level.equals("merchandiser")) {
            //HomeMDSActivity act = (HomeMDSActivity) getActivity();
            //act.changeFragment(new HomeMDSFragment());
        }
//        HomeSRActivity act = (HomeSRActivity) getActivity();
//        act.changeFragment(new HomeFragment());

//        Intent intent = new Intent(getActivity(), HomeSRActivity.class);
//        startActivity(intent);
//        getActivity().finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //set location manager for updating interval
                    locManager.requestLocationUpdates("gps", 1000, 1000, locListener);
                    displayLocation();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //this code is for QRCode retrieving result
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        qrCodeRes = result.getContents();
        qr_code_result.setText(qrCodeRes);
        //Toast.makeText(getActivity(), "the content is "+resultString, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        //Toast.makeText(getActivity(), "checking for play service availability...", Toast.LENGTH_SHORT).show();
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI
                .isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(getActivity(), resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();

                //Toast.makeText(getActivity(), "interesting. what is this?", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                //finish();
            }
            return false;
        }
        //Toast.makeText(getActivity(), "services is true", Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(getActivity(), "build Google API", Toast.LENGTH_SHORT).show();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        createLocationRequest();
    }

    /**
     * Starting the location updates
     * */
//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        boolean locGPS = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // Return a boolean
        boolean locNet = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // Return a boolean

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            //Toast.makeText(getActivity(), "Latitude: "+latitude+", Longitude: "+longitude, Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Lokasi telah ditemukan", Toast.LENGTH_SHORT).show();
            //lblLocation.setText(latitude + ", " + longitude);
        } else if(locGPS && locNet) {
            //lblLocation.setText("(Couldn't get the location. Make sure location is enabled on the device)");
            //Toast.makeText(getActivity(), "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), "Anda harus menyalakan fitur gps!", Toast.LENGTH_SHORT).show();

            ////////////////////////////////
            //there seems to be a bug here//
            ////////////////////////////////
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        //Toast.makeText(getActivity(), "create location request", Toast.LENGTH_SHORT).show();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void initiateLocationListener() {
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LocationGPS", "Latitude: "+location.getLatitude()+", Longitude: "+location.getLongitude());
                currentLocation = location;
                //displayLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                //displayLocation();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("NoGPS", "Need to turn on the GPS");
                Toast.makeText(getContext(), "Anda harus menyalakan fitur gps!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        //check for location permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
        }
        else {
            //set location manager for updating interval
            locManager.requestLocationUpdates("gps", 5000, 1000, locListener);
        }
    }
}
