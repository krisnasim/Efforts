package com.android.efforts.customclass;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonat on 04/04/2017.
 */

public class CustomJSONObjectRequest extends JsonObjectRequest {

    private Map<String, String> params;
    private Map<String, String> headers;

    public CustomJSONObjectRequest(int method, String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public CustomJSONObjectRequest(int method, String url, JSONObject jsonRequest, Map<String, String> params,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.params = params;
    }

    public CustomJSONObjectRequest(int method, String url, JSONObject jsonRequest, Map<String, String> params,
                                   Map<String, String> headers,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.params = params;
        this.headers = headers;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        //HashMap<String, String> headers = new HashMap<String, String>();
        //headers.put("Content-Type", "application/x-www-form-urlencoded");
        //headers.put("Content-Type", "application/json");
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        // here you can write a custom retry policy
        return super.getRetryPolicy();
    }
}
