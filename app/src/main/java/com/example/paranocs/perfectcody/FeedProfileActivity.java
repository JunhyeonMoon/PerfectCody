package com.example.paranocs.perfectcody;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.Adapters.UserPhotoRecyclerViewAdapter;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class FeedProfileActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView imageView_profile;
    private LinearLayout layout_follow;
    private TextView textView_nickname;
    private TabLayout tabLayout;

    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private UserPhotoRecyclerViewAdapter userPhotoRecyclerViewAdapter1;
    private UserPhotoRecyclerViewAdapter userPhotoRecyclerViewAdapter2;
    private ArrayList<Map<String, Object>> items_photo = new ArrayList<>();
    private ArrayList<Map<String, Object>> items_good = new ArrayList<>();
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_profile);
        mContext = getApplicationContext();
        uid = getIntent().getStringExtra("uid");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        getUserDataFromDB();
        getPhotoFromDB();
    }

    // FrameLayout 탭 전환
    private void changeView(int index) {
        switch (index) {
            case 0:
                recyclerView1.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.INVISIBLE);
                break;
            case 1:
                recyclerView1.setVisibility(View.INVISIBLE);
                recyclerView2.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void init(){
        imageView_profile = findViewById(R.id.circleImageView);
        textView_nickname = findViewById(R.id.textView_nickname);
        tabLayout = findViewById(R.id.tab_layout);
        layout_follow = findViewById(R.id.layout_follow);

        layout_follow.setOnClickListener(onClickListener);

        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView2 = findViewById(R.id.recyclerView2);
        userPhotoRecyclerViewAdapter1 = new UserPhotoRecyclerViewAdapter(mContext, items_photo);
        userPhotoRecyclerViewAdapter2 = new UserPhotoRecyclerViewAdapter(mContext, items_good);
        recyclerView1.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView2.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView1.setAdapter(userPhotoRecyclerViewAdapter1);
        recyclerView2.setAdapter(userPhotoRecyclerViewAdapter2);

        // 사진 & 좋아요 탭
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                changeView(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void getUserDataFromDB(){
        db.document(mContext.getString(R.string.db_users)+"/"+uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    textView_nickname.setText(SingleTon.getInstance().toString(docData.get("nickname")));
                    Glide.with(mContext).load(SingleTon.getInstance().toString(docData.get("profile"))).into(imageView_profile);
                }
            }
        });
    }

    private void getPhotoFromDB(){
        final String userID = uid;
        items_good.clear();
        items_photo.clear();
        db.collection(mContext.getString(R.string.db_photo)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> docData = document.getData();
                        if(docData.containsKey("goodUid")){
                            ArrayList<String> list= (ArrayList<String>) docData.get("goodUid");
                            if(list.contains(userID)){
                                items_good.add(docData);
                            }
                        }

                        if(userID.equals(SingleTon.getInstance().toString(docData.get("uid")))){
                            items_photo.add(docData);
                        }
                        userPhotoRecyclerViewAdapter1.notifyDataSetChanged();
                        userPhotoRecyclerViewAdapter2.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.layout_follow : {
                   //TODO 팔로우
                }
                break;
            }
        }
    };
}
