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
import com.android.efforts.model.Menu;

import java.util.List;

/**
 * Created by Jonathan Simananda on 05/04/2017.
 */

public class HomeMenuAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Menu> menuData;
    private Menu tempValues;
    public Resources res;

    public HomeMenuAdapter(Activity activity, List<Menu> data, Resources resLocal) {
        this.activity = activity;
        this.menuData = data;
        this.res = resLocal;

        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        private TextView menuName;
        private ImageView thumbnail;
    }

    @Override
    public int getCount() {
        return menuData.size();
    }

    @Override
    public Object getItem(int position) {
        return menuData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
        Log.v("ForumAdapter", "=====Row button clicked=====");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if(convertView == null) {
            rowView = inflater.inflate(R.layout.row_home_menu, parent, false);

            Log.d("GetView", "Finding all the right element after inflating custom layout");

            holder = new ViewHolder();

            holder.menuName = (TextView) rowView.findViewById(R.id.titleTextView);
            holder.thumbnail = (ImageView) rowView.findViewById(R.id.coverImageView);
//            holder.fullName = (TextView) rowView.findViewById(R.id.startPoint);
//            holder.forumDateCreated = (TextView) rowView.findViewById(R.id.endPoint);
//            holder.forumTitle = (TextView) rowView.findViewById(R.id.Distance);
//            holder.forumPost = (TextView) rowView.findViewById(R.id.StartDate);
//            holder.numberOfPosts = (TextView) rowView.findViewById(R.id.availableSeats);
//            holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);

            //getting ride data for each row
            Menu menu = menuData.get(position);

            //setting up text and image
            //post.getFullName();

            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }

        if(menuData.size() <= 0) {
            holder.menuName.setText("No Data");
        }
        else {
            tempValues = null;
            tempValues = menuData.get(position);

            Log.d("GetView", "setting each row with data");
//            Log.d("GetViewData", tempValues.getFullName());
//            Log.d("GetViewData", tempValues.getForumTitle());
//            Log.d("GetViewData", String.valueOf(tempValues.getForumDateCreated()));

            holder.menuName.setText(tempValues.getMenuName());
            holder.thumbnail.setImageResource(tempValues.getMenuThumbnail());
//            holder.fullName.setText(tempValues.getFullName());
//            holder.forumDateCreated.setText(datetoString);
//            holder.forumTitle.setText(tempValues.getForumTitle());
//            holder.forumPost.setText(tempValues.getForumPost());
//            holder.numberOfPosts.setText(String.valueOf(tempValues.getNumberOfPosts()));
//            holder.avatar.setImageResource(R.drawable.ic_attendance);

//            rowView.setOnClickListener(new OnItemClickListener(position));

        }

        return rowView;
    }

//    private class OnItemClickListener  implements View.OnClickListener {
//        private int mPosition;
//
//        OnItemClickListener(int position){
//            mPosition = position;
//        }
//
//        @Override
//        public void onClick(View arg0) {
//            Log.d("OnClick", "Your position is "+mPosition);
//            HomeFragment frag = new HomeFragment();
//            frag.onItemClick(mPosition);
//        }
//    }
}
