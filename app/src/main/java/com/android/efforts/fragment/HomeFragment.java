package com.android.efforts.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.efforts.R;
import com.android.efforts.activity.HomeActivity;
import com.android.efforts.adapter.HomeMenuAdapter;
import com.android.efforts.model.Menu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    @BindView(R.id.list_view_home_fragment)
    ListView list_view_home_fragment;

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

        //set title
        getActivity().setTitle("Beranda");

        String[] menuNames = getResources().getStringArray(R.array.home_menu_list);
        //check if the menu data is empty
        if(menuData.size() == 0) {
            for (int j = 0; j < menuNames.length; j++) {
                Log.d("names", menuNames[j]);
                Menu menu = new Menu();
                menu.setMenuName(menuNames[j]);
                if (j == 0) {
                    menu.setMenuThumbnail(R.drawable.attendance);
                } else if (j == 1) {
                    menu.setMenuThumbnail(R.drawable.sellout);
                } else if (j == 2) {
                    menu.setMenuThumbnail(R.drawable.inventory);
                } else if (j == 3) {
                    menu.setMenuThumbnail(R.drawable.saleskit);
                } else if (j == 4) {
                    menu.setMenuThumbnail(R.drawable.competitor);
                } else if (j == 5) {
                    menu.setMenuThumbnail(R.drawable.forum);
                } else if (j == 6) {
                    menu.setMenuThumbnail(R.drawable.forum);
                }

                Log.d("printout", menu.getMenuName());
                menuData.add(menu);
            }
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

    public void setAdapter() {
        if(menuData.size()>0){
            Log.d("setAdapter", "Setting up menu adapter");

            adapter = new HomeMenuAdapter(getActivity(), menuData, res);
            list_view_home_fragment.setAdapter(adapter);
            list_view_home_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("OnClick", "Your position is "+position);
                    Fragment fragment = new Fragment();

                    if(position == 0) {
                        fragment = new AttendanceFragment();
                    } else if(position == 1) {
                        //fragment = new SelloutFragment();
                    } else if(position == 2) {
                        //fragment = new InventoryFragment();
                    } else if(position == 3) {
                        //fragment = new SalesKitFragment();
                    } else if(position == 4) {
                        //fragment = new CompetitorFragment();
                    } else if(position == 5) {
                        //fragment = new ForumFragment();
                    } else if(position == 6) {
                        //fragment = new BlankAPIFragment();
                    }

                    if (act != null) // Make sure we are attached
                    {
                        act.changeFragment(fragment);
                    }
                    else {
                        Log.d("changeFragment", "hmm. null?");
                    }
                }
            });
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
