package com.example.asus.yxyyeapp.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.asus.yxyyeapp.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 威威 on 2016/9/24.
 */
public class Change_Password extends Activity {
    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_ensure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_ensure = (EditText) findViewById(R.id.et_ensure);
    }

    public void click(View v) {
        String old_password = et_old_password.getText().toString().trim();
        String new_password = et_new_password.getText().toString().trim();
        String ensure = et_ensure.getText().toString().trim();
        if (old_password.equals("") || new_password.equals("") || ensure.equals("")) {
            Toast.makeText(Change_Password.this, "输入框不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (new_password.equals(ensure)&&!old_password.equals(new_password)) {
                final ProgressDialog progress = new ProgressDialog(Change_Password.this);
                progress.setMessage("正在修改，请稍后...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                BmobUser.updateCurrentUserPassword(old_password, new_password, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {


                            startActivity(new Intent(Change_Password.this,LoginActivity.class));


                            Toast.makeText(Change_Password.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        } else {
                            Toast.makeText(Change_Password.this, "服务器繁忙，请稍后重试。。。", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(Change_Password.this, "密码输入有误，请重新输入", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
