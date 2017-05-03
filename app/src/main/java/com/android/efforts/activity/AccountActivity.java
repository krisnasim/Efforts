package com.android.efforts.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.efforts.R;
import com.android.efforts.customclass.CustomRequest;
import com.android.efforts.fragment.ReportFragment;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    @BindView(R.id.code_text_value) TextView code_text_value;
    @BindView(R.id.home_button) TextView home_button;

    private String url_Code = "";

    @OnClick(R.id.home_button)
    public void linkToHome() {
        Intent intent = new Intent(AccountActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        getBundleData();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("onErrorResponse", "JSON Response: " + error);
        Log.d("onErrorResponse", "JSON Error: "+error.getLocalizedMessage());
        Log.d("onErrorResponse", "JSON Error: "+error.getMessage());
        code_text_value.setText("Login Failed");
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            Log.d("onResponse", "JSON Response: " + response.toString(2));
            //JSONObject storeObj = response.getJSONObject("data").getJSONObject("store");
            Log.d("access_token", response.get("access_token").toString());
            Log.d("refresh_token", response.get("refresh_token").toString());
            Log.d("expires_in", response.get("expires_in").toString());
            long expiredTime = getExpiredTime(response.get("expires_in").toString());
            code_text_value.setText("Login Success!");

            SharedPreferences sharedPref = getSharedPreferences("userCred", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //enter the data from oauth token exchange
            editor.putString("access_token", response.get("access_token").toString());
            editor.putString("refresh_token", response.get("refresh_token").toString());
            editor.putLong("expires_in", expiredTime);
            //save it
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBundleData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d("url_code", bundle.getString("url_code"));
        url_Code = bundle.getString("url_code");
        Log.d("urlCode", url_Code);
        requestOAuthToken();
        //code_text_value.setText(url_Code);
    }

    private void requestOAuthToken() {
        String url = "https://id.nx.tsun.moe/oauth/token";
        String token = "";
        try {
            token = requestAuthorization();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //set headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", token);
        //set params
        HashMap<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", url_Code);
        params.put("redirect_uri", "https://android.efforts.trd.client.nx.tsun.moe/oauth/callback");
        params.put("client_id", "07fbb8e4-8caa-4b91-a7f6-1db581164c9f");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

    private String requestAuthorization() throws UnsupportedEncodingException {
        //prepare the clientID and clientSecret
        String clientID = getResources().getString(R.string.client_id);
        String clientSecret = getResources().getString(R.string.client_secret);
        String combinedKey = clientID+":"+clientSecret;

        //convert the combinedKey to Base64
        byte[] convertedKey = combinedKey.getBytes("UTF-8");
        String encodedKey = Base64.encodeToString(convertedKey, Base64.NO_WRAP);
        Log.d("encoded64Res", encodedKey);
        String finalKey = "Basic "+encodedKey;

        return finalKey;
    }

    private long getExpiredTime(String value) {
        long currentTime = System.currentTimeMillis();
        Log.d("currentTimeMilis", String.valueOf(currentTime));
        long intervalInSeconds = Long.valueOf(value);
        Log.d("intervalInSeconds", String.valueOf(intervalInSeconds));
        long intervalInMiliSeconds = intervalInSeconds * 1000;
        Log.d("intervalInMiliSeconds", String.valueOf(intervalInMiliSeconds));
        long expireTimeInMilis = currentTime + intervalInMiliSeconds;
        Log.d("expireTimeInMilis", String.valueOf(expireTimeInMilis));

        return expireTimeInMilis;
    }
}
