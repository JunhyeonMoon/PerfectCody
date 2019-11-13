package com.example.paranocs.perfectcody;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;
    private LinearLayout imageView_home;
    private LinearLayout imageView_search;
    private LinearLayout imageView_favorite;
    private LinearLayout imageView_profile;
    private LinearLayout imageView_addPhoto;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private UserProfileFragment userProfileFragment;
    private SearchFragment searchFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private int REQUEST_PHOTO = 100;
    private int PERMISSION_CODE = 101;

    RequestQueue queue;

//  애니메이션 효과 주기위한 변수
    private Animation scale;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scale = AnimationUtils.loadAnimation(this, R.anim.gps_button_animation);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.imageView_home:{
                    v.startAnimation(scale);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
                }
                break;
                case R.id.imageView_search:{
                    v.startAnimation(scale);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, searchFragment).commit();
                }
                break;
                case R.id.imageView_favorite:{
                    v.startAnimation(scale);
                    sendRequest();
                }
                break;
                case R.id.imageView_profile:{
                    v.startAnimation(scale);
                    if(mAuth.getCurrentUser() == null){
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, userProfileFragment).commit();
                    }
                }
                break;
                case R.id.imageView_addPhoto:{
                    v.startAnimation(scale);
                    if(mAuth.getCurrentUser() == null){
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        //권한체크
                        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getApplicationContext(), "갤러리 권한 있음", Toast.LENGTH_SHORT).show();
                            getPhoto();
                        }else {
                            Toast.makeText(getApplicationContext(), "갤러리 권한 없음", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_CODE);
                        }
                    }
                }
                break;

            }
        }
    };

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

    private void getPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_PHOTO);
    }

    private void uploadPhoto(String path){
        Uri file = Uri.fromFile(new File(path));
        final String fileName = file.getLastPathSegment();
        final String uid = mAuth.getCurrentUser().getUid();
        final StorageReference ref = storage.getReference().child(getString(R.string.storage_userPhoto) +"/" + uid + "/" + fileName);
        UploadTask uploadTask = ref.putFile(file);

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
                    data.put("good", 0);
                    data.put("bad", 0);
                    data.put("comment", 0);
                    data.put("uid", uid);
                    db.collection(getString(R.string.db_photo)).add(data);
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

    private void sendRequest(){
        String url = "https://5f6cubbbhi.execute-api.ap-northeast-2.amazonaws.com/version1";
        String filename = "model1.jpg";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uri", SingleTon.getInstance().uri);
            jsonObject.put("filename", filename);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        Intent intent = new Intent(mContext, CheckImageDetectionActivity.class);
                        intent.putExtra("uri", SingleTon.getInstance().uri);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void init(){
        mContext = getApplicationContext();
        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        userProfileFragment = new UserProfileFragment();
        searchFragment = new SearchFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
        queue = Volley.newRequestQueue(this);

        imageView_home = findViewById(R.id.imageView_home);
        imageView_search = findViewById(R.id.imageView_search);
        imageView_favorite = findViewById(R.id.imageView_favorite);
        imageView_profile = findViewById(R.id.imageView_profile);
        imageView_addPhoto = findViewById(R.id.imageView_addPhoto);

        imageView_home.setOnClickListener(onClickListener);
        imageView_search.setOnClickListener(onClickListener);
        imageView_favorite.setOnClickListener(onClickListener);
        imageView_profile.setOnClickListener(onClickListener);
        imageView_addPhoto.setOnClickListener(onClickListener);
    }

}
