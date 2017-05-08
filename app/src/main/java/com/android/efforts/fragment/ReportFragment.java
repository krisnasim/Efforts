package com.android.efforts.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.efforts.R;
import com.android.efforts.customclass.CustomJSONObjectRequest;
import com.android.efforts.customclass.JWTUtils;
import com.android.efforts.customclass.ProtoBufRequest;
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

public class ReportFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    String token = "";

    public ReportFragment() {
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
        SharedPreferences sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        Log.d("sharedPref", sharedPref.getString("jwt", "NoToken"));
        String fullName = sharedPref.getString("full_name", "John Did");
        String email = sharedPref.getString("email", "nomail@reply.com");
        token = sharedPref.getString("access_token", "noToken");

        try {
            loginWithOAuthNX("email", "pwd");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inflater.inflate(R.layout.fragment_report, container, false);
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
    public void onErrorResponse(VolleyError error) {
        Log.d("errorResponse", String.valueOf(error));
        //onLoginFailed();
    }

    @Override
    public void onResponse(JSONObject response) {
        String bodyResult = "";
        try {
            Log.d("onResponse", response.toString(2));
            bodyResult = JWTUtils.decoded(String.valueOf(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginWithOAuthNX(String email, String pwd) throws JSONException, UnsupportedEncodingException {
        //String url = "http://192.168.100.60:8180/r/api/v1/data";
        String url = "http://form.nx.tsun.moe/r/api/v1/data";


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
        headers.put("Content-Type", "application/x-protobuf");
        headers.put("Accept", "application/x-protobuf");
        headers.put("Authorization", "Bearer "+token);
        Log.d("token", token);

        HashMap<String, String> params = new HashMap<>();
        params.put("title", "this is title report");
        params.put("description", "this is description");
        params.put("report_type", "authorization_report");
        params.put("photo_url", "photo_url");

        //JSONObject insideJsonObj = new JSONObject();
        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("username", email);
//        jsonObj.put("password_digest", pwd);
        jsonObj.put("title", "this is title report");
        jsonObj.put("description", "this is description");
        jsonObj.put("report_type", "authorization_report");
        jsonObj.put("photo_url", "photo_url");

        //JSONObject jsonObj = new JSONObject();
        //jsonObj.put("auth", insideJsonObj);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //CustomJSONObjectRequest customJSONReq = new CustomJSONObjectRequest(Request.Method.POST, url, jsonObj, params, headers, this, this);
        //ProtoBufRequest testReq = new ProtoBufRequest(Request.Method.POST, url, params, asd, this, this);

    //        try {
    //            //Map<String, String> testH = jsObjRequest.getHeaders();
    //            //Log.d("headers",testH.get("Content-Type"));
    //            Log.d("headers", String.valueOf(customJSONReq.getHeaders()));
    //            Log.d("content", jsonObj.toString(2));
    //        } catch (AuthFailureError authFailureError) {
    //            authFailureError.printStackTrace();
    //        }
    //
    //        requestQueue.add(customJSONReq);
    }
}
