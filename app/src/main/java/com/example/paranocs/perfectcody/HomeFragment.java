package com.example.paranocs.perfectcody;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.paranocs.perfectcody.Adapters.MainViewPagerAdapter;
import com.example.paranocs.perfectcody.Utils.VerticalViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String TAG = getClass().getName();
    private Context mContext;

    private MainViewPagerAdapter mainViewPagerAdapter;
    private VerticalViewPager viewPager;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void init(View view){
        progressBar = view.findViewById(R.id.progressBar);
        viewPager = (VerticalViewPager) view.findViewById(R.id.viewPager);
        mainViewPagerAdapter = new MainViewPagerAdapter(mContext, items);
        viewPager.setAdapter(mainViewPagerAdapter);
        getPhotoFromDB();
    }

    private void getPhotoFromDB(){
        Query query = db.collection(getString(R.string.db_photo)).orderBy("good", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getDocuments().size() > 0){
                        loading(true);
                        items.clear();
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Object> docData = document.getData();
                            docData.put("docID", document.getId());
                            items.add(docData);
                        }
                        mainViewPagerAdapter.notifyDataSetChanged();
                        loading(false);
                    }
                }
            }
        });
    }

    private void loading(boolean b){
        if(b){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }
}
