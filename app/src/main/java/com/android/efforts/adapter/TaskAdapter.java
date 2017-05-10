package com.android.efforts.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.efforts.model.Task;

import java.util.List;

/**
 * Created by Jonathan Simananda on 09/05/2017.
 */

public class TaskAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Task> taskData;
    private Task tempValues;
    public Resources res;

    public TaskAdapter(Activity activity, List<Task> data, Resources resLocal) {
        this.activity = activity;
        this.taskData = data;
        this.res = resLocal;

        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        private TextView task_name;
        private TextView task_title;
        private TextView task_content;
        private TextView task_timestamp;
        private ImageView avatar;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
