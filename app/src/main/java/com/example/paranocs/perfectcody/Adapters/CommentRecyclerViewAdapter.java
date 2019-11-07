package com.example.paranocs.perfectcody.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Map;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter {
    private String TAG = getClass().getName();
    private Context mContext;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_profile;
        TextView textView_nickname;
        TextView textView_comment;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView_profile = itemView.findViewById(R.id.imageView_profile);
            this.textView_nickname = itemView.findViewById(R.id.textView_nickname);
            this.textView_comment = itemView.findViewById(R.id.textView_comment);
        }
    }

    public CommentRecyclerViewAdapter(Context context, ArrayList<Map<String, Object>> items){
        this.mContext = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CommentViewHolder mHolder = (CommentViewHolder) holder;
        String uid = toString(items.get(position).get("uid"));
        String comment = toString(items.get(position).get("comment"));
        db.document(mContext.getString(R.string.db_users) + "/" + uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    mHolder.textView_nickname.setText(docData.get("nickname").toString());
                    Glide.with(mContext).load(docData.get("profile").toString()).into(mHolder.imageView_profile);
                }
            }
        });
        mHolder.textView_comment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String toString(Object o){
        return o != null ? o.toString() : "0";
    }
}
