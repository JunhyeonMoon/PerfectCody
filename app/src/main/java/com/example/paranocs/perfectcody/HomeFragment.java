package com.example.paranocs.perfectcody;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.paranocs.perfectcody.Utils.VerticalViewPager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String TAG = getClass().getName();
    private Context mContext;

    private VerticalViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();
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
        getPhotoFromDB();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void init(View view){
        progressBar = view.findViewById(R.id.progressBar);
        viewPager = (VerticalViewPager) view.findViewById(R.id.viewPager);
        fragmentAdapter = new FragmentAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(fragmentAdapter);
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
                        fragments.clear();
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Object> docData = document.getData();
                            docData.put("docID", document.getId());
                            items.add(docData);
                            ViewPagerFragment viewPagerFragment = new ViewPagerFragment(docData);
                            fragments.add(viewPagerFragment);
                        }
                        fragmentAdapter.notifyDataSetChanged();
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

    class FragmentAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();
        public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
