package com.example.paranocs.perfectcody;

import androidx.annotation.AnimatorRes;
import androidx.appcompat.app.AppCompatActivity;

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
    TextView textView1;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_profile);

        textView1 = (TextView)findViewById(R.id.text1);
        textView2 = (TextView)findViewById(R.id.text2);

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

        // 시작 시 FrameLayout에 표시될 뷰를 결정하기 위해 textView2는 removeView()함수로 제거.
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.contents);
        frameLayout.removeView(textView2);
    }

    // FrameLayout 탭 전환
    private void changeView(int index) {
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.contents);
        // 0번째 뷰 제거. (뷰가 하나이기 때문에 0번째만 제거하면 모든 뷰가 제거됨.)
        frameLayout.removeViewAt(0);

        switch (index) {
            case 0:
                frameLayout.addView(textView1);
                break;
            case 1:
                frameLayout.addView(textView2);
                break;

        }
    }
}
