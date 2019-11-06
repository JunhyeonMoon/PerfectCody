package com.example.paranocs.perfectcody;

import androidx.annotation.AnimatorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

public class FeedProfileActivity extends AppCompatActivity {

    Button button_follow;
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_profile);

        recyclerView1 = (RecyclerView)findViewById(R.id.recyclerView1);
        recyclerView2 = (RecyclerView)findViewById(R.id.recyclerView2);

        // 팔로우 <-> 언팔로우
        button_follow = (Button)findViewById(R.id.button_follow);
        button_follow.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonText = button_follow.getText().toString();

                if(buttonText.equals("+팔로우")) {
                    button_follow.setText("언팔로우");
                    button_follow.setBackgroundColor(Color.GRAY);
                } else {
                    button_follow.setText("+팔로우");
                    button_follow.setBackgroundColor(Color.RED);
                }
            }
        });


        // 드래그 화면 전환



        // 사진 & 좋아요 탭
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
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
}
