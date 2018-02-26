package com.example.asus.yxyyeapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.Recruit_information;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by 威威 on 2016/8/24.
 */
public class VolunteerRecruit extends BaseActivity implements View.OnClickListener {
    private Button btn_submit;//提交
    private EditText et_my_phone;//联系电话
    private EditText et_address;//活动地点
    private EditText et_apply_time;//活动报名截止时间
    private EditText et_gather_time;//集合时间
    private EditText et_gather_address;//集合地点
    private ImageView iv_upload_phone;//上传照片
    private EditText et_content;

    private String img_url;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESIZE_REQUEST_CODE = 2;

    private ProgressDialog progress;

//    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volunteer_recruit_activity);

        initView();
        initData();
    }

    private void initView() {
        btn_submit = (Button) findViewById(R.id.btn_submit);
        iv_upload_phone = (ImageView) findViewById(R.id.iv_upload_phone);
        et_my_phone = (EditText) findViewById(R.id.et_my_phone);
        et_address = (EditText) findViewById(R.id.et_address);
        et_apply_time = (EditText) findViewById(R.id.et_apply_time);
        et_gather_time = (EditText) findViewById(R.id.et_gather_time);
        et_gather_address = (EditText) findViewById(R.id.et_gather_address);
        et_content = (EditText) findViewById(R.id.et_content);
    }

    private void initData() {
        btn_submit.setOnClickListener(this);
        iv_upload_phone.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                progress = new ProgressDialog(VolunteerRecruit.this);
                progress.setMessage("正在提交，请稍后...");
                progress.setCanceledOnTouchOutside(false);
                if (et_content.getText().toString().trim().equals("")||et_my_phone.getText().toString().trim().equals("") || iv_upload_phone.
                        getDrawable()==null || et_address.getText().
                        toString().trim().equals("") || et_apply_time.getText().toString().trim()
                        .equals("") || et_gather_time.getText().toString().trim().equals("") ||
                        et_gather_address.getText().toString().trim().equals("")) {
                    showToast("请完善信息");
                } else {
                    progress.show();
                    submitInformation();
                }
                break;
            case R.id.iv_upload_phone:
                selectImage();
                break;
            default:
                break;
        }
    }

    private void submitInformation() {
        final BmobFile uploadIimage = new BmobFile(new File(img_url));
        if (uploadIimage != null) {
            uploadIimage.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        final Recruit_information rif = new Recruit_information();
                        rif.setMy_phone(et_my_phone.getText().toString());
                        rif.setAddress(et_address.getText().toString());
                        rif.setActivity_image(uploadIimage);
                        rif.setApply_time(et_apply_time.getText().toString());
                        rif.setContent(et_content.getText().toString());
                        rif.setGather_address(et_gather_address.getText().toString());
                        rif.setGather_time(et_gather_time.getText().toString());
                        rif.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    showToast("提交成功");
                                    progress.dismiss();
                                } else {
                                    showToast("提交失败");
                                }
                            }
                        });
                    } else {
                        showToast("提交上传失败");
                    }
                }
            });
        }
    }

    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");//图片
        startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    Uri originalUri = data.getData();
                    resizeImage(originalUri);
                    //下面方法将获取的uri转为String类型哦！
                    String[] imgs1 = {MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                    Cursor cursor = this.managedQuery(originalUri, imgs1, null, null, null);
                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    img_url = cursor.getString(index);
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
            iv_upload_phone.setImageDrawable(drawable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
