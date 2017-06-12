package com.android.efforts.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.efforts.R;
import com.android.efforts.model.Task;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Simananda on 09/05/2017.
 */

public class TaskAdapter extends BaseAdapter implements View.OnClickListener {

    //private Activity activity;
    private LayoutInflater inflater;
    private List<Task> taskData;
    private Task tempValues;
    public Resources res;

    public TaskAdapter(Activity activity, List<Task> data, Resources resLocal) {
        //this.activity = activity;
        this.taskData = data;
        this.res = resLocal;

        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        @BindView(R.id.task_name) TextView task_name;
        @BindView(R.id.task_title) TextView task_title;
        @BindView(R.id.task_content) TextView task_content;
        @BindView(R.id.task_timestamp) TextView task_timestamp;
        @BindView(R.id.task_status) TextView task_status;
        @BindView(R.id.avatar) ImageView avatar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getCount() {
        return taskData.size();
    }

    @Override
    public Object getItem(int position) {
        return taskData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if(convertView == null) {
            rowView = inflater.inflate(R.layout.task_row_layout, parent, false);
            holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        if(taskData.size() <= 0) {
            holder.task_title.setText("No data");
        }
        else {
            tempValues = null;
            tempValues = taskData.get(position);

            Log.d("GetView", "setting each row with data");
            Log.d("GetViewData", tempValues.getTaskTitle());
            Log.d("GetViewData", String.valueOf(tempValues.getTaskTimestamp()));
            Log.d("GetViewData", String.valueOf(tempValues.getTaskName()));

            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //Format formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
            Date date = tempValues.getTaskTimestamp();
            //Log.d("DateView", String.valueOf(tempValues.getTravelStart()));
            String datetoString = formatter.format(date);

            holder.task_name.setText(tempValues.getTaskName());
            holder.task_title.setText(tempValues.getTaskTitle());
            holder.task_content.setText(tempValues.getTaskContent());
            holder.task_status.setText(tempValues.getTaskStatus());
            holder.task_timestamp.setText(datetoString);
            holder.avatar.setImageResource(R.mipmap.ic_launcher);
        }

        return rowView;
    }
}
