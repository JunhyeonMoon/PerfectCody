package com.example.paranocs.perfectcody;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.paranocs.perfectcody.Adapters.MainViewPagerAdapter;
import com.example.paranocs.perfectcody.Utils.VerticalViewPager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;
    private ImageView imageView_home;
    private ImageView imageView_search;
    private ImageView imageView_favorite;
    private ImageView imageView_profile;
    private ImageView imageView_addPhoto;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private UserProfileFragment userProfileFragment;
    private SearchFragment searchFragment;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private int REQUEST_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();
                }
                break;

                case R.id.imageView_search:{
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, searchFragment).commit();
                }
                break;
                case R.id.imageView_favorite:{

                }
                break;
                case R.id.imageView_profile:{
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
                    if(mAuth.getCurrentUser() == null){
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        //권한체크
                        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getApplicationContext(), "SMS 수신권한 있음", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();

                        }

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(intent, REQUEST_PHOTO);
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
                    data.put("uri", downloadUri);
                    data.put("good", 0);
                    data.put("bad", 0);
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
            cursor.close();
            return s;
        }
        cursor.close();
        return null;
    }

    private void init(){
        mContext = getApplicationContext();
        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        userProfileFragment = new UserProfileFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, homeFragment).commit();

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
