package com.android.efforts.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.efforts.R;
import com.android.efforts.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailActivity extends AppCompatActivity {

    @BindView(R.id.task_title_input) TextView task_title_input;
    @BindView(R.id.task_content_input) TextView task_content_input;
    @BindView(R.id.task_date_start_input) TextView task_date_start_input;
    @BindView(R.id.task_date_end_input) TextView task_date_end_input;
    @BindView(R.id.task_status_input) TextView task_status_input;

    private Task selectedTask;

    @OnClick(R.id.task_respond_button)
    public void respondTask() {
        //Toast.makeText(this, "well, hello!", Toast.LENGTH_SHORT).show();

        //new Intent
        Intent intent = new Intent(TaskDetailActivity.this, TaskRespondActivity.class);
        //make bundle
        Bundle extra = new Bundle();
        intent.putExtra("task", selectedTask);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);

        setTitle("Detil Tugas");

        Bundle data = getIntent().getExtras();
        selectedTask = (Task) data.getParcelable("task");
        Log.d("task", selectedTask.getTaskID());
        Log.d("Task", selectedTask.getTaskTitle());

        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = df2.format(selectedTask.getTaskStartDate());
        String endDate = df2.format(selectedTask.getTaskEndDate());

        task_title_input.setText(selectedTask.getTaskTitle());
        task_content_input.setText(selectedTask.getTaskContent());
        task_date_start_input.setText(startDate);
        task_date_end_input.setText(endDate);
        task_status_input.setText(selectedTask.getTaskStatus());

        Log.d("selectedTask", selectedTask.getTaskContent());
        Log.d("selectedTask", selectedTask.getTaskStatus());
        Log.d("selectedTask", selectedTask.getTaskIDReference());
    }
}
