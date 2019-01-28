package com.bytedance.android.lesson.restapi.solution;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.utils.ResourceUtils;
import com.bytedance.android.lesson.restapi.solution.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChoosePicture extends AppCompatActivity implements SurfaceHolder.Callback{
    public static final int PICK_IMAGE=1;
    public static final int REQUEST_IMAGE=2;
    public static final String TAG="Choose";
    public Uri mSelectedImage=null;
    public Uri mSelectedVideo;
    public File imgFile=null;
    ImageView imageView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String data = bundle.getString("videoUri");
        mSelectedVideo=Uri.parse(data);
        Log.d("mess",String.valueOf(mSelectedVideo));
    }

    public void chooseImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }



    private void setPic2(){
        imageView=findViewById(R.id.img);
        Bitmap bitmap=null;
        try {
            bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(mSelectedImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
    }
    private void setPic() {
        imageView=findViewById(R.id.img);
        int TargetW=imageView.getWidth();
        int TargetH=imageView.getHeight();

        BitmapFactory.Options bmOptions=new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
        int photoW=bmOptions.outWidth;
        int photoH=bmOptions.outHeight;

        int scaleFactor=Math.min(photoW/TargetW,photoH/TargetH);

        bmOptions.inJustDecodeBounds=false;
        bmOptions.inSampleSize=scaleFactor;
        bmOptions.inPurgeable=true;

        Bitmap bmp=BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);

        String path=imgFile.getAbsolutePath();
        bmp=Utils.rotateImage(bmp,path);
        imageView.setImageBitmap(bmp);
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f;
        if (imgFile != null) {
            f = imgFile;
        } else {
            f = new File(ResourceUtils.getRealPath(ChoosePicture.this, uri));
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    public void postVideo(View view) {

        Toast.makeText(this, "上传中", Toast.LENGTH_LONG).show();

        RetrofitManager.get(IMiniDouyinService.HOST).create(IMiniDouyinService.class).createVideo("123456789", "qwertyui",
                getMultipartFromUri("cover_image", mSelectedImage),
                getMultipartFromUri("video", mSelectedVideo)).
                enqueue(new Callback<PostVideoResponse>() {
                    @Override
                    public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {

                        Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response.body() + "]");
                        if (response.isSuccessful()) {

                            Toast.makeText(ChoosePicture.this, "上传完成", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "success");
                            startActivity(new Intent(ChoosePicture.this, MainActivity.class));
                        } else {
                            Log.d(TAG, "onResponse() called with: response.errorBody() = [" + response.errorBody() + "]");

                        }
                    }

                    @Override public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                        Toast.makeText(ChoosePicture.this, "上传失败，请重试", Toast.LENGTH_LONG).show();

                    }
                });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    public void TakePicture(View view){
        File mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        ),"CameraDemo");
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath()+File.separator+
                "IMG_"+timeStamp+".jpg");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgFile=Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);

        startActivityForResult(takePictureIntent,REQUEST_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK ) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                setPic2();
            }
            if (requestCode==REQUEST_IMAGE){
                setPic();

            }
        }
    }

}
