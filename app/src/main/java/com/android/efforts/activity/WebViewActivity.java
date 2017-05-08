package com.android.efforts.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.customclass.CustomRequest;
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

public class WebViewActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    @BindView(R.id.oauth_login_web_view) WebView oauth_login_web_view;

    private Uri uri;
    private String url_Code = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);

        //create the webview and URL here
        String url = "https://id.nx.tsun.moe/oauth/authorize";
        String response_type = "response_type=code";
        String redirect_uri = "redirect_uri=https://android.efforts.trd.client.nx.tsun.moe/oauth/callback";
        String client_id = "client_id=07fbb8e4-8caa-4b91-a7f6-1db581164c9f";
        final String final_url = url+"?"+response_type+"&"+redirect_uri+"&"+client_id;

        //dont think need javascript to run, for now...
        oauth_login_web_view.setInitialScale(1);
        oauth_login_web_view.getSettings().setUseWideViewPort(true);
        //need javascript because for responsive using bootstrap
        //oauth_login_web_view.getSettings().setJavaScriptEnabled(true);
        oauth_login_web_view.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Here put your code
                //Log.d("My Webview", final_url);
                //Uri uri = request.get
                Uri uri = request.getUrl();

                Log.d("OverrideURL", "YOOHOO");
                if(uri.getScheme().equals("https") && uri.getHost().equals("android.efforts.trd.client.nx.tsun.moe") && uri.getPath().equals("/oauth/callback")) {
                    Log.d("URL Catch", "Entered URL loading block");

                    url_Code = uri.getQueryParameter("code");

                    requestOAuthToken();
                    return true;
                }

                //Indicates WebView to NOT load the url
                //return true;
                //Allow WebView to load url
                return false;

                //return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("URL Loading", "entering depreceated function");

                Uri uri = Uri.parse(url);
                Log.d("OverrideURL", "YOOHOO 2");
                if(uri.getScheme().equals("https") && uri.getHost().equals("android.efforts.trd.client.nx.tsun.moe") && uri.getPath().equals("/oauth/callback")) {
                    Log.d("URL Catch", "Entered depreceated URL loading block");

                    Log.d("URI", uri.toString());
                    Log.d("Get URI path", uri.getScheme());
                    Log.d("Get URI path", uri.getHost());
                    Log.d("Get URI path", uri.getPath());
                    Log.d("Last URI", uri.getLastPathSegment());
                    Log.d("value URI", uri.getQueryParameter("code"));

//                    Bundle bundle = new Bundle();
//                    bundle.putString("url_code", uri.getQueryParameter("code"));
                    url_Code = uri.getQueryParameter("code");

                    requestOAuthToken();

//                    Intent intent = new Intent(WebViewActivity.this, AccountActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);

                    //stop loading the view
                    view.stopLoading();
                    return true;
                }

                //return super.shouldOverrideUrlLoading(view, url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //super.onPageStarted(view, url, favicon);
                // Here put your code
                Log.d("My Webview started", url);
                uri = Uri.parse(url);
                Log.d("Get URI path", uri.getScheme());
                Log.d("Get URI path", uri.getHost());
                //Log.d("Get URI path", uri.getEncodedPath());
                //Log.d("Get URI path", uri.getAuthority());
                Log.d("Get URI path", uri.getPath());
                if(uri.getScheme().equals("https") && uri.getHost().equals("android.efforts.trd.client.nx.tsun.moe") && uri.getPath().equals("/oauth/callback")) {
                    Log.d("URL Catch", "catched an URL!");
                }
            }
        });

        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    //haven't done shit here. yet
                }
            });
        } else {
            cookieManager.removeAllCookie();
        }

        //cookieManager.setAcceptCookie(false);

        //WebView webview = new WebView(this);
        WebSettings ws = oauth_login_web_view.getSettings();
        ws.setSaveFormData(false);
        //ws.setSavePassword(false);

        oauth_login_web_view.clearCache(true);
        oauth_login_web_view.clearHistory();
        oauth_login_web_view.loadUrl(final_url);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("onErrorResponse", "JSON Response: " + error);
        Log.d("onErrorResponse", "JSON Error: "+error.getLocalizedMessage());
        Log.d("onErrorResponse", "JSON Error: "+error.getMessage());
        //code_text_value.setText("Login Failed");
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
            //code_text_value.setText("Login Success!");

            SharedPreferences sharedPref = getSharedPreferences("userCred", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //enter the data from oauth token exchange
            editor.putString("access_token", response.get("access_token").toString());
            editor.putString("refresh_token", response.get("refresh_token").toString());
            editor.putLong("expires_in", expiredTime);
            //save it
            editor.apply();

            //toast for success
            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

            //should go over the new Activity already here
            Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
