package com.bytedance.android.lesson.restapi.solution;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bytedance.android.lesson.restapi.solution.bean.Feed;
import com.bytedance.android.lesson.restapi.solution.bean.FeedResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE=1;
    public static final String TAG="MainActivity";
    private List<Feed> mFeeds = new ArrayList<>();
    private RecyclerView mRv;
    public ImageView mBtnRefresh;
    public ImageView add;
    public TextView mtextView;
    //    Button mBtnRefresh=findViewById(R.id.btn_refresh);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initRecyclerView();
        mBtnRefresh = findViewById(R.id.dd3);
        add=findViewById(R.id.dd5);
        findViewById(R.id.dd5).setOnClickListener(v -> {
            add.setImageResource(R.mipmap.addwhite);

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

        });

    }


    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);

                return new MainActivity.MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;
                String text_now="\n   上传者："+mFeeds.get(i).getUserName()+"\n     ID："+mFeeds.get(i).getStudentId();
                Glide.with(iv.getContext()).load(mFeeds.get(i).getImageUrl()).into(iv);
                String Url = mFeeds.get(i).getVideoUrl();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        videoPlay(Url,text_now);

                    }
                });
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });
    }


    public void videoPlay(String url,String now){

        Intent intent = new Intent(MainActivity.this,VideoPlay.class);
        intent.putExtra("v_url",url);
        intent.putExtra("message",now);
        startActivity(intent);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void fetchFeed(View view) {
        mBtnRefresh.setImageResource(R.mipmap.refreshwhite);
        Toast.makeText(MainActivity.this, "刷新中!", Toast.LENGTH_LONG).show();
        RetrofitManager.get(IMiniDouyinService.HOST).create(IMiniDouyinService.class).fetchFeed().enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response.body() + "]");
                if (response.isSuccessful()) {
                    mFeeds = response.body().getFeeds();
                    mRv.getAdapter().notifyDataSetChanged();
                    ResetRefresh();
                } else {
                    Log.d(TAG, "onResponse() called with: response.errorBody() = [" + response.errorBody() + "]");
                    Toast.makeText(MainActivity.this, "fetch feed failure!", Toast.LENGTH_LONG).show();
                }

            }

            @Override public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }

        });
    }
    public void ResetRefresh()
    {

        mBtnRefresh.setImageResource(R.mipmap.refreshlight);
    }


    //申请权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {

                for(int i=0;i<4;i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) { //同意权限申请

                    } else {
                        Toast toast = Toast.makeText(this, "获取失败", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }
                add.setImageResource(R.mipmap.addlight);
                startActivity(new Intent(MainActivity.this, CustomCameraActivity.class));
                break;
            }

        }
    }



}
