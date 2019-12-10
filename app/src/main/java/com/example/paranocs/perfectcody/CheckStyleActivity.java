package com.example.paranocs.perfectcody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.paranocs.perfectcody.Adapters.CheckStyleRecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CheckStyleActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private RecyclerView recyclerView;
    private CheckStyleRecyclerViewAdapter checkStyleRecyclerViewAdapter;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_style);
        mContext = getApplicationContext();
        recyclerView = findViewById(R.id.recyclerView);
        checkStyleRecyclerViewAdapter = new CheckStyleRecyclerViewAdapter(mContext, items);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView.setAdapter(checkStyleRecyclerViewAdapter);
        button = findViewById(R.id.button);

        final ArrayList<String> manCasual = new ArrayList<String>(Arrays.asList("2.jpg", "6.jpg", "7.jpg", "14.jpg", "20.jpg", "21.jpg", "23.jpg", "25.jpg", "26.jpg", "27.jpg"));
        final ArrayList<String> manDandy = new ArrayList<String>(Arrays.asList("24.jpg", "29.jpg", "32.jpg", "36.jpg", "99.jpg", "100.jpg", "120.jpg", "166.jpg", "179.jpg", "180.jpg"));
        final ArrayList<String> manStreet = new ArrayList<String>(Arrays.asList("1.jpg", "3.jpg", "4.jpg", "5.jpg", "8.jpg", "9.jpg", "10.jpg", "11.jpg", "12.jpg", "13.jpg"));
        final ArrayList<String> womanCasual = new ArrayList<String>(Arrays.asList("45.jpg", "46.jpg", "47.jpg", "49.jpg", "50.jpg", "87.jpg", "88.jpg", "89.jpg", "90.jpg", "91.jpg"));
        final ArrayList<String> womanDandy = new ArrayList<String>(Arrays.asList("40.jpg", "41.jpg", "42.jpg", "43.jpg", "44.jpg", "59.jpg", "60.jpg", "61.jpg", "85.jpg", "94.jpg"));
        final ArrayList<String> womanStreet = new ArrayList<String>(Arrays.asList("39.jpg", "48.jpg", "51.jpg", "52.jpg", "53.jpg", "54.jpg", "55.jpg", "56.jpg", "57.jpg", "58.jpg"));

        String uid = mAuth.getCurrentUser().getUid();
        String dbPath = getString(R.string.db_users) + "/" + uid;
        items.clear();
        db.document(dbPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    if(docData.get("gender").toString().equals("M")){
                        for(int i = 0; i < manCasual.size(); i++) {
                            String path = getString(R.string.storage_codySet) + "/" +
                                    getString(R.string.storage_manCasual) + "/" +
                                    manCasual.get(i);

                            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("bitmap", resource);
                                                doc.put("style", "casual");
                                                doc.put("isChecked", false);
                                                items.add(doc);
                                                Log.d(TAG, "doc: " + doc);
                                                checkStyleRecyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                }
                            });

                            path = getString(R.string.storage_codySet) + "/" +
                                    getString(R.string.storage_manDandy) + "/" +
                                    manDandy.get(i);

                            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("bitmap", resource);
                                                doc.put("style", "dandy");
                                                doc.put("isChecked", false);
                                                items.add(doc);
                                                Log.d(TAG, "doc: " + doc);
                                                checkStyleRecyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                }
                            });

                            path = getString(R.string.storage_codySet) + "/" +
                                    getString(R.string.storage_manStreet) + "/" +
                                    manStreet.get(i);

                            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("bitmap", resource);
                                                doc.put("style", "street");
                                                doc.put("isChecked", false);
                                                items.add(doc);
                                                Log.d(TAG, "doc: " + doc);
                                                checkStyleRecyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }else{
                        for(int i = 0; i < womanCasual.size(); i++){
                            String path = getString(R.string.storage_codySet) + "/" +
                                    getString(R.string.storage_womanCasual) + "/" +
                                    womanCasual.get(i);

                            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("bitmap", resource);
                                                doc.put("style", "casual");
                                                doc.put("isChecked", false);
                                                items.add(doc);
                                                Log.d(TAG, "doc: " + doc);
                                                checkStyleRecyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                }
                            });
                            path = getString(R.string.storage_codySet) + "/" +
                                    getString(R.string.storage_womanDandy) + "/" +
                                    womanDandy.get(i);

                            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("bitmap", resource);
                                                doc.put("style", "dandy");
                                                doc.put("isChecked", false);
                                                items.add(doc);
                                                Log.d(TAG, "doc: " + doc);
                                                checkStyleRecyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                            }
                                        });
                                    }
                                }
                            });

                            path = getString(R.string.storage_codySet) + "/" +
                                    getString(R.string.storage_womanStreet) + "/" +
                                    womanStreet.get(i);

                            storage.getReference(path).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(mContext).asBitmap().load(task.getResult()).into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                Map<String, Object> doc = new HashMap<>();
                                                doc.put("bitmap", resource);
                                                doc.put("style", "street");
                                                doc.put("isChecked", false);
                                                items.add(doc);
                                                Log.d(TAG, "doc: " + doc);
                                                checkStyleRecyclerViewAdapter.notifyDataSetChanged();
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
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> l = checkStyleRecyclerViewAdapter.getClickedItems();
                if(l.size() != 10){
                    Toast.makeText(mContext, "10개를 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                float c = 0;
                float d = 0;
                float s = 0;
                for(int i = 0; i < l.size(); i++){
                    if(0 <= l.get(i) && l.get(i) < 10){
                        c++;
                    }else if(10 <= l.get(i) && l.get(i) < 20){
                        d++;
                    }else if(11 <= l.get(i) && l.get(i) < 30){
                        s++;
                    }
                }
                c = c/10.f;
                d = d/10.f;
                s = s/10.f;

                String uid = mAuth.getCurrentUser().getUid();
                String dbPath = getString(R.string.db_users) + "/" + uid;
                Map<String, Object> data = new HashMap<>();
                data.put("Casual", Float.toString(c));
                data.put("Dandy", Float.toString(d));
                data.put("Street", Float.toString(s));
                db.document(dbPath).set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }
}
