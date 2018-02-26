package com.example.asus.yxyyeapp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.Fragment.MyFragment;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.UserHeadImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 威威 on 2016/8/22.
 */
public class UploadHeadActivity extends BaseActivity implements View.OnClickListener {
    private static CircleImageView mImageHeader;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "IMG_20160502_222836.jpg";

    private SharedPreferences sp;
    private static Bitmap bitmap;

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MyFragment.ib_head.setImageBitmap(bitmap);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_phpto);

        initView();

        mImageHeader.setImageBitmap(bitmap);
    }

    private void initView() {
        mImageHeader = (CircleImageView) findViewById(R.id.ib_head);
        final TextView selectBtn1 = (TextView) findViewById(R.id.btn_selectimage);
        final TextView selectBtn2 = (TextView) findViewById(R.id.btn_takephoto);
        final TextView btn_cancle = (TextView) findViewById(R.id.canclePhoto);
        btn_cancle.setOnClickListener(this);
        selectBtn1.setOnClickListener(this);
        selectBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //相册选取照片
            case R.id.btn_selectimage:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");//图片
                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
                break;
            //手机拍照
            case R.id.btn_takephoto:
                if (isSdcardExisting()) {
                    Intent cameraIntent = new Intent(
                            "android.media.action.IMAGE_CAPTURE");//拍照
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
                    cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                } else {
                    Toast.makeText(view.getContext(), "请插入sd卡", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            case R.id.canclePhoto:
                finish();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                //相册选取图片
                case IMAGE_REQUEST_CODE:
                    Uri originalUri = data.getData();
                    resizeImage(originalUri);
                    //下面方法将获取的uri转为String类型哦！
                    String []imgs1={MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                    Cursor cursor=this.managedQuery(originalUri, imgs1, null, null, null);
                    int index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String img_url=cursor.getString(index);
                    upload(img_url);
                    break;
                //手机拍照
                case CAMERA_REQUEST_CODE:
                    if (isSdcardExisting()) {
                        resizeImage(getImageUri());
                        //下面方法将获取的uri转为String类型哦！
                        String []imgs={MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                        Cursor cursor1=this.managedQuery(getImageUri(), imgs, null, null, null);
                        int index1=cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor1.moveToFirst();
                        String img_url1=cursor1.getString(index1);
                        upload(img_url1);

                    } else {
                        Toast.makeText(UploadHeadActivity.this, "未找到存储卡，无法存储照片！",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                case RESIZE_REQUEST_CODE:
                    if (data != null) {
                        showResizeImage(data);
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //判断SD卡是否存在
    private boolean isSdcardExisting() {
        final String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void resizeImage(Uri uri) {//重塑图片大小
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可以裁剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESIZE_REQUEST_CODE);
    }

    private void showResizeImage(Intent data) {//显示图片
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            mImageHeader.setImageDrawable(drawable);
        }
    }

    //获取路径
    private Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
    }

    //将图片上传
    private void upload(String imagepath) {
        final BmobFile icon = new BmobFile(new File(imagepath));
        if (icon!=null) {
            icon.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        UserHeadImage iamge = new UserHeadImage();
                        iamge.setImageFile(icon);
                        iamge.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    showToast("上传成功");
                                }else {
                                    showToast("上传失败");
                                }
                            }
                        });
                        showToast("图片上传成功");

                        String fileUrl = icon.getFileUrl();
                        if (sp == null) {
                            sp = getSharedPreferences("config",MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("fileUrl",fileUrl);
                        editor.commit();
                        downImage(fileUrl);
                    }else{
                        showToast("图片上传失败");
                    }
                }
            });
        }else {
            showToast("文件夹为空");
        }
    }


    //加载图片
    public static void downImage(final String imageUrl) {
        new Thread() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(imageUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
