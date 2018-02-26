package com.example.asus.yxyyeapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus.yxyyeapp.Activity.VolunteerRecruit;
import com.example.asus.yxyyeapp.Base.BaseActivity;
import com.example.asus.yxyyeapp.R;

/**
 * Created by 威威 on 2016/8/16.
 */
public class FragmentActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_mainpager;
    private LinearLayout ll_activity;
    private LinearLayout ll_message;
    private LinearLayout ll_my;

    private TextView tv_mainpager;
    private TextView tv_activity;
    private TextView tv_message;
    private TextView tv_my;

    private TextView tv_title;
    private TextView tv_volunteer_recruit;

    private ImageButton ib_mainpager;
    private ImageButton ib_activity;
    private ImageButton ib_message;
    private ImageButton ib_my;

    private Fragment view1;
    private Fragment view2;
    private Fragment view3;
    private Fragment view4;

    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.volunteer_layout);

        initView();

        initEvent();

        setSelect(0);
    }

    private void initView() {
        ll_mainpager = (LinearLayout) findViewById(R.id.ll_mainpager);
        ll_activity = (LinearLayout) findViewById(R.id.ll_activity);
        ll_message = (LinearLayout) findViewById(R.id.ll_message);
        ll_my = (LinearLayout) findViewById(R.id.ll_my);

        tv_mainpager = (TextView) findViewById(R.id.tv_mainpager);
        tv_activity = (TextView) findViewById(R.id.tv_activity);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_my = (TextView) findViewById(R.id.tv_my);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_volunteer_recruit = (TextView)findViewById(R.id.tv_volunteer_recruit);

        ib_mainpager = (ImageButton) findViewById(R.id.ib_mainpager);
        ib_activity = (ImageButton) findViewById(R.id.ib_activity);
        ib_message = (ImageButton) findViewById(R.id.ib_message);
        ib_my = (ImageButton) findViewById(R.id.ib_my);
    }

    private void initEvent() {
        ll_mainpager.setOnClickListener(this);
        ll_activity.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_my.setOnClickListener(this);

    }

    private void setSelect(int i){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);

        switch (i) {
            case 0:
                if(view1==null) {
                    view1 = new MainFragment();
                    transaction.add(R.id.frag,view1);
                } else {
                    transaction.show(view1);
                }
                ib_mainpager.setImageResource(R.mipmap.ic_tab_mainpage_selected);
                tv_mainpager.setTextColor(this.getResources().getColor(R.color.colorText1));
                tv_title.setText("首页");
                tv_volunteer_recruit.setVisibility(View.GONE);
                break;
            case 1:
                if(view2==null) {
                    view2 = new ActivityFragment();
                    transaction.add(R.id.frag,view2);
                } else {
                    transaction.show(view2);
                }
                ib_activity.setImageResource(R.mipmap.ic_tab_left_selected);
                tv_activity.setTextColor(this.getResources().getColor(R.color.colorText1));
                tv_title.setText("活动");
                tv_volunteer_recruit.setVisibility(View.VISIBLE);
                tv_volunteer_recruit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FragmentActivity.this,VolunteerRecruit.class);
                        startActivity(intent);
                    }
                });
                break;
            case 2:
                if(view3==null) {
                    view3 = new MessageFragment();
                    transaction.add(R.id.frag,view3);
                } else {
                    transaction.show(view3);
                }
                ib_message.setImageResource(R.mipmap.ic_tab_right_selected);
                tv_message.setTextColor(this.getResources().getColor(R.color.colorText1));
                tv_title.setText("服务");
                tv_volunteer_recruit.setVisibility(View.GONE);
                break;
            case 3:
                if(view4==null) {
                    view4 = new MyFragment();
                    transaction.add(R.id.frag,view4);
                } else {
                    transaction.show(view4);
                }
                ib_my.setImageResource(R.mipmap.ic_tab_mine_selected);
                tv_my.setTextColor(this.getResources().getColor(R.color.colorText1));
                tv_title.setText("我的");
                tv_volunteer_recruit.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction ) {
        if(view1!=null) {
            transaction.hide(view1);
        }
        if(view2!=null) {
            transaction.hide(view2);
        }
        if(view3!=null) {
            transaction.hide(view3);
        }
        if(view4!=null) {
            transaction.hide(view4);
        }
    }

    @Override
    public void onClick(View v) {
        resetImgs();
        switch (v.getId()) {
            case R.id.ll_mainpager:
                setSelect(0);
                break;
            case R.id.ll_activity:
                setSelect(1);
                break;
            case R.id.ll_message:
                setSelect(2);
                break;
            case R.id.ll_my:
                setSelect(3);
                break;
        }
    }
    private void resetImgs() {
        ib_mainpager.setImageResource(R.mipmap.ic_tab_mainpage_normal);
        tv_mainpager.setTextColor(ContextCompat.getColor(this,R.color.colorText2));
        ib_activity.setImageResource(R.mipmap.ic_tab_left_normal);
        tv_activity.setTextColor(ContextCompat.getColor(this,R.color.colorText2));
        ib_message.setImageResource(R.mipmap.ic_tab_right_normal);
        tv_message.setTextColor(ContextCompat.getColor(this,R.color.colorText2));
        ib_my.setImageResource(R.mipmap.ic_tab_mine_normal);
        tv_my.setTextColor(ContextCompat.getColor(this,R.color.colorText2));
    }

    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
