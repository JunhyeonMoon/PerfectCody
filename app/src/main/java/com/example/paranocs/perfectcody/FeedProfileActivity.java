package com.example.paranocs.perfectcody;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class FeedProfileActivity extends AppCompatActivity {

    Button button_follow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_profile);

        button_follow = (Button)findViewById(R.id.button_follow);
        button_follow.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        button_follow.setBackgroundColor(Color.GRAY);
                        button_follow.setText("언팔로우");
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        button_follow.setBackgroundColor(Color.RED);
                        button_follow.setText("+팔로우");
                        break;
                    }
                }
                return false;
            }
        });
    }

}
