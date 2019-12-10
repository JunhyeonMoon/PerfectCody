package com.example.paranocs.perfectcody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private TextView textView_info;
    private String result = "";
    private int userStyle = 0;
    private int userSex = 0;
    private ArrayList<String> targetName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mContext = getApplicationContext();
        textView_info = findViewById(R.id.textView_info);
        viewPager = findViewById(R.id.viewPager);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentAdapter);
        userSex = getIntent().getIntExtra("userSex", 0);
        userStyle = getIntent().getIntExtra("userStyle", 0);
        result = getIntent().getStringExtra("result");
        Log.d(TAG, "result: " + result);
        String targets = result.split("'")[1];
        ArrayList<String> targetName = new ArrayList<String>(Arrays.asList(targets.split(" ")));

        String codyPath = "";
        if (userSex == 0) {
            if (userStyle == 0) {
                codyPath = getString(R.string.storage_manCasual);
            } else if (userStyle == 1) {
                codyPath = getString(R.string.storage_manDandy);
            } else {
                codyPath = getString(R.string.storage_manStreet);
            }
        } else {
            if (userStyle == 0) {
                codyPath = getString(R.string.storage_womanCasual);
            } else if (userStyle == 1) {
                codyPath = getString(R.string.storage_womanDandy);
            } else {
                codyPath = getString(R.string.storage_womanStreet);
            }
        }

        fragments.clear();
        items.clear();
        for (int i = 0; i < targetName.size(); i++) {
            String target = targetName.get(i);
            final String path = getString(R.string.storage_codySet) + "/" +
                    codyPath + "/" + target + ".jpg";

            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Map<String, Object> doc = new HashMap<>();
                                doc.put("path", path);
                                doc.put("userStyle", userStyle);
                                doc.put("userSex", userSex);
                                doc.put("bitmap", resource);
                                items.add(doc);
                                Log.d(TAG, "doc: " + doc);
                                ResultViewPagerFragment f = new ResultViewPagerFragment(doc);
                                fragments.add(f);
                                fragmentAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }


    class FragmentAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
