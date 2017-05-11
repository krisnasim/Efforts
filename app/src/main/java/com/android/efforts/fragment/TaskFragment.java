package com.android.efforts.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.efforts.R;
import com.android.efforts.adapter.NewsAdapter;
import com.android.efforts.adapter.TaskAdapter;
import com.android.efforts.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskFragment extends Fragment {

    @BindView(R.id.task_list_view) ListView task_list_view;

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

        //get date
        Date date = new Date(2017, 05, 25, 8, 30);

        //create new forum object
        Task newTask = new Task();
        newTask.setTaskName("Sales");
        newTask.setTaskTitle("Check for sellout");
        newTask.setTaskContent("Check for daily sellouts");
        newTask.setTaskStatus("Pending");
        newTask.setTaskTimestamp(date);

        for(int x = 0; x < 6; x++) {
            taskData.add(newTask);
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

    private void setAdapter() {
        if(taskData.size()>0){
            Log.d("setAdapter", "Setting up news adapter");

            adapter = new TaskAdapter(getActivity(), taskData, res);
            task_list_view.setAdapter(adapter);
            task_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("log", "task clicked");

                }
            });
        }
        else {
            Log.d("setAdapter", "The taskData array is empty!");
        }
    }
}
