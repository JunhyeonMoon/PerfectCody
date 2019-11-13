package com.example.paranocs.perfectcody;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.Adapters.CommentRecyclerViewAdapter;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {
    private String TAG = getClass().getName();
    private Context mContext;
    private Map<String, Object> item = new HashMap<>();

    private ArrayList<Map<String, Object>> commentItems = new ArrayList<>();
    private ArrayList<String> goodUid = new ArrayList<>();
    private ArrayList<String> badUid = new ArrayList<>();

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

    public ViewPagerFragment(){}
    public ViewPagerFragment(Map<String, Object> item) {
        this.item = item;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        singleTon = SingleTon.getInstance();
        init(view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        singleTon = SingleTon.getInstance();
        String uri = item.get("uri").toString();

        //TODO 이미지 체크를 위한 임시코드 나중에 삭제
        SingleTon.getInstance().uri = uri;
    }

    void init(View view) {
        imageView = view.findViewById(R.id.imageView);
        imageView_good = view.findViewById(R.id.imageView_good);
        imageView_bad = view.findViewById(R.id.imageView_bad);
        imageView_good.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        imageView_bad.setImageResource(R.drawable.ic_thumb_down_black_24dp);
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


        Log.d(TAG, "init: " + item.get("docID"));
        final String docID = singleTon.toString(item.get("docID"));
        final String uid = singleTon.toString(item.get("uid"));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageView_bad : {
                        if(mAuth.getCurrentUser() == null){
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        }

                        Map<String, Object> data = new HashMap<>();
                        int bad_int = Integer.parseInt(item.get("bad").toString());
                        if(!isCheckbad){
                            bad_int++;
                            item.put("bad", bad_int);
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
                            item.put("bad", bad_int);
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
                        if(mAuth.getCurrentUser() == null){
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        }
                        Map<String, Object> data = new HashMap<>();
                        int good_int = Integer.parseInt(item.get("good").toString());
                        if(!isCheckGood){
                            good_int++;
                            item.put("good", good_int);
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
                            item.put("good", good_int);
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
                                        Map<String, Object> commentSize = new HashMap<>();
                                        commentSize.put("comment", commentItems.size());
                                        db.document(getString(R.string.db_photo) + "/" + singleTon.toString(item.get("docID")))
                                                .update(commentSize);
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
                            final Map<String, Object> commentData = new HashMap<>();
                            commentData.put("uid", uid);
                            commentData.put("comment", comment);
                            db.collection(mContext.getString(R.string.db_photo) + "/" + docID + "/" +
                                    mContext.getString(R.string.db_photo_comment)).add(commentData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        commentItems.add(commentData);
                                        commentRecyclerViewAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
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

        textView_good.setText(singleTon.toString(item.get("good")));
        textView_bad.setText(singleTon.toString(item.get("bad")));
        textView_comment.setText(singleTon.toString(item.get("comment")));



        String uri = item.get("uri").toString();
        Glide.with(mContext).load(uri).into(imageView);

        db.document(mContext.getString(R.string.db_users) + "/" +
                singleTon.toString(item.get("uid"))).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

        db.document(getString(R.string.db_photo) + "/" + singleTon.toString(item.get("docID")))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Map<String ,Object> docData = task.getResult().getData();
                            textView_commentNum.setText("댓글 " + singleTon.toString(docData.get("comment")) + "개");
                        }
                    }
                });

        if(mAuth.getCurrentUser() != null) {
            db.document(mContext.getString(R.string.db_photo) + "/" +
                    singleTon.toString(item.get("docID")))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> docData = task.getResult().getData();
                        if (docData.containsKey("goodUid")) {
                            goodUid = (ArrayList<String>) docData.get("goodUid");
                            String uid = mAuth.getCurrentUser().getUid();
                            if (goodUid.contains(uid)) {
                                isCheckGood = true;
                                imageView_good.setImageResource(R.drawable.ic_thumb_up_red_24dp);
                            }
                        }

                        if (docData.containsKey("badUid")) {
                            badUid = (ArrayList<String>) docData.get("badUid");
                            String uid = mAuth.getCurrentUser().getUid();
                            if (badUid.contains(uid)) {
                                isCheckbad = true;
                                imageView_bad.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                            }
                        }
                    }
                }
            });
        }
    }
}
