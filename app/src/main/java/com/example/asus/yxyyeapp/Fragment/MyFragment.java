package com.example.asus.yxyyeapp.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asus.yxyyeapp.Activity.InformationActivity;
import com.example.asus.yxyyeapp.Activity.LoginActivity;
import com.example.asus.yxyyeapp.Activity.SettingActivity;
import com.example.asus.yxyyeapp.Activity.UploadHeadActivity;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.User;
import com.example.asus.yxyyeapp.bean.UserHeadImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 威威 on 2016/8/16.
 */
public class MyFragment extends Fragment implements View.OnClickListener {
    private LinearLayout ll_signature;
    private RelativeLayout ll_information; //完善个人信息
    private RelativeLayout ll_setting;//设置
    private RelativeLayout ll_activity;//天气查询

    private Button btn_newlogin;//退出当前帐号
    public static CircleImageView ib_head;//头像
    public static TextView tv_nickname;//昵称
    public static TextView tv_introduction;//个人简介
    private TextView tv_autograph;//签名

    private Dialog dialog;
    private TextView choosePhoto;
    private TextView takePhoto;
    private TextView canclePhoto;
    private View inflate;
    public Toast mToast;


    private View vw;
    private SharedPreferences sp;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESIZE_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "IMG_20160502_222836.jpg";

    private static Bitmap bitmap;

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MyFragment.ib_head.setImageBitmap(bitmap);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vw = inflater.inflate(R.layout.fragmen_my_layout, container, false);

        initView();
        ib_head.setImageBitmap(bitmap);
        initListener();
        initData();
        downLoadImage();
        return vw;
    }

    private void initView() {
        btn_newlogin = (Button) vw.findViewById(R.id.btn_newlogin);
        ll_information = (RelativeLayout) vw.findViewById(R.id.ll_information);
        tv_nickname = (TextView) vw.findViewById(R.id.tv_nickname);
        tv_introduction = (TextView) vw.findViewById(R.id.tv_introduction);
        tv_autograph = (TextView) vw.findViewById(R.id.tv_autograph);
        ll_signature = (LinearLayout) vw.findViewById(R.id.ll_signature);
        ib_head = (CircleImageView) vw.findViewById(R.id.ib_head);
        ll_setting = (RelativeLayout) vw.findViewById(R.id.ll_setting);
        //ll_activity = (RelativeLayout) vw.findViewById(R.id.ll_activity);

    }

    private void initListener() {
        btn_newlogin.setOnClickListener(this);
        ll_information.setOnClickListener(this);
        ll_signature.setOnClickListener(this);
        ib_head.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        //ll_activity.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_newlogin:
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                LoginActivity.check1 = false;
                startActivity(intent1);
                getActivity().finish();
                break;
            case R.id.ll_information:
                Intent intent2 = new Intent(getActivity(), InformationActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_signature:
                ShowInputSignatureDialog();
                break;
            case R.id.ib_head:
                showDialogPhoto();
//                Intent intent3 = new Intent(getActivity(),UploadHeadActivity.class);
//                startActivity(intent3);
                break;
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
                //getActivity().finish();
                dialog.dismiss();
                break;
            case R.id.ll_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_OK) {
            return;
        } else {
        switch (requestCode) {
            //相册选取图片
            case IMAGE_REQUEST_CODE:
                Uri originalUri = data.getData();
                resizeImage(originalUri);
                //下面方法将获取的uri转为String类型哦！
                String[] imgs1 = {MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                Cursor cursor = getActivity().managedQuery(originalUri, imgs1, null, null, null);
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String img_url = cursor.getString(index);
                upload(img_url);
                break;
            //手机拍照
            case CAMERA_REQUEST_CODE:
                if (isSdcardExisting()) {
                    resizeImage(getImageUri());
                    //下面方法将获取的uri转为String类型哦！
                    String[] imgs = {MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                    Cursor cursor1 = getActivity().managedQuery(getImageUri(), imgs, null, null, null);
                    int index1 = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor1.moveToFirst();
                    String img_url1 = cursor1.getString(index1);
                    upload(img_url1);

                } else {
                    Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！",
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


    private void showResizeImage(Intent data) {//显示图片
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            ib_head.setImageDrawable(drawable);
        }
    }

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getActivity(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    //将图片上传
    private void upload(String imagepath) {
        final BmobFile icon = new BmobFile(new File(imagepath));
        if (icon != null) {
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
                                } else {
                                    showToast("上传失败");
                                }
                            }
                        });
                        showToast("图片上传成功");

                        String fileUrl = icon.getFileUrl();
                        if (sp == null) {
                            sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("fileUrl", fileUrl);
                        editor.commit();
                        downImage(fileUrl);
                    } else {
                        showToast("图片上传失败");
                    }
                }
            });
        } else {
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
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

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

    //获取路径
    private Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
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

    private void showDialogPhoto() {

        dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        //填充对话框布局
        inflate = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_phpto, null);

        choosePhoto = (TextView) inflate.findViewById(R.id.btn_selectimage);
        takePhoto = (TextView) inflate.findViewById(R.id.btn_takephoto);
        canclePhoto = (TextView) inflate.findViewById(R.id.canclePhoto);

        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        canclePhoto.setOnClickListener(this);


//        Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");
//        if (bt != null) {
//            mImageHeader.setImageBitmap(bt);
//        } else {
////如果本地没有头像图片则从服务器取头像，然后保存在SD卡中
//        }

        //布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前的Activity所在窗体
        Window dialogWindow = dialog.getWindow();
        //设置弹窗从下方弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

        lp.y = 20;//设置弹窗距离底部的距离
        //降属性设置给弹窗
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void initData() {
        if (sp == null) {
            sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        tv_autograph.setText(sp.getString("tv_autograph", ""));

        String UserObjectId = sp.getString("UserObjectId", "");
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(UserObjectId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    MyFragment.tv_nickname.setText(user.getName());
                    MyFragment.tv_introduction.setText("简介:" + user.getIntroduction());
                }
            }
        });
    }

    private void ShowInputSignatureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();

        View view = View.inflate(getActivity(), R.layout.dialog_signature, null);
        dialog.setView(view, 0, 0, 0, 0);
        final EditText et_signature = (EditText) view.findViewById(R.id.et_signature);
        Button btn_save = (Button) view.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_autograph.setText(et_signature.getText().toString());
                if (sp == null) {
                    sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("tv_autograph", et_signature.getText().toString());
                editor.commit();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //加载头像
    private void downLoadImage() {
        if (sp == null) {
            sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        String fileUrl = sp.getString("fileUrl", "");
        UploadHeadActivity.downImage(fileUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
