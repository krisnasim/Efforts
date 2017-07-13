package com.android.efforts.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.activity.TaskDetailActivity;
import com.android.efforts.activity.WebViewActivity;
import com.android.efforts.adapter.TaskAdapter;
import com.android.efforts.customclass.ProtoBufRequest;
import com.android.efforts.model.Task;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moe.tsun.nx.api.NxCommonProto;
import moe.tsun.nx.api.NxFormProto;

public class TaskFragment extends Fragment implements Response.ErrorListener, Response.Listener<NxFormProto.PgQrDatum> {

    @BindView(R.id.task_list_view) ListView task_list_view;

    private String token = "";
    private Resources res;
    private TaskAdapter adapter;
    private List<Task> taskData = new ArrayList<Task>();

    public TaskFragment() {
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
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Task");

        SharedPreferences sharedPref = getActivity().getSharedPreferences("userCred", Context.MODE_PRIVATE);
        //Log.d("sharedPref", sharedPref.getString("jwt", "NoToken"));
        String fullName = sharedPref.getString("full_name", "John Did");
        String email = sharedPref.getString("email", "nomail@reply.com");
        token = sharedPref.getString("access_token", "noToken");

        try {
            loginWithOAuthNX("email", "pws");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        //get date
//        Date date = new Date(2017, 05, 25, 8, 30);
//
//        //create new forum object
//        Task newTask = new Task();
//        newTask.setTaskName("Sales");
//        newTask.setTaskTitle("Check for sellout");
//        newTask.setTaskContent("Check for daily sellouts");
//        newTask.setTaskStatus("Pending");
//        newTask.setTaskTimestamp(date);
//
//        for(int x = 0; x < 6; x++) {
//            taskData.add(newTask);
//        }

        //setAdapter();

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
    public void onResponse(NxFormProto.PgQrDatum response) {
        String bodyResult = "";
        try {
            Log.d("onResponse", response.toString());

            NxFormProto.PgQrDatum responseData = response;
            List<NxFormProto.QrDatum> arrayData = responseData.getPayloadList();

            for(int i=0; i<arrayData.size(); i++) {
                NxFormProto.QrDatum data = arrayData.get(i);
                Log.d("content", String.valueOf(data.getId()));
                Log.d("content", String.valueOf(data.getCredentialId()));
                Log.d("content", String.valueOf(data.getCreatedAt()));
                Log.d("content", String.valueOf(data.getUpdatedAt()));
                JSONObject JSONdata = new JSONObject(data.getContent());
                Log.d("JSON", String.valueOf(JSONdata.get("date")));
                Log.d("JSON", String.valueOf(JSONdata.get("date_start")));
                Log.d("JSON", String.valueOf(JSONdata.get("date_end")));
                Log.d("JSON", String.valueOf(JSONdata.get("title")));
                Log.d("JSON", String.valueOf(JSONdata.get("content")));
                Log.d("JSON", String.valueOf(JSONdata.get("status")));
                //Log.d("content", String.valueOf(data.getContent()));

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
                Date timestampDate = df.parse(String.valueOf(JSONdata.get("date")));
                Date startTaskDate = df2.parse(String.valueOf(JSONdata.get("date_start")));
                Date endTaskDate = df2.parse(String.valueOf(JSONdata.get("date_end")));

                Task newTask = new Task();
                newTask.setTaskID(String.valueOf(data.getId()));
                newTask.setTaskName("John Doe");
                newTask.setTaskTitle(String.valueOf(JSONdata.get("title")));
                newTask.setTaskContent(String.valueOf(JSONdata.get("content")));
                newTask.setTaskStatus(String.valueOf(JSONdata.get("status")));
                newTask.setTaskTimestamp(timestampDate);
                newTask.setTaskStartDate(startTaskDate);
                newTask.setTaskEndDate(endTaskDate);

                taskData.add(newTask);
            }

            setAdapter();
            //bodyResult = JWTUtils.decoded(String.valueOf(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        if(taskData.size()>0){
            Log.d("setAdapter", "Setting up news adapter");

            adapter = new TaskAdapter(getActivity(), taskData, res);
            task_list_view.setAdapter(adapter);
            task_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("log", "task clicked");
                    Task selectedTask = (Task) parent.getItemAtPosition(position);
                    Log.d("task", selectedTask.getTaskTitle());
                    Log.d("task", selectedTask.getTaskID());

                    //start Intent
                    Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                    //make bundle
                    Bundle extra = new Bundle();
                    intent.putExtra("task", selectedTask);
                    startActivity(intent);
                }
            });
        }
        else {
            Log.d("setAdapter", "The taskData array is empty!");
        }
    }

    private void loginWithOAuthNX(String email, String pwd) throws JSONException, UnsupportedEncodingException {
        //String url = "http://192.168.100.60:8180/r/api/v1/data";
        String url = "https://form.nx.tsun.moe/r/api/v1/forms/3215339506078574761/data";

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
//        headers.put("Charset", "UTF-8");
//        headers.put("Content-Type", "application/x-protobuf");
//        headers.put("Accept", "application/x-protobuf,application/json");
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

        //CUSTOM SAMPLE FOR NXFORMPROTOBUF
        //NxFormProto.OpForm testForm = NxFormProto.OpForm.newBuilder()
        //NxFormProto.OpDatum testDatum = NxFormProto.OpDatum.newBuilder().setFormId(3210349132296870646L).setContent(jsonObj.toString(2)).build();
        NxFormProto.OpDatum testDatum = NxFormProto.OpDatum.newBuilder().setFormId(3215339506078574761L).setContent(jsonObj.toString(2)).build();
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
        ProtoBufRequest<NxFormProto.OpDatum, NxFormProto.PgQrDatum> testReq = new ProtoBufRequest<>(url, NxFormProto.PgQrDatum.class, headers, this, this);

        requestQueue.add(testReq);
    }
}
