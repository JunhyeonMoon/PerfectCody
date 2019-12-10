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

import java.util.ArrayList;
import java.util.Map;

public class MyClosetRecyclerViewAdapter extends RecyclerView.Adapter {
    private String TAG = getClass().getName();
    private Context mContext;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();

    public MyClosetRecyclerViewAdapter(Context mContext, ArrayList<Map<String, Object>> items){
        this.mContext = mContext;
        this.items = items;
    }

    public class ClosetViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView imageView_check;
        public ClosetViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView_check = itemView.findViewById(R.id.imageView_check);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_closet, parent, false);
        return new MyClosetRecyclerViewAdapter.ClosetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MyClosetRecyclerViewAdapter.ClosetViewHolder mHolder = (MyClosetRecyclerViewAdapter.ClosetViewHolder) holder;
        Map<String, Object> data = items.get(position);
        Glide.with(mContext).load(SingleTon.getInstance().toString(data.get("uri"))).override(500, 300).into(mHolder.imageView);
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
}
