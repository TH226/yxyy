package com.example.asus.yxyyeapp.Base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import cn.bmob.v3.Bmob;

/**
 * Created by 威威 on 2016/8/14.
 */
public class BaseActivity extends android.support.v4.app.FragmentActivity {

    public Toast mToast;
    public String BMOB_APP_KEY = "5febda56600b401956447493c2d828e6";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, BMOB_APP_KEY);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

}
