package com.example.paranocs.perfectcody.Adapters;

import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.okhttp.internal.Internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainViewPagerAdapter extends PagerAdapter {
    private String TAG = getClass().getName();
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageView imageView;
    private ImageView imageView_good;
    private ImageView imageView_bad;
    private ImageView imageView_comment;
    private ImageView imageView_feedProfile;
    private TextView textView_good;
    private TextView textView_bad;
    private TextView textView_comment;


    public MainViewPagerAdapter(Context context, ArrayList<Map<String, Object>> items) {
        this.items = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.main_pager, container, false);
        init(view, position);

        String uri = items.get(position).get("uri").toString();
        Glide.with(mContext).load(uri).into(imageView);
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

    void init(View view, final int position) {
        imageView = view.findViewById(R.id.imageView);
        imageView_good = view.findViewById(R.id.imageView_good);
        imageView_bad = view.findViewById(R.id.imageView_bad);
        imageView_comment = view.findViewById(R.id.imageView_comment);
        imageView_feedProfile = view.findViewById(R.id.imageView_feedProfile);
        
        textView_good = view.findViewById(R.id.textView_good);
        textView_bad = view.findViewById(R.id.textView_bad);
        textView_comment = view.findViewById(R.id.textView_comment);


        final String docID = toString(items.get(position).get("docID"));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView_bad:
                    case R.id.imageView_good : {
                        Map<String, Object> data = new HashMap<>();
                        String target = "";
                        if(v.getId() == R.id.imageView_good){
                            target = "good";
                        }else if(v.getId() == R.id.imageView_bad){
                            target = "bad";
                        }
                        Log.d(TAG, "docID: " + docID);

                        int target_int = Integer.parseInt(items.get(position).get(target).toString());
                        target_int++;
                        items.get(position).put(target, target_int);
                        data.put(target, target_int);
                        db.document(mContext.getString(R.string.db_photo) + "/"
                                + docID).update(data);
                    }
                    break;

                    case R.id.imageView_comment : {
                        //TODO 코멘트 달기
                    }
                    break;
                }
            }
        };
        imageView_good.setOnClickListener(onClickListener);
        imageView_bad.setOnClickListener(onClickListener);
        imageView_comment.setOnClickListener(onClickListener);
        imageView_feedProfile.setOnClickListener(onClickListener);

        textView_good.setText(toString(items.get(position).get("good")));
        textView_bad.setText(toString(items.get(position).get("bad")));
        textView_comment.setText(toString(items.get(position).get("comment")));
    }

    private String toString(Object o){
        return o != null ? o.toString() : "0";
    }

}
