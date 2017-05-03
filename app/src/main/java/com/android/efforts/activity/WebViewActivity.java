package com.android.efforts.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.efforts.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.oauth_login_web_view) WebView oauth_login_web_view;

    private Uri uri;

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
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Here put your code
                //Log.d("My Webview", final_url);
                //Uri uri = request.get

                Log.d("OverrideURL", "YOOHOO");
                if(uri.getScheme().equals("https") && uri.getHost().equals("android.efforts.trd.client.nx.tsun.moe") && uri.getPath().equals("/oauth/callback")) {
                    Log.d("URL Catch", "Entered URL loading block");

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
                if(uri.getScheme().equals("https") && uri.getHost().equals("android.efforts.trd.client.nx.tsun.moe") && uri.getPath().equals("/oauth/callback")) {
                    Log.d("URL Catch", "Entered depreceated URL loading block");

                    Log.d("URI", uri.toString());
                    Log.d("Get URI path", uri.getScheme());
                    Log.d("Get URI path", uri.getHost());
                    Log.d("Get URI path", uri.getPath());
                    Log.d("Last URI", uri.getLastPathSegment());
                    Log.d("value URI", uri.getQueryParameter("code"));

                    Bundle bundle = new Bundle();
                    bundle.putString("url_code", uri.getQueryParameter("code"));

                    Intent intent = new Intent(WebViewActivity.this, AccountActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

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


}
