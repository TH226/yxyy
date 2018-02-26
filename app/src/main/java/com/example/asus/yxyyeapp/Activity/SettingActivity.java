package com.example.asus.yxyyeapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


/**
 * Created by 威威 on 2016/9/24.
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    private TextView tv_phone;
    private LinearLayout ll_resetpassword;
    private LinearLayout ll_opinion;
    private LinearLayout ll_update;
    private LinearLayout ll_software;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        initData();
        listener();
        query();
    }



    private void listener() {
        ll_resetpassword.setOnClickListener(this);
        ll_opinion.setOnClickListener(this);
        ll_update.setOnClickListener(this);
        ll_software.setOnClickListener(this);
    }

    private void initData() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        ll_resetpassword = (LinearLayout) findViewById(R.id.ll_resetpassword);
        ll_opinion = (LinearLayout) findViewById(R.id.ll_opinion);
        ll_update = (LinearLayout) findViewById(R.id.ll_update);
        ll_software = (LinearLayout) findViewById(R.id.ll_software);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_resetpassword:
                Intent intent1 = new Intent(SettingActivity.this, Change_Password.class);
                startActivity(intent1);
                break;
            case R.id.ll_opinion:
                ShowInputSignatureDialog();
                break;
            case R.id.ll_update:
                Toast.makeText(SettingActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_software:
                Intent intent3 = new Intent(SettingActivity.this, SoftwareActivity.class);
                startActivity(intent3);
                break;
            default:
                break;
        }

    }

    private void ShowInputSignatureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(SettingActivity.this, R.layout.doialog_opinion, null);
        dialog.setView(view, 0, 0, 0, 0);

        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_no = (Button) view.findViewById(R.id.btn_no);
        final EditText et_qq = (EditText) view.findViewById(R.id.et_qq);
        final EditText et_opinion1 = (EditText) view.findViewById(R.id.et_opinion1);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_qq.getText().toString().trim().equals("") || et_opinion1.getText().toString().trim().equals("")) {
                    Toast.makeText(SettingActivity.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SettingActivity.this, "反馈成功，我们会尽快跟你联系", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void query() {
        SharedPreferences  sp = getSharedPreferences("config",MODE_PRIVATE);
        String userObjectId = sp.getString("UserObjectId", "");
        BmobQuery<User> bmonquery = new BmobQuery<User>();
        bmonquery.getObject(userObjectId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    String mobilePhoneNumber = user.getMobilePhoneNumber();
                    if (!mobilePhoneNumber.isEmpty()) {
                        tv_phone.setText(mobilePhoneNumber);
                    }
                }else {
                    tv_phone.setText("未绑定");
                }
            }
        });
    }
}
