package com.android.efforts.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.customclass.ProtoBufRequest;
import com.android.efforts.model.Task;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import moe.tsun.nx.api.NxCommonProto;
import moe.tsun.nx.api.NxFormProto;

public class TaskRespondActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<NxFormProto.QrDatum>  {

    @BindView(R.id.task_respond_input) EditText task_respond_input;
    @BindView(R.id.submit_task_respond_button) Button submit_task_respond_button;

    private Task selectedTask;
    private SharedPreferences sharedPref;
    private ProgressDialog progressDialog;

    @OnClick(R.id.submit_task_respond_button)
    public void submitTaskRespond() {
        String taskRespond = task_respond_input.getText().toString();

//        if (taskRespond.matches("")) {
//            Toast.makeText(this, "Anda belum memasukan detil informasi tugas!", Toast.LENGTH_SHORT).show();
//        }
//        else {

            sharedPref = getSharedPreferences("userCred", Context.MODE_PRIVATE);
            Log.d("SharedPref", sharedPref.getString("access_token", "NoToken"));
            String token = sharedPref.getString("access_token", "empty token");

            submit_task_respond_button.setEnabled(false);
            progressDialog = new ProgressDialog(this, R.style.CustomDialog);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Mohon tunggu...");
            progressDialog.show();

            //set URLs
            String urlTask = "https://form.nx.tsun.moe/r/api/v1/forms/3215339506078574761/data";
            String urlTaskRespond = "https://form.nx.tsun.moe/r/api/v1/forms/3215342139977754797/data";
            String urlTaskRespondNew = "https://form.nx.tsun.moe/r/api/v1/forms/3223195701625743566/data";

            //set headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Charset", "UTF-8");
            headers.put("Content-Type", "application/x-protobuf");
            headers.put("Accept", "application/x-protobuf,application/json");
            headers.put("Authorization", "Bearer "+token);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
            String timestampDate = df.format(selectedTask.getTaskTimestamp());
            String startTaskDate = df2.format(selectedTask.getTaskStartDate());
            String endTaskDate = df2.format(selectedTask.getTaskEndDate());

            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("title", selectedTask.getTaskTitle());
                jsonObj.put("date", timestampDate);
                jsonObj.put("content", selectedTask.getTaskContent());
                jsonObj.put("date_start", startTaskDate);
                jsonObj.put("date_end", endTaskDate);
                jsonObj.put("status", "SELESAI");
                jsonObj.put("task_id_reference", selectedTask.getTaskIDReference());
                jsonObj.put("task_id_obsolete", selectedTask.getTaskID());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            NxFormProto.OpDatum testDatum = null;
            try {
                testDatum = NxFormProto.OpDatum.newBuilder().setFormId(3223195701625743566L).setContent(jsonObj.toString(2)).build();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            ProtoBufRequest<NxFormProto.OpDatum, NxFormProto.QrDatum> testReq = new ProtoBufRequest<>(Request.Method.POST, urlTaskRespondNew, testDatum, NxFormProto.QrDatum.class, headers, this, this);
            requestQueue.add(testReq);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_respond);
        ButterKnife.bind(this);

        setTitle("Respon Tugas");

        Bundle data = getIntent().getExtras();
        selectedTask = (Task) data.getParcelable("task");
        Log.d("task", selectedTask.getTaskID());
        Log.d("Task", selectedTask.getTaskTitle());
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
    public void onResponse(NxFormProto.QrDatum response) {
        String bodyResult = "";
        try {
            Log.d("onResponse", response.toString());
            progressDialog.dismiss();
            //bodyResult = JWTUtils.decoded(String.valueOf(response));

            Toast.makeText(this, "Laporan berhasil!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
