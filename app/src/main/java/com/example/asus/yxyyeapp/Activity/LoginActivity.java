package com.example.asus.yxyyeapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.Fragment.FragmentActivity;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;


public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Button bt_login;
    private EditText ed_user1;
    private EditText ed_pass1;
    private TextView tv_xin;
    private TextView tv_wang;

    private static String name;
    private static String password;
    public static boolean check1 = false;
    private static String YES = "yes";
    private static String NO = "no";
    private String isMemory = "";
    private static String FILE = "saveUserNamePwd";
    private static SharedPreferences sp = null;
    private String UserObjectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bt_login = (Button) findViewById(R.id.bt_login);
        ed_user1 = (EditText) findViewById(R.id.et_user1);
        ed_pass1 = (EditText) findViewById(R.id.password1);
        tv_xin = (TextView) findViewById(R.id.xinyong);
        tv_wang = (TextView) findViewById(R.id.wangji);

        sp = getSharedPreferences("config",MODE_PRIVATE);
        isMemory = sp.getString("isMemory", NO);

        if (isMemory.equals(YES)) {
            name = sp.getString("name", "");
            password = sp.getString("password", "");
            ed_user1.setText(name);
            ed_pass1.setText(password);
        }

        if (check1) {
           // showToast("登录成功");
            Intent intent = new Intent(LoginActivity.this,FragmentActivity.class);
            intent.putExtra("from", "login");
            startActivity(intent);
            finish();
        }

        Linstener();
    }

    private void Linstener() {
        bt_login.setOnClickListener(this);
        tv_xin.setOnClickListener(this);
        tv_wang.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                check1 = true;
                login();
                break;
            case R.id.xinyong:
                Intent intent1 = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.wangji:
                Intent intent2 = new Intent(LoginActivity.this,ResetPwdActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }


    private void login(){
        String account = ed_user1.getText().toString();
        String password = ed_pass1.getText().toString();
        remmber();
        if (TextUtils.isEmpty(account)) {
            showToast("账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("正在登录，请稍后...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //V3.3.9提供的新的登录方式，可传用户名/邮箱/手机号码
        BmobUser.loginByAccount(account, password, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException ex) {
                progress.dismiss();
                if(ex==null){
                    UserObjectId = user.getObjectId();
                    if (sp == null) {
                        sp = getSharedPreferences("config",MODE_PRIVATE);
                    }
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("UserObjectId",UserObjectId);
                    editor.commit();

                    //toast("登录成功---用户名："+user.getUsername()+"，年龄："+user.getAge());
                  //  showToast("登录成功");
                    Intent intent = new Intent(LoginActivity.this,FragmentActivity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    finish();
                }else{
                    showToast("登录失败,请重新输入密码");
                }
            }
        });
    }

    public void remmber() {
            if (sp == null) {
                sp = getSharedPreferences(FILE,MODE_PRIVATE);
            }
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("name", ed_user1.getText().toString());
            edit.putString("password", ed_pass1.getText().toString());
            edit.putString("isMemory", YES);
            edit.commit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       /// finish();
    }
}
