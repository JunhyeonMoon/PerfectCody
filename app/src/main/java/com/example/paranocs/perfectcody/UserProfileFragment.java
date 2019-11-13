package com.example.paranocs.perfectcody;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {
    private String TAG = getClass().getName();
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView imageView_profile;
    private TextView textView_profileEdit;
    private TextView textView_closet;
    private TextView textView_nickname;
    private TabLayout tabLayout;


    private RecyclerView recyclerView1;
    private RecyclerView recyclerView2;
    private UserPhotoRecyclerViewAdapter userPhotoRecyclerViewAdapter1;
    private UserPhotoRecyclerViewAdapter userPhotoRecyclerViewAdapter2;
    private ArrayList<Map<String, Object>> items_photo = new ArrayList<>();
    private ArrayList<Map<String, Object>> items_good = new ArrayList<>();

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
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
        storage = FirebaseStorage.getInstance();

        getUserDataFromDB();
        getPhotoFromDB();
    }

    private void init(View view){
        imageView_profile = view.findViewById(R.id.circleImageView);
        textView_profileEdit= view.findViewById(R.id.textView_profileEdit);
        textView_closet = view.findViewById(R.id.textView_myCloset);
        textView_nickname = view.findViewById(R.id.textView_nickname);
        tabLayout = view.findViewById(R.id.tab_layout);

        recyclerView1 = view.findViewById(R.id.recyclerView1);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        userPhotoRecyclerViewAdapter1 = new UserPhotoRecyclerViewAdapter(mContext, items_photo);
        userPhotoRecyclerViewAdapter2 = new UserPhotoRecyclerViewAdapter(mContext, items_good);
        recyclerView1.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView2.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView1.setAdapter(userPhotoRecyclerViewAdapter1);
        recyclerView2.setAdapter(userPhotoRecyclerViewAdapter2);

        textView_profileEdit.setOnClickListener(onClickListener);
        textView_closet.setOnClickListener(onClickListener);
        imageView_profile.setOnClickListener(onClickListener);

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

    private void changeView(int index) {
        switch (index) {
            case 0:
                recyclerView1.setVisibility(View.VISIBLE);
                recyclerView2.setVisibility(View.GONE);
                break;
            case 1:
                recyclerView1.setVisibility(View.GONE);
                recyclerView2.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void getUserDataFromDB(){
        String uid = mAuth.getCurrentUser().getUid();
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
        final String userID = mAuth.getCurrentUser().getUid();
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
                case R.id.circleImageView : {
                    //TODO 갤러리 연결해서 프로필 이미지수정
                }
                break;
                case R.id.textView_profileEdit: {
                    Intent intent = new Intent(mContext, ProfileEditActivity.class);
                    startActivity(intent);
                }
                break;
                case R.id.textView_myCloset : {
                    Intent intent = new Intent(mContext, MyClosetActivity.class);
                    startActivity(intent);
                }
                break;
            }
        }
    };
}
