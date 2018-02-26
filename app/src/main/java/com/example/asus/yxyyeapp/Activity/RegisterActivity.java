package com.example.asus.yxyyeapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private Button backto;
    private Button btn_register;
    private EditText et_account;
    private EditText et_password;
    private EditText et_pwd_again;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        backto = (Button) findViewById(R.id.bt_back);
        btn_register = (Button) findViewById(R.id.register);
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_pwd_again = (EditText) findViewById(R.id.et_pwd_again);

        initLinstener();

    }

    private void initLinstener() {
        backto.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.register:
                registerUser();
                break;
        }

    }

    //用户注册
    private void registerUser(){
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();
        String pwd = et_pwd_again.getText().toString();
        if (TextUtils.isEmpty(account)) {
            showToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        if (!password.equals(pwd)) {
            showToast("两次密码不一样");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册，请稍后...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final User user = new User();
        user.setUsername(account);
        user.setPassword(password);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    progress.dismiss();

                    showToast("注册成功");
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    finish();
                }else {
                    showToast("该用户已存在,请重新注册");
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
