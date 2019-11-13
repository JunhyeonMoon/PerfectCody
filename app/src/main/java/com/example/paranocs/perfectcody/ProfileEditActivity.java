package com.example.paranocs.perfectcody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView circleImageVIew;
    private TextView user_nickname;
    private TextView edit_nickname;
    private TextView edit_photo;
    private TextView user_nickname2;

    private int REQUEST_PHOTO = 100;
    private int PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        mContext = getApplicationContext();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        getUserDataFromDB();
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

    private void init(){
        circleImageVIew = findViewById(R.id.circleImageView);
        user_nickname = findViewById(R.id.user_nickname);
        user_nickname2 = findViewById(R.id.user_nickname2);
        edit_photo = findViewById(R.id.edit_photo);
        edit_nickname = findViewById(R.id.edit_nickname);

        edit_photo.setOnClickListener(onClickListener);
        edit_nickname.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.edit_photo : {
                    //권한체크
                    int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getApplicationContext(), "갤러리 권한 있음", Toast.LENGTH_SHORT).show();
                        getPhoto();
                    }else {
                        Toast.makeText(getApplicationContext(), "갤러리 권한 없음", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(
                                ProfileEditActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_CODE);
                    }

                }
                break;
                case R.id.edit_nickname : {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEditActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_edittext, null);
                    builder.setView(dialogView)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int id) {
                                    EditText editText = dialogView.findViewById(R.id.editText_nickname);
                                    String newName = SingleTon.getInstance().toString(editText.getText());
                                    if(newName.equals("")){
                                        Toast.makeText(mContext, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String uid = mAuth.getCurrentUser().getUid();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("nickname", newName);
                                        db.document(getString(R.string.db_users) + "/" + uid).update(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(mContext, "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show();
                                                    getUserDataFromDB();
                                                    dialog.cancel();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;

            }
        }
    };

    private void getUserDataFromDB(){
        String uid = mAuth.getCurrentUser().getUid();
        db.document(mContext.getString(R.string.db_users)+"/"+uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> docData = task.getResult().getData();
                    user_nickname.setText(SingleTon.getInstance().toString(docData.get("nickname")));
                    user_nickname2.setText(SingleTon.getInstance().toString(docData.get("nickname")));
                    Glide.with(mContext).load(SingleTon.getInstance().toString(docData.get("profile"))).into(circleImageVIew);
                }
            }
        });
    }

    private void getPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUEST_PHOTO);
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

    private void uploadPhoto(String path){
        Uri file = Uri.fromFile(new File(path));
        final String fileName = file.getLastPathSegment();
        final String uid = mAuth.getCurrentUser().getUid();
        final StorageReference ref = storage.getReference().child(getString(R.string.storage_userProfile) +"/" + uid + "/" + fileName);
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
                    data.put("profile", downloadUri.toString());
                    db.document(getString(R.string.db_users) + "/" + uid).update(data);
                    getUserDataFromDB();
                }
            }
        });

    }
}
