package com.example.asus.yxyyeapp.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.R;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ResetPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_phone;
    private EditText et_verify_code;
    private EditText et_pwd;
    private  Button back;
    private Button btn_reset;
    private Button btn_send;

    private MyCountTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_verify_code = (EditText) findViewById(R.id.et_verify_code);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        back = (Button)findViewById(R.id.back);
        btn_reset = (Button)findViewById(R.id.btn_reset);
        btn_send = (Button)findViewById(R.id.btn_send);

        initListener();
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_send.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            btn_send.setText("重新发送验证码");
        }
    }

    private void initListener() {
        back.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_send:
                requestSMSCode();
                break;
            case R.id.btn_reset:
                resetPwd();
                break;

        }

    }

    private void requestSMSCode() {
        String number = et_phone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();

            BmobSMS.requestSMSCode(this, number, "密码重置", new RequestSMSCodeListener() {
                @Override
                public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                    if (e == null) {// 验证码发送成功
                        showToast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {//如果验证码发送错误，可停止计时
                        timer.cancel();
                    }
                }
            });

//            Bmob.requestSMSCode(this, number, "重置密码模板", new RequestSMSCodeListener() {
//
//                @Override
//                public void done(Integer smsId, BmobException ex) {
//                    if (ex == null) {// 验证码发送成功
//                        showToast("验证码发送成功");// 用于查询本次短信发送详情
//                    } else {//如果验证码发送错误，可停止计时
//                        timer.cancel();
//                    }
//                }
//            });
        } else {
            showToast("请输入手机号码");
        }
    }

    private void resetPwd() {
        final String code = et_verify_code.getText().toString();
        final String pwd = et_pwd.getText().toString();
        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast("密码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(ResetPwdActivity.this);
        progress.setMessage("正在重置密码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.resetPasswordBySMSCode(code, pwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                progress.dismiss();
                if (e == null) {
                    showToast("密码重置成功");
                    finish();
                } else {
                    showToast("密码重置失败");
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
