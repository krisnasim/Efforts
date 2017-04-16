package com.android.efforts.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.efforts.R;
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.adapter.HomeMenuAdapter;
import com.android.efforts.model.Menu;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

//    @BindView(R.id.list_view_home_fragment) ListView list_view_home_fragment;
    @BindView(R.id.card_report_view) CardView card_report_view;
    @BindView(R.id.card_task_view) CardView card_task_view;
    @BindView(R.id.bar_report_chart) BarChart bar_report_chart;
    @BindView(R.id.bar_task_chart) BarChart bar_task_chart;

    private Resources res;
    private HomeMenuAdapter adapter;
    private List<Menu> menuData = new ArrayList<Menu>();
    private HomeActivity act;

    public HomeFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        act = (HomeActivity) getActivity();

        //LineChart chart = (LineChart) findViewById(R.id.chart);
        //YourData[] dataObjects = ...;
        final String[] dayData = {"M", "T", "W", "T", "F", "S", "S"};
        int[] reportValueDatas = {6, 13, 4, 5, 7, 2, 17};
        int[] taskValueDatas = {4, 6, 4, 5, 5, 7, 2};

        List<BarEntry> reportEntries = new ArrayList<BarEntry>();
        List<BarEntry> taskEntries = new ArrayList<BarEntry>();

        //set title
        getActivity().setTitle("Beranda");

        //testing new MPAndroidChart here
        for (int j=0; j<7; j++) {
            // turn your data into Entry objects
            reportEntries.add(new BarEntry(j+1, reportValueDatas[j]));
            taskEntries.add(new BarEntry(j+1, taskValueDatas[j]));
        }

        BarDataSet reportDataSet = new BarDataSet(reportEntries, "Report"); // add entries to dataset
        reportDataSet.setColors(ContextCompat.getColor(getActivity(), R.color.md_amber_500),
                ContextCompat.getColor(getActivity(), R.color.md_red_500),
                ContextCompat.getColor(getActivity(), R.color.md_blue_500),
                ContextCompat.getColor(getActivity(), R.color.md_blue_500),
                ContextCompat.getColor(getActivity(), R.color.md_purple_500),
                ContextCompat.getColor(getActivity(), R.color.md_indigo_500),
                ContextCompat.getColor(getActivity(), R.color.md_red_500));
        //reportDataSet.setValueTextColor(...);

        BarDataSet taskDataSet = new BarDataSet(taskEntries, "Task"); // add entries to dataset
        taskDataSet.setColors(ContextCompat.getColor(getActivity(), R.color.md_amber_500),
                ContextCompat.getColor(getActivity(), R.color.md_red_500),
                ContextCompat.getColor(getActivity(), R.color.md_amber_500),
                ContextCompat.getColor(getActivity(), R.color.md_red_500),
                ContextCompat.getColor(getActivity(), R.color.md_red_500),
                ContextCompat.getColor(getActivity(), R.color.md_orange_500),
                ContextCompat.getColor(getActivity(), R.color.md_indigo_500));
        //taskDataSet.setValueTextColor(...);

        XAxis bottom = bar_report_chart.getXAxis();
        bottom.setDrawAxisLine(false);
        bottom.setDrawGridLines(false);
        //bottom.setDrawLabels(false);
        bottom.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottom.setDrawLimitLinesBehindData(false);
        bottom.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dayData[(int) value-1];
            }
        });

        YAxis left = bar_report_chart.getAxisLeft();
        left.setDrawLabels(false); // no axis labels
        left.setDrawAxisLine(false); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(false); // no zero line
        bar_report_chart.getAxisRight().setEnabled(false); // no right axis

        Legend legend = bar_report_chart.getLegend();
        legend.setEnabled(false);
        Description description = new Description();
        description.setText("");
        bar_report_chart.setDescription(description);

        XAxis bottom_2 = bar_task_chart.getXAxis();
        bottom_2.setDrawAxisLine(false);
        bottom_2.setDrawGridLines(false);
        //bottom_2.setDrawLabels(false);
        bottom_2.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottom_2.setDrawLimitLinesBehindData(false);
        bottom_2.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dayData[(int) value-1];
            }
        });

        YAxis left_2 = bar_task_chart.getAxisLeft();
        left_2.setDrawLabels(false); // no axis labels
        left_2.setDrawAxisLine(false); // no axis line
        left_2.setDrawGridLines(false); // no grid lines
        left_2.setDrawZeroLine(false); // no zero line
        bar_task_chart.getAxisRight().setEnabled(false); // no right axis

        Legend legend_2 = bar_task_chart.getLegend();
        legend_2.setEnabled(false);
        Description description_2 = new Description();
        description_2.setText("");
        bar_task_chart.setDescription(description_2);

        BarData reportData = new BarData(reportDataSet);
        bar_report_chart.setData(reportData);
        reportDataSet.setDrawValues(false);
        bar_report_chart.invalidate(); // refresh
        bar_report_chart.setTouchEnabled(false);
        bar_report_chart.animateY(2000, Easing.EasingOption.EaseOutCirc);

        BarData taskData = new BarData(taskDataSet);
        bar_task_chart.setData(taskData);
        taskDataSet.setDrawValues(false);
        bar_task_chart.invalidate(); // refresh
        bar_task_chart.setTouchEnabled(false);
        bar_task_chart.animateY(2000, Easing.EasingOption.EaseOutCirc);



