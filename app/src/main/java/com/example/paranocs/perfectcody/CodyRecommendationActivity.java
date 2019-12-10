package com.example.paranocs.perfectcody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.paranocs.perfectcody.Utils.SingleTon;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class CodyRecommendationActivity extends AppCompatActivity {
    private String TAG = getClass().getName();
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private String uri = "";
    private String boxing_info = "";
    private String cloth_type = "";
    private String color = "";
    private String upper_category = "";
    private String upper_pattern = "";

    private ImageView imageView;
    private ImageView imageView_color;
    private ColorPickerDialog colorPickerDialog;
    private Spinner spinner_category;
    private ArrayList<String> category = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    private Spinner spinner_pattern;
    private ArrayList<String> pattern = new ArrayList<>();
    private ArrayAdapter<String> patternAdapter;
    private TextView textView_pattern;

    private Spinner spinner_upper_category;
    private ArrayList<String> upperCategory = new ArrayList<>();
    private ArrayAdapter<String> upperCategoryAdapter;
    private TextView textView_upper_category;

    private Button button_recommend;
    private RequestQueue queue;
    private ProgressBar progressBar;
    final ArrayList<String> cloth_type_en = new ArrayList<String>(
            Arrays.asList("short_sleeve_top", "long_sleeve_top","short_sleeve_outwear","long_sleeve_outwear","vest","sling","shorts","trousers","skirt","short_sleeve_dress","long_sleeve_dress","vest_dress","sling_dress"));

    private int userStyle = 0;
    private int userSex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cody_recommendation);
        mContext = getApplicationContext();
        queue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progressBar);

        uri = getIntent().getStringExtra("uri");
        boxing_info = getIntent().getStringExtra("boxing_info");
        cloth_type = getIntent().getStringExtra("cloth_type");
        color = getIntent().getStringExtra("color");
        upper_category = getIntent().getStringExtra("upper_category");
        upper_pattern = getIntent().getStringExtra("upper_pattern");

        boxing_info = boxing_info.split("'")[1];
        cloth_type = cloth_type.split("'")[1];
        color = color.split("'")[1];
        upper_category = upper_category.split("'")[1];
        upper_pattern = upper_pattern.split("'")[1];

        if(boxing_info.equals(".")){
            boxing_info = "";
        }
        if(cloth_type.equals(".")){
            cloth_type = "";
        }
        if(color.equals(".")){
            color= "";
        }
        if(upper_category.equals(".")){
            upper_category = "";
        }
        if(upper_pattern.equals(".")){
            upper_pattern = "";
        }


        imageView = findViewById(R.id.imageView);
        imageView_color = findViewById(R.id.imageView_color);
        Glide.with(mContext).asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
                Bitmap background = resource;
                Canvas canvas = new Canvas(background);
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(5f);

                int width = imageView.getDrawable().getIntrinsicWidth();
                int height = imageView.getDrawable().getIntrinsicHeight();
                int left = Integer.parseInt(boxing_info.split(" ")[0]);
                int top = Integer.parseInt(boxing_info.split(" ")[1]);
                int right = Integer.parseInt(boxing_info.split(" ")[2]);
                int bottom = Integer.parseInt(boxing_info.split(" ")[3]);
                canvas.drawRect(left, top, right, bottom, paint);
                imageView.setImageBitmap(background);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this);
        String[] color_rgb = color.split(" ");
        colorPickerDialog.setInitialColor(Color.rgb(Integer.parseInt(color_rgb[0]), Integer.parseInt(color_rgb[1]), Integer.parseInt(color_rgb[2])));
        imageView_color.setBackgroundColor(Color.rgb(Integer.parseInt(color_rgb[0]), Integer.parseInt(color_rgb[1]), Integer.parseInt(color_rgb[2])));
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                Log.d(TAG, "onColorPicked: " + color + " " + hexVal);
                imageView_color.setBackgroundColor(color);
            }
        });

        imageView_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerDialog.show();
            }
        });

        spinner_category = findViewById(R.id.spinner_category);
        category.add("반팔 티셔츠");
        category.add("긴팔 티셔츠");
        category.add("반팔 외투");
        category.add("긴팔 외투");
        category.add("베스트");
        category.add("민소매");
        category.add("짧은 바지");
        category.add("바지");
        category.add("스커트");
        category.add("반팔 드레스");
        category.add("긴팔 드레스");
        category.add("베스트 드레스");
        category.add("민소매 드레스");
        spinnerAdapter = new ArrayAdapter(mContext, R.layout.custom_spinner, category);
        spinner_category.setAdapter(spinnerAdapter);
        spinner_category.setSelection(cloth_type_en.indexOf(cloth_type));
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cloth_type = cloth_type_en.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textView_pattern = findViewById(R.id.textView_pattern);
        spinner_pattern = findViewById(R.id.spinner_pattern);
        Log.d(TAG, "pattern " + upper_pattern);
        if(!upper_pattern.equals("")){
            pattern.add("check");
            pattern.add("dot");
            pattern.add("printed");
            pattern.add("simple");
            pattern.add("stripe");
            patternAdapter = new ArrayAdapter(mContext, R.layout.custom_spinner, pattern);
            spinner_pattern.setAdapter(patternAdapter);
            spinner_pattern.setSelection(pattern.indexOf(upper_pattern));
            spinner_pattern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    upper_pattern = pattern.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            spinner_pattern.setVisibility(View.GONE);
            textView_pattern.setVisibility(View.GONE);
        }

        textView_upper_category = findViewById(R.id.textView_upper_category);
        spinner_upper_category = findViewById(R.id.spinner_upper_category);

        if(!upper_category.equals("")){
            upperCategory.add("hoodie");
            upperCategory.add("shirt");
            upperCategory.add("sweater");
            upperCategory.add("sweatshirt");
            upperCategoryAdapter = new ArrayAdapter<>(mContext, R.layout.custom_spinner, upperCategory);
            spinner_upper_category.setAdapter(upperCategoryAdapter);
            spinner_upper_category.setSelection(upperCategory.indexOf(upper_category));
            spinner_upper_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    upper_category = upperCategory.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            spinner_upper_category.setVisibility(View.GONE);
            textView_upper_category.setVisibility(View.GONE);
        }

        button_recommend = findViewById(R.id.button_recommend);
        button_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                sendRequest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void sendRequest(){
        String url = "https://kmykrall63.execute-api.ap-northeast-2.amazonaws.com/new";
        JSONObject jsonObject = new JSONObject();
        ColorDrawable colorDrawable = (ColorDrawable)imageView_color.getBackground();
        int color = colorDrawable.getColor();
        int topType = 0;
        if(!upper_category.equals("")){
            topType = upperCategory.indexOf(upper_category) + 1;
        }
        int topPattern = 0;
        if(!upper_pattern.equals("")){
            topPattern = pattern.indexOf(upper_pattern) + 1;
        }
        try {
            //jsonObject.put("uri", SingleTon.getInstance().uri);
            jsonObject.put("input_Type", cloth_type_en.indexOf(cloth_type));
            jsonObject.put("input_R", Color.red(color));
            jsonObject.put("input_G", Color.green(color));
            jsonObject.put("input_B", Color.blue(color));
            jsonObject.put("input_topType", topType);
            jsonObject.put("input_topPattern", topPattern);
            jsonObject.put("user_sex", userSex);
            jsonObject.put("user_style", userStyle);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response: " + response.toString());
                        try {
                            String result = response.get("result").toString();
                            progressBar.setVisibility(View.GONE);
                             if(result.contains("No Cody in List")){
                                 Toast.makeText(mContext, "추천 결과가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                                 return;
                             }

                            Intent intent = new Intent(mContext, ResultActivity.class);
                            intent.putExtra("result", result);
                            intent.putExtra("userStyle", userStyle);
                            intent.putExtra("userSex", userSex);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "추천 에러", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Response error : " + error);
                        Toast.makeText(mContext, "추천 에러", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
