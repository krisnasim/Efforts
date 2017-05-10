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
import com.android.efforts.model.News;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Simananda on 09/05/2017.
 */

public class NewsAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private LayoutInflater inflater;
    private List<News> newsData;
    private News tempValues;
    public Resources res;

    public NewsAdapter(Activity activity, List<News> data, Resources resLocal) {
        this.activity = activity;
        this.newsData = data;
        this.res = resLocal;

        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        @BindView(R.id.news_title) TextView news_title;
        @BindView(R.id.news_content) TextView news_content;
        @BindView(R.id.news_timestamp) TextView news_timestamp;
        @BindView(R.id.avatar_news) ImageView avatar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onClick(View v) {
        Log.v("NewsAdapter", "=====Row button clicked=====");
    }

    @Override
    public int getCount() {
        return newsData.size();
    }

    @Override
    public Object getItem(int position) {
        return newsData.get(position);
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
            rowView = inflater.inflate(R.layout.news_row_layout, parent, false);
            holder = new ViewHolder(convertView);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        if(newsData.size() <= 0) {
            holder.news_title.setText("No Data");
        }
        else {
            tempValues = null;
            tempValues = newsData.get(position);

            Log.d("GetView", "setting each row with data");
            Log.d("GetViewData", tempValues.getNewsTitle());
            Log.d("GetViewData", String.valueOf(tempValues.getNewsTimestamp()));
            Log.d("GetViewData", String.valueOf(tempValues.getNewsID()));

            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //Format formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
            Date date = tempValues.getNewsTimestamp();
            //Log.d("DateView", String.valueOf(tempValues.getTravelStart()));
            String datetoString = formatter.format(date);

            holder.news_title.setText(tempValues.getNewsTitle());
            holder.news_content.setText(tempValues.getNewaContent());
            holder.news_timestamp.setText(datetoString);
            holder.avatar.setImageResource(R.mipmap.ic_launcher);
        }

        return rowView;
    }
}
