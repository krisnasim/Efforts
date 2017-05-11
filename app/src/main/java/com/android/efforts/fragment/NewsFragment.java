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
import com.android.efforts.model.News;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsFragment extends Fragment {

    @BindView(R.id.news_list_view) ListView news_list_view;

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
}
