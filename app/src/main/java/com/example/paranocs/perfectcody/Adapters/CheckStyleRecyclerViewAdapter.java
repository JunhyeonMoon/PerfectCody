package com.example.paranocs.perfectcody.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.CheckStyleActivity;
import com.example.paranocs.perfectcody.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class CheckStyleRecyclerViewAdapter extends RecyclerView.Adapter {
    private String TAG = getClass().getName();
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Map<String, Object>> items = new ArrayList<>();

    public CheckStyleRecyclerViewAdapter(Context mContext, ArrayList<Map<String, Object>> items){
        this.mContext = mContext;
        this.items = items;
    }

    public class CheckStyleViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView imageView_check;
        public CheckStyleViewHolder (@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView_check = itemView.findViewById(R.id.imageView_check);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_image, parent, false);
        return new CheckStyleRecyclerViewAdapter.CheckStyleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final CheckStyleRecyclerViewAdapter.CheckStyleViewHolder mHolder = (CheckStyleRecyclerViewAdapter.CheckStyleViewHolder) holder;
        Bitmap bmp = (Bitmap)items.get(position).get("bitmap");
        mHolder.imageView.setImageBitmap(bmp);

        mHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.imageView_check.setVisibility(View.VISIBLE);
                items.get(position).put("isChecked", true);
            }
        });

        mHolder.imageView_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.imageView_check.setVisibility(View.GONE);
                items.get(position).put("isChecked", false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<Integer> getClickedItems() {
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0; i < items.size(); i++){
            if((boolean)items.get(i).get("isChecked")){
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
