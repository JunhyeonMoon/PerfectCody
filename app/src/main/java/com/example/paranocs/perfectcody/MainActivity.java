package com.example.paranocs.perfectcody;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.paranocs.perfectcody.Adapters.MainViewPagerAdapter;
import com.example.paranocs.perfectcody.Utils.VerticalViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;
    private ImageView imageView_home;
    private ImageView imageView_search;
    private ImageView imageView_favorite;
    private ImageView imageView_profile;
    private ImageView imageView_addPhoto;

    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private FragmentTransaction fragmentTransaction;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();

        imageView_home = findViewById(R.id.imageView_home);
        imageView_search = findViewById(R.id.imageView_search);
        imageView_favorite = findViewById(R.id.imageView_favorite);
        imageView_profile = findViewById(R.id.imageView_profile);
        imageView_addPhoto = findViewById(R.id.imageView_addPhoto);

        imageView_home.setOnClickListener(onClickListener);
        imageView_search.setOnClickListener(onClickListener);
        imageView_favorite.setOnClickListener(onClickListener);
        imageView_profile.setOnClickListener(onClickListener);
        imageView_addPhoto.setOnClickListener(onClickListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.imageView_home:{
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;

                case R.id.imageView_search:{

                }
                break;
                case R.id.imageView_favorite:{

                }
                break;
                case R.id.imageView_profile:{

                }
                break;
                case R.id.imageView_addPhoto:{
                    if(mAuth.getCurrentUser() == null){
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        //TODO:갤러리에서 사진 업로드
                    }
                }
                break;


            }
        }
    };
}
