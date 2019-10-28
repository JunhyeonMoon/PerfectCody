package com.example.paranocs.perfectcody;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.paranocs.perfectcody.Adapters.MainViewPagerAdapter;
import com.example.paranocs.perfectcody.Utils.VerticalViewPager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;
    private ImageView imageView_home;
    private ImageView imageView_search;
    private ImageView imageView_favorite;
    private ImageView imageView_profile;
    private MainViewPagerAdapter mainViewPagerAdapter;


    private VerticalViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();


        imageView_home = findViewById(R.id.imageView_home);
        imageView_search = findViewById(R.id.imageView_search);
        imageView_favorite = findViewById(R.id.imageView_favorite);
        imageView_profile = findViewById(R.id.imageView_profile);
        viewPager = (VerticalViewPager) findViewById(R.id.viewPager);

        ArrayList<HashMap<String, Object>> items = new ArrayList<>();
        HashMap<String, Object> item = new HashMap<>();
        item.put("aa", 11);
        items.add(item);
        items.add(item);
        items.add(item);
        mainViewPagerAdapter = new MainViewPagerAdapter(mContext, items);
        viewPager.setAdapter(mainViewPagerAdapter);

        Log.d(TAG, "viewPager current position " + viewPager.getCurrentItem());

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
