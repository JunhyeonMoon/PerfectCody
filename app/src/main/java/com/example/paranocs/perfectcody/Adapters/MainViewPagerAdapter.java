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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.okhttp.internal.Internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainViewPagerAdapter extends PagerAdapter {
    private String TAG = getClass().getName();
    private View mCurrentView;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private ArrayList<Map<String, Object>> commentItems = new ArrayList<>();
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
        init(view, position);
        String uri = items.get(position).get("uri").toString();
        Glide.with(mContext).load(uri).into(imageView);

        db.document(mContext.getString(R.string.db_users) + "/" +
                toString(items.get(position).get("uid"))).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    Glide.with(mContext).load(docData.get("profile").toString()).into(imageView_feedProfile);
                    imageView_feedProfile.setBackground(new ShapeDrawable(new OvalShape()));
                    imageView_feedProfile.setClipToOutline(true);
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

        final String docID = toString(items.get(position).get("docID"));
        final String uid = toString(items.get(position).get("uid"));
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

                        if(target.equals("good")){
                            textView_good.setText(Integer.toString(target_int));
                        }else if(target.equals("bad")){
                            textView_bad.setText(Integer.toString(target_int));
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

        textView_good.setText(toString(items.get(position).get("good")));
        textView_bad.setText(toString(items.get(position).get("bad")));
        textView_comment.setText(toString(items.get(position).get("comment")));
    }

    private String toString(Object o){
        return o != null ? o.toString() : "0";
    }

}
