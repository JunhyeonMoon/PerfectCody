package com.example.paranocs.perfectcody.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.R;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

public class UserPhotoRecyclerViewAdapter extends RecyclerView.Adapter {
    private String TAG = getClass().getName();
    private Context mContext;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Map<String, Object>> items = new ArrayList<>();

    public UserPhotoRecyclerViewAdapter(Context context, ArrayList<Map<String, Object>> items){
        this.mContext = context;
        this.items = items;
    }

    public class UserPhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public UserPhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new UserPhotoRecyclerViewAdapter.UserPhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final UserPhotoRecyclerViewAdapter.UserPhotoViewHolder mHolder = (UserPhotoRecyclerViewAdapter.UserPhotoViewHolder) holder;
        Map<String, Object> data = items.get(position);
        Glide.with(mContext).load(SingleTon.getInstance().toString(data.get("uri"))).override(500, 300).into(mHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
