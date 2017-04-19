package com.android.efforts.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.customclass.CustomJSONObjectRequest;
import com.android.efforts.customclass.JWTUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AccountAuthenticatorActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progressDialog;

    @BindView(R.id.text_logo_login) TextView text_logo_login;
    @BindView(R.id.input_email) TextView input_email;
    @BindView(R.id.input_password) TextView input_pwd;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.link_signup) TextView link_signup;
    @BindView(R.id.link_skip_login) TextView link_skip_login;

    //set click listener to login button
    @OnClick(R.id.btn_login)
    public void clickToLogin(View view) {
        login();
    }

    //set click listener to signup textview
    @OnClick(R.id.link_signup)
    public void clickToSignup(View view) {
        //starting the signup activity
    }

    @OnClick(R.id.link_skip_login)
    public void skipLogin() {
        //starting the home activity
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        link_signup.setVisibility(View.GONE);
        //link_skip_login.setVisibility(View.GONE);
        Typeface face= Typeface.createFromAsset(getAssets(), "MOAM91.otf");
        text_logo_login.setTypeface(face);

        checkforSharedPreferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.dismiss();
        Log.d("errorResponse", String.valueOf(error));
        onLoginFailed();
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialog.dismiss();
        String bodyResult = "";
        try {
            Log.d("onResponse", response.toString(2));
            bodyResult = JWTUtils.decoded(String.valueOf(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d("jsonPreview", "check JSON: "+bodyResult);
            JSONObject jsonObj = new JSONObject(bodyResult);

            Log.d("JSON", jsonObj.getString("exp"));
            Log.d("JSON", jsonObj.getString("created"));
            Log.d("JSON", jsonObj.getString("sub"));
            Log.d("JSON", jsonObj.getString("username"));
            Log.d("JSON", jsonObj.getString("name"));
            Log.d("JSON", jsonObj.getString("email"));
            Log.d("JSON", jsonObj.getString("level"));

            SharedPreferences sharedPref = getSharedPreferences("userCred", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("jwt", response.getString("jwt"));
            editor.putString("expired_timestamp", jsonObj.getString("exp"));
            editor.putString("created_timestamp", jsonObj.getString("created"));
            editor.putString("primary_key", jsonObj.getString("sub"));
            editor.putString("username", jsonObj.getString("username"));
            editor.putString("full_name", jsonObj.getString("name"));
            editor.putString("email", jsonObj.getString("email"));
            editor.putString("level", jsonObj.getString("level"));

            editor.apply();

            Toast.makeText(this, "Welcome, "+ jsonObj.getString("name") +"!", Toast.LENGTH_SHORT).show();
            //send level string to throw into different home activity
            String levelUser = jsonObj.getString("level");
            onLoginSuccess(levelUser);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tryWiseServer(String email, String pwd) throws JSONException {
        String url = "https://dell-api.verifi.care/login";

        Log.d("WiseServer", "Entering the Wisely Server. Kidding, it's AWS. Oh wait, now it's Heroku..");
        Log.d("WiseServer", url);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        HashMap<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password_digest", pwd);

        JSONObject insideJsonObj = new JSONObject();
        insideJsonObj.put("username", email);
        insideJsonObj.put("password_digest", pwd);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("auth", insideJsonObj);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomJSONObjectRequest customJSONReq = new CustomJSONObjectRequest(Request.Method.POST, url, jsonObj, this, this);

        try {
            //Map<String, String> testH = jsObjRequest.getHeaders();
            //Log.d("headers",testH.get("Content-Type"));
            Log.d("headers", String.valueOf(customJSONReq.getHeaders()));
            Log.d("content", jsonObj.toString(2));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        requestQueue.add(customJSONReq);
    }

    private void loginWithOAuthNX(String email, String pwd) throws JSONException, UnsupportedEncodingException {
        String url = "https://id.nx.tsun.moe/oauth/token";

        //convert both clientID and clientSecret into Base64
        String clientID = "07fbb8e4-8caa-4b91-a7f6-1db581164c9f";
        String clientSecret = "qV60eBr0qGw3tbSvJ2kl86AH";
        String combinedClient = clientID+":"+clientSecret;
        byte[] byteArray = combinedClient.getBytes("UTF-8");
        //convert bytes to Base64
        String base64Result = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.d("NXServer", "Entering NX Platform...");
        Log.d("NXServer", url);

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Basic "+base64Result);

        HashMap<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", pwd);
        params.put("grant_type", "authorization_code");

        //JSONObject insideJsonObj = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("username", email);
        jsonObj.put("password_digest", pwd);

        //JSONObject jsonObj = new JSONObject();
        //jsonObj.put("auth", insideJsonObj);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomJSONObjectRequest customJSONReq = new CustomJSONObjectRequest(Request.Method.POST, url, jsonObj, this, this);

        try {
            //Map<String, String> testH = jsObjRequest.getHeaders();
            //Log.d("headers",testH.get("Content-Type"));
            Log.d("headers", String.valueOf(customJSONReq.getHeaders()));
            Log.d("content", jsonObj.toString(2));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        requestQueue.add(customJSONReq);
    }

    private void login() {
        Log.d(TAG, "Calling the Login function");

        btn_login.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.CustomDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.show();

        final String email = input_email.getText().toString();
        final String password = input_pwd.getText().toString();

        try {
            //tryWiseServer(email, password);
            loginWithOAuthNX(email, password);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void createAccount(String email, String password, String authToken) {
        Account account = new Account(email, "YOUR ACCOUNT TYPE");

        AccountManager am = AccountManager.get(this);
        am.addAccountExplicitly(account, password, null);
        am.setAuthToken(account, "full_access", authToken);
    }

    public void onLoginSuccess(String level) {
        btn_login.setEnabled(true);

        //check which level user is
        if(level.equals("sales_representative") || level.equals("area_manager") || level.equals("promoter") || level.equals("admin")) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
//        else if(level.equals("area_manager")) {
//            Intent intent = new Intent(getApplicationContext(), HomeAMActivity.class);
//            startActivity(intent);
//            finish();
//        }
        else if(level.equals("merchandiser")) {
            //Intent intent = new Intent(getApplicationContext(), HomeMDSActivity.class);
            //startActivity(intent);
            finish();
        }
//          else if(level.equals("promoter")) {
//            Intent intent = new Intent(getApplicationContext(), HomePTActivity.class);
//            startActivity(intent);
//            finish();
//        } else if(level.equals("admin")) {
//            Intent intent = new Intent(getApplicationContext(), HomeSRActivity.class);
//            startActivity(intent);
//            finish();
//        }
        else {
            Toast.makeText(this, "Mohon maaf, tapi level pengguna anda tidak sesuai. Mohon cek kembali kepada admin untuk pemasangan level pengguna anda", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Username atau password yang anda masukan tidak cocok. Mohon cek kembali user dan password saat login", Toast.LENGTH_LONG).show();
        btn_login.setEnabled(true);
    }

    private void checkforSharedPreferences() {
        SharedPreferences sharedPref = this.getSharedPreferences("userCred", Context.MODE_PRIVATE);
        String token = sharedPref.getString("jwt", "empty token");
        String level = sharedPref.getString("level", "empty level");
        //Log.d("tokenPrint", token);
        if(token.matches("empty token")) {
            //well, do nothing. no user is logged in
        } else {
            //no login required. GO AWAY FROM HERE
            //but wait. check which level is the user. don't just throw them into wrong level
            if(level.equals("sales_representative") || level.equals("area_manager") || level.equals("promoter") || level.equals("admin")) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
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
