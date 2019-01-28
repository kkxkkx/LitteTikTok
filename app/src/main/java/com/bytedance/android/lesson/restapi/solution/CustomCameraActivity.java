package com.bytedance.android.lesson.restapi.solution;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;


public class CustomCameraActivity extends AppCompatActivity {

    private Uri mSelectedVideo;

    private static String TAG="CustomCamera";
    public static  final  int REQUEST_VIDEO=3;
    public static  final int CHOOSE_VIDEO=2;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record_video);


        findViewById(R.id.btn_post).setOnClickListener(v -> {
            if(mSelectedVideo!=null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(CustomCameraActivity.this,ChoosePicture.class);
                bundle.putString("videoUri",mSelectedVideo.toString());
                intent.putExtras(bundle);

                startActivity(intent);

                // startActivity(new Intent(CustomCameraActivity.this, ChoosePicture.class));
            }

        });
    }


    public void Deal_video(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        startActivityForResult(intent, REQUEST_VIDEO);
    }

    @Override
    public void onActivityResult(int requestcode,int resultcode,final Intent data){
        super.onActivityResult(requestcode,resultcode,data );
        videoView = findViewById(R.id.img);
        if(resultcode==RESULT_OK&&data!=null&&requestcode==REQUEST_VIDEO){
            mSelectedVideo=data.getData();
            mSelectedVideo = data.getData();
            videoView.setVideoURI(mSelectedVideo);
            videoView.start();
            Log.d("mVideo", String.valueOf(mSelectedVideo));
        }
        if(resultcode==RESULT_OK&&data!=null&&requestcode==CHOOSE_VIDEO){
            mSelectedVideo = data.getData();
            Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
            videoView.setVideoURI(mSelectedVideo);
            videoView.start();
        }
    }
    public void chooseVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"),
                CHOOSE_VIDEO);

    }
}
