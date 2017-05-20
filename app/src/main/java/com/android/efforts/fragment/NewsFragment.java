package com.android.efforts.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.efforts.R;
import com.android.efforts.adapter.NewsAdapter;
import com.android.efforts.customclass.ProtoBufRequest;
import com.android.efforts.model.News;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moe.tsun.nx.api.NxCommonProto;
import moe.tsun.nx.api.NxFormProto;

public class NewsFragment extends Fragment implements Response.ErrorListener, Response.Listener<NxFormProto.QrAttachment> {

    @BindView(R.id.news_list_view) ListView news_list_view;

    private String token = "";
    private Resources res;
    private NewsAdapter adapter;
    private List<News> newsData = new ArrayList<News>();

    public NewsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("News");

        SharedPreferences sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        //Log.d("sharedPref", sharedPref.getString("jwt", "NoToken"));
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

        //get date
        Date date = new Date(2017, 05, 25, 8, 30);

        //create new forum object
        News newNews = new News();
        newNews.setNewsTitle("This is some news title");
        newNews.setNewsTimestamp(date);
        newNews.setNewaContent("This is some short content about the news. I hope you understand that not much is happening right here");

        for(int x = 0; x < 6; x++) {
            newsData.add(newNews);
        }

        setAdapter();

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
    public void onErrorResponse(VolleyError error) {
        try {
            Log.d("errorResponse", String.valueOf(error));
            Log.d("errorHeader", String.valueOf(error.networkResponse.headers));
            //Log.d("errorResponse", String.valueOf(error.networkResponse.data));
            Log.d("errorResponseData", new String(error.networkResponse.data));
            NxCommonProto.SpringError err = NxCommonProto.SpringError.parseFrom(error.networkResponse.data);
//            Log.d("errorResponse", err.getErrorMap().toString());
            //onLoginFailed();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("errorResponse", "FUCK THIS SHIT!!!!");
        }
    }

    @Override
    public void onResponse(NxFormProto.QrAttachment response) {
        String bodyResult = "";
        try {
            Log.d("onResponse", response.toString());
            //bodyResult = JWTUtils.decoded(String.valueOf(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        if(newsData.size()>0){
            Log.d("setAdapter", "Setting up news adapter");

            adapter = new NewsAdapter(getActivity(), newsData, res);
            news_list_view.setAdapter(adapter);
            news_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("log", "news clicked");

//                    for(int a=0; a<forumData.size(); a++) {
//                        forumData.get(a).getPostID();
//                    }
//                    Bundle bundle = new Bundle();
//                    bundle.putString("postID", newsData.get(position).getPostID());
//                    PostFragment fragment = new PostFragment();
//                    fragment.setArguments(bundle);
//
//                    //get sharedpref level
//                    String level = sharedPref.getString("level", "NoLevel");
//                    if(level.equals("sales_representative") || level.equals("area_manager") || level.equals("promoter") || level.equals("admin")) {
//                        HomeSRActivity act = (HomeSRActivity) getActivity();
//                        act.changeFragment(fragment);
//                    } else if(level.equals("merchandiser")) {
//                        HomeMDSActivity act = (HomeMDSActivity) getActivity();
//                        act.changeFragment(fragment);
//                    }

                }
            });
        }
        else {
            Log.d("setAdapter", "The newsData array is empty!");
        }
    }

    private void loginWithOAuthNX(String email, String pwd) throws JSONException, UnsupportedEncodingException {
        //String url = "http://192.168.100.60:8180/r/api/v1/data";
        String url = "https://form.nx.tsun.moe/r/api/v1/assets/3210349132296870646";

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
        headers.put("Charset", "UTF-8");
        headers.put("Content-Type", "image/jpg");
        headers.put("Accept", "application/x-protobuf,application/json");
        headers.put("Authorization", "Bearer "+token);
        Log.d("token", token);

        //JSONObject insideJsonObj = new JSONObject();
        JSONObject jsonObj = new JSONObject();
//        jsonObj.put("username", email);
//        jsonObj.put("password_digest", pwd);
        jsonObj.put("title", "this is title report");
        jsonObj.put("description", "this is description");
        jsonObj.put("report_type", "authorization_report");
        JSONObject photo = new JSONObject();
        photo.put("path", "https://vignette4.wikia.nocookie.net/k-on/images/b/be/K-ON_Character.jpg");
        photo.put("contentType", "image/jpeg");
        photo.put("name", "K-ON_Character");
        photo.put("description", "kawaii");
        jsonObj.put("photo", photo);

        //JSONObject jsonObj = new JSONObject();
        //jsonObj.put("auth", insideJsonObj);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.competitor);

        //CUSTOM SAMPLE FOR NXFORMPROTOBUF
        //NxFormProto.OpForm testForm = NxFormProto.OpForm.newBuilder()
        NxFormProto.OpDatum testDatum = NxFormProto.OpDatum.newBuilder().setFormId(3210349132296870646L).setContent(jsonObj.toString(2)).build();
        byte[] data = testDatum.toByteArray();
        //NxFormProto.OpTemplate testTemplate = NxFormProto.OpTemplate.newBuilder()

        HashMap<String, String> params = new HashMap<>();
//        params.put("title", "this is title report");
//        params.put("description", "this is description");
//        params.put("report_type", "authorization_report");
//        params.put("photo_url", "photo_url");
//        params.put("content", data);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //CustomJSONObjectRequest customJSONReq = new CustomJSONObjectRequest(Request.Method.POST, url, jsonObj, params, headers, this, this);
        ProtoBufRequest<NxFormProto.OpDatum, NxFormProto.QrAttachment> testReq = new ProtoBufRequest<>(Request.Method.POST, url, bm, NxFormProto.QrAttachment.class, headers, this, this);

        requestQueue.add(testReq);
    }
}
