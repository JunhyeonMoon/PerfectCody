package com.example.paranocs.perfectcody;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paranocs.perfectcody.Adapters.MainViewPagerAdapter;
import com.example.paranocs.perfectcody.Utils.VerticalViewPager;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String TAG = getClass().getName();
    private Context mContext;

    private MainViewPagerAdapter mainViewPagerAdapter;
    private VerticalViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = (VerticalViewPager) view.findViewById(R.id.viewPager);


        //TODO:메인피드에 나올 사진리스트를 db에서 유저가 업로드한 사진들 중에서 가져오게 수정
        ArrayList<HashMap<String, Object>> items = new ArrayList<>();
        HashMap<String, Object> item = new HashMap<>();
        item.put("aa", 11);
        items.add(item);
        items.add(item);
        items.add(item);
        mainViewPagerAdapter = new MainViewPagerAdapter(mContext, items);
        viewPager.setAdapter(mainViewPagerAdapter);

        Log.d(TAG, "viewPager current position " + viewPager.getCurrentItem());

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
