package com.example.paranocs.perfectcody;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultViewPagerFragment extends Fragment {
    private String TAG = getClass().getName();
    private Context mContext;
    private Map<String, Object> item = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView imageView;

    public ResultViewPagerFragment() {
        // Required empty public constructor
    }

    public ResultViewPagerFragment(Map<String, Object> item) {
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_view_pager, container, false);
        imageView = view.findViewById(R.id.imageVIew);
        Log.d(TAG, "resultviewpager " + item);
        String path = item.get("path").toString();
        Log.d(TAG, "resultviewpager " + path);

        Bitmap bmp = (Bitmap)item.get("bitmap");
        imageView.setImageBitmap(bmp);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
