package com.example.asus.yxyyeapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.Fragment.MyFragment;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 威威 on 2016/8/21.
 */
public class InformationActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_sex;
    private ImageButton ib_back_front;
    private TextView tv_save;
    private EditText et_name;
    private EditText et_truename;
    private TextView et_sex;
    private EditText et_age;
    private EditText et_company;
    private EditText et_introduction;

    private SharedPreferences sp;
    private int Index = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_information);

        initView();
        linstener();
    }

    private void initView() {
        ll_sex = (LinearLayout) findViewById(R.id.ll_sex);
        et_name = (EditText) findViewById(R.id.et_name);
        et_truename = (EditText) findViewById(R.id.et_truename);
        et_sex = (TextView) findViewById(R.id.et_sex);
        et_age = (EditText) findViewById(R.id.et_age);
        et_company = (EditText) findViewById(R.id.et_company);
        et_introduction = (EditText) findViewById(R.id.et_introduction);

        if (sp == null) {
            sp = getSharedPreferences("config",MODE_PRIVATE);
        }
        et_name.setText(sp.getString("et_name",""));
        et_truename.setText(sp.getString("et_truename",""));
        et_sex.setText(sp.getString("et_sex",""));
        et_age.setText(sp.getString("et_age",""));
        et_company.setText(sp.getString("et_company",""));
        et_introduction.setText(sp.getString("et_introduction",""));

        ib_back_front = (ImageButton) findViewById(R.id.ib_back_front);
        tv_save = (TextView) findViewById(R.id.tv_save);
    }

    private void linstener(){
        tv_save.setOnClickListener(this);
        ib_back_front.setOnClickListener(this);
        ll_sex.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back_front:
                initData();
                finish();
                break;
            case R.id.ll_sex:
                CreateDialog();
                break;
            case R.id.tv_save:
                if (sp == null) {
                    sp = getSharedPreferences("config",MODE_PRIVATE);
                }
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("et_name",et_name.getText().toString().trim());
                editor.putString("et_truename",et_truename.getText().toString().trim());
                editor.putString("et_age",et_age.getText().toString().trim());
                editor.putString("et_sex",et_sex.getText().toString().trim());
                editor.putString("et_company",et_company.getText().toString().trim());
                editor.putString("et_introduction",et_introduction.getText().toString().trim());
                editor.commit();

                UpdateData();
                break;

        }
    }
    private void initData() {
        if (sp == null) {
            sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        String UserObjectId = sp.getString("UserObjectId","");
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(UserObjectId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e ==null) {
                    MyFragment.tv_nickname.setText(user.getName());
                    MyFragment.tv_introduction.setText("简介:"+user.getIntroduction());
                }
            }
        });
//        query.getObject(InformationActivity.this, UserObjectId, new GetListener<User>() {
//            @Override
//            public void onSuccess(User user) {
//                MyFragment.tv_nickname.setText(user.getName());
//                MyFragment.tv_introduction.setText("简介:"+user.getIntroduction());
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//            }
//        });
    }
    private void UpdateData() {
        if (sp == null) {
            sp = getSharedPreferences("config",MODE_PRIVATE);
        }
       String UserObjectId = sp.getString("UserObjectId","");

        User UserInformation = new User();
        UserInformation.setName(et_name.getText().toString().trim());
        UserInformation.setTruename(et_truename.getText().toString().trim());
        UserInformation.setAge(et_age.getText().toString().trim());
        UserInformation.setSxe(et_sex.getText().toString().trim());
        UserInformation.setCompany(et_company.getText().toString().trim());
        UserInformation.setIntroduction(et_introduction.getText().toString().trim());
        UserInformation.update(UserObjectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("保存成功");
                }else {
                    showToast("保存失败");
                }
            }
        });
    }

    private void CreateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(InformationActivity.this);
        builder.setTitle("请选择性别");
        final String[] sex = {"男","女"};
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(sex, Index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                et_sex.setText(sex[i].toString().trim());
                Index = i;
                dialogInterface.dismiss();
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
