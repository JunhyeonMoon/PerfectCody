package com.example.paranocs.perfectcody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.paranocs.perfectcody.Adapters.MyClosetRecyclerViewAdapter;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyClosetActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;

    private Button button_recommend;
    private FloatingActionButton floatingButton_add;
    private RecyclerView recyclerView;
    private MyClosetRecyclerViewAdapter myClosetRecyclerViewAdapter;
    private ArrayList<Map<String, Object>> items = new ArrayList<>();
    private int REQUEST_PHOTO = 100;
    private int PERMISSION_CODE = 101;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RequestQueue queue;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_closet);
        mContext = getApplicationContext();
        queue = Volley.newRequestQueue(this);
        button_recommend = findViewById(R.id.button_recommend);
        floatingButton_add = findViewById(R.id.floatingButton_add);
        progressBar = findViewById(R.id.progressBar);
        button_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> clicked = myClosetRecyclerViewAdapter.getClickedItems();
                if(clicked.size() != 1){
                    Toast.makeText(mContext, "하나의 옷을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> data = items.get(clicked.get(0));
                String uri = data.get("uri").toString();
                progressBar.setVisibility(View.VISIBLE);
                sendRequest(uri);
                //intent.putExtra("uri", uri);
                //startActivity(intent);
            }
        });

        floatingButton_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //권한체크
                int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "갤러리 권한 있음", Toast.LENGTH_SHORT).show();
                    getPhoto();
                }else {
                    Toast.makeText(getApplicationContext(), "갤러리 권한 없음", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(
                            MyClosetActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_CODE);
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        myClosetRecyclerViewAdapter = new MyClosetRecyclerViewAdapter(mContext, items);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerView.setAdapter(myClosetRecyclerViewAdapter);
        getItemsFromDB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PHOTO){
            if(resultCode == RESULT_OK){
                Uri photoUri = data.getData();
                String path = getRealPathFromURI(photoUri);
                uploadPhoto(path);
            }else{
                Toast.makeText(mContext, "cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        getItemsFromDB();
    }


    private void getPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_PHOTO);
    }


    private void uploadPhoto(String path){
        Uri file = Uri.fromFile(new File(path));
        BitmapFactory.Options bm = new BitmapFactory.Options();
        Bitmap b = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        Bitmap cb = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        final String fileName = file.getLastPathSegment();
        final String uid = mAuth.getCurrentUser().getUid();
        final StorageReference ref = storage.getReference().child(getString(R.string.storage_closet) +"/" + uid + "/" + fileName);
        UploadTask uploadTask = ref.putBytes(byteArray);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    //db에 업로드 정보갱신
                    Uri downloadUri = task.getResult();
                    Map<String, Object> data = new HashMap<>();
                    data.put("uri", downloadUri.toString());
                    data.put("uid", uid);
                    String dbPath = getString(R.string.db_users) + "/" +  uid + "/" + getString(R.string.db_closet);
                    db.collection(dbPath).add(data);
                    data.put("isChecked", false);
                    items.add(data);
                    myClosetRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public String getRealPathFromURI(Uri contentURI) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            return s;
        }
        return null;
    }

    private void getItemsFromDB(){
        String uid = mAuth.getCurrentUser().getUid();
        String dbPath = getString(R.string.db_users) + "/" + uid + "/" + getString(R.string.db_closet);
        db.collection(dbPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    items.clear();
                    for(DocumentSnapshot document : task.getResult().getDocuments()){
                        Map<String, Object> docData = document.getData();
                        docData.put("isChecked", false);
                        items.add(docData);
                    }
                    myClosetRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void sendRequest(final String uri){
        String url = "https://5f6cubbbhi.execute-api.ap-northeast-2.amazonaws.com/version1";
        JSONObject jsonObject = new JSONObject();
        try {
            //jsonObject.put("uri", SingleTon.getInstance().uri);
            jsonObject.put("uri", uri);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        Intent intent = new Intent(mContext, CodyRecommendationActivity.class);

                        try {
                            progressBar.setVisibility(View.GONE);
                            String errorMsg = response.get("error_message").toString();
                            if(!errorMsg.equals("")){
                                Toast.makeText(mContext, "올바른 옷 사진이 아닙니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String boxing_info = response.get("boxing_info").toString();
                            String cloth_type = response.get("cloth_type").toString();
                            String color = response.get("rgb").toString();
                            String upper_category = response.get("upper_category").toString();
                            String upper_pattern = response.get("upper_pattern").toString();
                            intent.putExtra("uri", uri);
                            intent.putExtra("boxing_info", boxing_info);
                            intent.putExtra("cloth_type", cloth_type);
                            intent.putExtra("color", color);
                            intent.putExtra("upper_category", upper_category);
                            intent.putExtra("upper_pattern", upper_pattern);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "응답 에러", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Response error : " + error);
                        Toast.makeText(mContext, "응답 에러", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }

}