//        String[] menuNames = getResources().getStringArray(R.array.home_menu_list);
//        //check if the menu data is empty
//        if(menuData.size() == 0) {
//            for (int j = 0; j < menuNames.length; j++) {
//                Log.d("names", menuNames[j]);
//                Menu menu = new Menu();
//                menu.setMenuName(menuNames[j]);
//                if (j == 0) {
//                    menu.setMenuThumbnail(R.drawable.attendance);
//                } else if (j == 1) {
//                    menu.setMenuThumbnail(R.drawable.sellout);
//                } else if (j == 2) {
//                    menu.setMenuThumbnail(R.drawable.competitor);
//                } else if (j == 3) {
//                    menu.setMenuThumbnail(R.drawable.sellout);
//                } else if (j == 4) {
//                    menu.setMenuThumbnail(R.drawable.competitor);
//                }
//
//                Log.d("printout", menu.getMenuName());
//                menuData.add(menu);
//            }
//        }
//        setAdapter();
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

    public void setAdapter() {
        if(menuData.size()>0){
            Log.d("setAdapter", "Setting up menu adapter");

            adapter = new HomeMenuAdapter(getActivity(), menuData, res);
//            list_view_home_fragment.setAdapter(adapter);
//            list_view_home_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Log.d("OnClick", "Your position is "+position);
//                    Fragment fragment = new Fragment();
//
//                    if(position == 0) {
//                        fragment = new AttendanceFragment();
//                    } else if(position == 1) {
//                        fragment = new SelloutFragment();
//                    } else if(position == 2) {
//                        fragment = new CompetitorFragment();
//                    }
//
//                    if (act != null) // Make sure we are attached
//                    {
//                        act.changeFragment(fragment);
//                    }
//                    else {
//                        Log.d("changeFragment", "hmm. null?");
//                    }
//                }
//            });
        }
        else {
            Log.d("setAdapter", "The menuData array is empty!");
        }
    }

//    public void onItemClick(int mPosition) {
//        //call fragmentManager
////        FragmentManager manager = getFragmentManager();
////        FragmentTransaction transaction = manager.beginTransaction();
//        Fragment fragment = new Fragment();
//
//        if(mPosition == 0) {
//             fragment = new AttendanceFragment();
//        } else if(mPosition == 1) {
//             fragment = new SelloutFragment();
//        } else if(mPosition == 2) {
//             fragment = new InventoryFragment();
//        } else if(mPosition == 3) {
//             fragment = new SalesKitFragment();
//        } else if(mPosition == 4) {
//             fragment = new CompetitorFragment();
//        } else if(mPosition == 5) {
//             fragment = new ForumFragment();
//        }
//
//        act.changeFragment(fragment);
////        transaction.replace(R.id.home_main_frame, fragment);
////        transaction.commit();
//    }

}
