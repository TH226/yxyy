package com.example.asus.yxyyeapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.Recruit_information;
import com.lidroid.xutils.BitmapUtils;

/**
 * Created by 威威 on 2016/8/24.
 */
public class NewRecruitActivity extends BaseActivity {
    private ImageView iv_activity;//主题照片
    private TextView tv_theme;//活动主题
    private TextView tv_time;
    private TextView tv_address;
    private TextView tv_ph;
    private TextView tv_way;
    private TextView tv_content;
    private TextView tv_report;

    private Recruit_information mLists;
    private BitmapUtils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recruit);
        mLists = (Recruit_information) getIntent().getSerializableExtra("informatoin");
        initView();
        initData();
        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewRecruitActivity.this);
                builder.setTitle("请选择类型");
                final String[] sex = {"虚假信息","不良信息","谣传信息","其他"};

                builder.setSingleChoiceItems(sex, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),"举报成功，我们会在五个工作日内处理，谢谢", Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void initView() {
        iv_activity = (ImageView) findViewById(R.id.iv_activity);
        tv_theme = (TextView) findViewById(R.id.tv_theme);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_ph = (TextView) findViewById(R.id.tv_ph);
        tv_way = (TextView) findViewById(R.id.tv_way);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_report = (TextView) findViewById(R.id.tv_report);
    }

    private void initData() {
        utils = new BitmapUtils(NewRecruitActivity.this);
        //设置加载过程中的默认图片
        utils.configDefaultLoadingImage(R.mipmap.image7);

        utils.display(iv_activity,mLists.getActivity_image().getFileUrl());
        tv_theme.setText(mLists.getAddress()+"义工招幕");
        tv_time.setText(mLists.getGather_time());
        tv_address.setText(mLists.getGather_address());
        tv_ph.setText(mLists.getMy_phone());
        tv_content.setText(mLists.getContent());

    }


    public void click(View v) {
        String phone = tv_ph.getText().toString().trim();
        if (phone.isEmpty()||phone.length()<11) {
            Toast.makeText(getApplicationContext(),"无法拨打电话",Toast.LENGTH_LONG).show();
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            Integer a;
            Integer b;

            a = Integer.valueOf(phone.substring(0,6));
            b = Integer.valueOf(phone.substring(6));

            intent.setData(Uri.parse("tel:"+a+b));
            startActivity(intent);
        }
    }

    public void click2(View v) {
        final EditText input = new EditText(NewRecruitActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(NewRecruitActivity.this);
        builder.setTitle("请告诉领队你参加过几次活动");
        builder.setView(input);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(NewRecruitActivity.this,"报名成功，请等待审核",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}