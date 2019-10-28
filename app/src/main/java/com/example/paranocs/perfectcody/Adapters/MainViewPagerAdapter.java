package com.example.paranocs.perfectcody.Adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.paranocs.perfectcody.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainViewPagerAdapter extends PagerAdapter {
    private ArrayList<HashMap<String, Object>> items = new ArrayList<>();
    private Context mContext;

    public MainViewPagerAdapter(Context context, ArrayList<HashMap<String, Object>> items){
        this.items = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view = null;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.main_pager, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.common_google_signin_btn_icon_light, mContext.getTheme()));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
