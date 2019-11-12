package com.example.paranocs.perfectcody.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.renderscript.Allocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.FeedProfileActivity;
import com.example.paranocs.perfectcody.LoginActivity;
import com.example.paranocs.perfectcody.R;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.okhttp.internal.Internal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainViewPagerAdapter extends PagerAdapter {
    private String TAG = getClass().getName();
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private ArrayList<Map<String, Object>> commentItems = new ArrayList<>();
    private ArrayList<String> goodUid = new ArrayList<>();
    private ArrayList<String> badUid = new ArrayList<>();

    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private SingleTon singleTon;
    private boolean isCheckGood = false;
    private boolean isCheckbad = false;

    private ImageView imageView;
    private ImageView imageView_good;
    private ImageView imageView_bad;
    private ImageView imageView_comment;
    private ImageView imageView_feedProfile;
    private TextView textView_good;
    private TextView textView_bad;
    private TextView textView_comment;

    private ConstraintLayout layout_comment;
    private TextView textView_commentNum;
    private ImageView imageView_closeComment;
    private RecyclerView recyclerView_comment;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private EditText editText_comment;
    private ImageView imageView_addComment;


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
        View view = inflater.inflate(R.layout.main_pager, container, false);

        container.addView(view);
        return view;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.setPrimaryItem(container, position, object);
        View view = (View)object;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        singleTon = SingleTon.getInstance();
        init(view, position);
        String uri = items.get(position).get("uri").toString();
        Glide.with(mContext).load(uri).into(imageView);

        db.document(mContext.getString(R.string.db_users) + "/" +
                singleTon.toString(items.get(position).get("uid"))).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    Glide.with(mContext).load(singleTon.toString(docData.get("profile"))).into(imageView_feedProfile);
                    imageView_feedProfile.setBackground(new ShapeDrawable(new OvalShape()));
                    imageView_feedProfile.setClipToOutline(true);
                }
            }
        });

        db.document(mContext.getString(R.string.db_photo) + "/" +
                singleTon.toString(items.get(position).get("docID")))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    if(docData.containsKey("goodUid")){
                        goodUid = (ArrayList<String>) docData.get("goodUid");
                        String uid = mAuth.getCurrentUser().getUid();
                        if(goodUid.contains(uid)){
                            isCheckGood = true;
                            imageView_good.setImageResource(R.drawable.ic_thumb_up_red_24dp);
                        }
                    }

                    if(docData.containsKey("badUid")){
                        badUid = (ArrayList<String>) docData.get("badUid");
                        String uid = mAuth.getCurrentUser().getUid();
                        if(badUid.contains(uid)){
                            isCheckbad = true;
                            imageView_bad.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                        }
                    }
                }
            }
        });

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

        layout_comment = view.findViewById(R.id.layout_comment);
        textView_commentNum = view.findViewById(R.id.textView_commentNum);
        imageView_closeComment = view.findViewById(R.id.imageView_closeComment);
        editText_comment = view.findViewById(R.id.editText_comment);
        imageView_addComment = view.findViewById(R.id.imageView_addComment);

        recyclerView_comment = view.findViewById(R.id.recyclerView_comment);
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(mContext, commentItems);
        recyclerView_comment.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView_comment.setAdapter(commentRecyclerViewAdapter);

        final String docID = singleTon.toString(items.get(position).get("docID"));
        final String uid = singleTon.toString(items.get(position).get("uid"));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView_bad : {
                        Map<String, Object> data = new HashMap<>();
                        int bad_int = Integer.parseInt(items.get(position).get("bad").toString());
                        if(!isCheckbad){
                            bad_int++;
                            items.get(position).put("bad", bad_int);
                            data.put("bad", bad_int);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(data);

                            String userID = mAuth.getCurrentUser().getUid();

                            Map<String, Object> newData = new HashMap<>();
                            badUid.add(userID);
                            newData.put("badUid", badUid);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(newData);

                            textView_bad.setText(Integer.toString(bad_int));
                            isCheckbad = true;

                            imageView_bad.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                        }else{
                            bad_int--;
                            items.get(position).put("bad", bad_int);
                            data.put("bad", bad_int);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(data);

                            Map<String, Object> newData = new HashMap<>();
                            badUid.remove(mAuth.getCurrentUser().getUid());
                            newData.put("badUid", badUid);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(newData);

                            textView_bad.setText(Integer.toString(bad_int));
                            isCheckbad = false;

                            imageView_bad.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                        }
                    }
                    break;
                    case R.id.imageView_good : {
                        Map<String, Object> data = new HashMap<>();
                        int good_int = Integer.parseInt(items.get(position).get("good").toString());
                        if(!isCheckGood){
                            good_int++;
                            items.get(position).put("good", good_int);
                            data.put("good", good_int);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(data);

                            String userID = mAuth.getCurrentUser().getUid();
                            Map<String, Object> newData = new HashMap<>();
                            goodUid.add(userID);
                            newData.put("goodUid", goodUid);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(newData);

                            textView_good.setText(Integer.toString(good_int));
                            isCheckGood = true;
                            imageView_good.setImageResource(R.drawable.ic_thumb_up_red_24dp);
                        }else{
                            good_int--;
                            items.get(position).put("good", good_int);
                            data.put("good", good_int);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(data);


                            String userID = mAuth.getCurrentUser().getUid();
                            Map<String, Object> newData = new HashMap<>();
                            goodUid.remove(userID);
                            newData.put("goodUid", goodUid);
                            db.document(mContext.getString(R.string.db_photo) + "/"
                                    + docID).update(newData);

                            textView_good.setText(Integer.toString(good_int));
                            isCheckGood = false;
                            imageView_good.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        }
                    }
                    break;

                    case R.id.imageView_comment : {
                        layout_comment.setVisibility(View.VISIBLE);
                        commentItems.clear();
                        commentRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "sd " + docID);
                        db.collection(mContext.getString(R.string.db_photo) + "/" +
                                docID + "/" +
                                mContext.getString(R.string.db_photo_comment) + "/").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().getDocuments().size() > 0){
                                        commentItems.clear();
                                        for(QueryDocumentSnapshot document : task.getResult()){
                                            commentItems.add(document.getData());
                                        }
                                        commentRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                    break;
                    case R.id.imageView_feedProfile: {
                        Intent intent = new Intent(mContext, FeedProfileActivity.class);
                        intent.putExtra("uid", uid);
                        mContext.startActivity(intent);
                    }
                    break;
                    case R.id.imageView_addComment:{
                        if(mAuth.getCurrentUser() == null){
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        }else{
                            String uid = mAuth.getCurrentUser().getUid();
                            String comment = editText_comment.getText().toString();
                            Map<String, Object> commentData = new HashMap<>();
                            commentData.put("uid", uid);
                            commentData.put("comment", comment);
                            db.collection(mContext.getString(R.string.db_photo) + "/" + docID + "/" +
                                    mContext.getString(R.string.db_photo_comment)).add(commentData);
                            commentItems.add(commentData);
                            commentRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                    case R.id.imageView_closeComment:{
                        layout_comment.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        };
        imageView_good.setOnClickListener(onClickListener);
        imageView_bad.setOnClickListener(onClickListener);
        imageView_comment.setOnClickListener(onClickListener);
        imageView_feedProfile.setOnClickListener(onClickListener);
        imageView_addComment.setOnClickListener(onClickListener);
        imageView_closeComment.setOnClickListener(onClickListener);

        textView_good.setText(singleTon.toString(items.get(position).get("good")));
        textView_bad.setText(singleTon.toString(items.get(position).get("bad")));
        textView_comment.setText(singleTon.toString(items.get(position).get("comment")));
    }

}
