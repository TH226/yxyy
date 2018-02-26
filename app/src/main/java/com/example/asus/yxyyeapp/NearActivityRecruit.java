package com.example.asus.yxyyeapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.yxyyeapp.Activity.RecruitActivity;
import com.example.asus.yxyyeapp.View.RefreshListView;
import com.example.asus.yxyyeapp.bean.Recruit_information;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 威威 on 2016/8/24.
 */
public class NearActivityRecruit extends Activity {
    private ImageButton ib_back_front_my;
    private RefreshListView lvList;
    private List<Recruit_information> mListRecruit;

    private ViewHolder holder;
    private MyAdapter listAdapter;
    private ImageButton IvRefresh;

    private static SharedPreferences sp = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.near_activity_recruit);

        lvList = (RefreshListView) findViewById(R.id.lv_recruit_information1);
        ib_back_front_my = (ImageButton) findViewById(R.id.ib_back_front_my);
        IvRefresh= (ImageButton) findViewById(R.id.refresh_recruit);

        IvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.notifyDataSetChanged();
                initData();
            }
        });
        initView();
        initData();
        listener();
    }

    private void listener() {
        ib_back_front_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              //  Toast.makeText(NearActivityRecruit.this, "pos=" +(position-1), Toast.LENGTH_SHORT).show();


                String object_id = sp.getString("ObjectId", "");
                SharedPreferences.Editor edit = sp.edit();
                String object_ID = mListRecruit.get(position-1).getObjectId();
                if (!object_id.contains(object_ID)) {
                    object_id = object_id + object_ID + ",";
                    edit.putString("ObjectId",object_id);
                }
                edit.commit();
                listAdapter.notifyDataSetChanged();


                Intent intent = new Intent();
                intent.setClass(NearActivityRecruit.this,RecruitActivity.class);
                intent.putExtra("Recruit_information",mListRecruit.get(position-1));
                startActivity(intent);

            }
        });

        //下拉刷新监听
        lvList.setOnRefershListener(new RefreshListView.OnRefreshListener() {
            //刷新操作
            @Override
            public void onRefresh() {
                listAdapter.notifyDataSetChanged();
                initData();
                // Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void initData() {
        final ProgressDialog progress = new ProgressDialog(NearActivityRecruit.this);
        progress.setMessage("正在加载，请稍后...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        mListRecruit = new ArrayList<Recruit_information>();
        BmobQuery<Recruit_information> query = new BmobQuery<Recruit_information>();
        query.order("apply_time,createdAt");
        // query.setLimit(50);
        query.findObjects(new FindListener<Recruit_information>() {
            @Override
            public void done(final List<Recruit_information> list, BmobException e) {
                if (e == null) {
                    mListRecruit = list;
                    progress.dismiss();
                    if (listAdapter == null) {
                        listAdapter = new MyAdapter();
                        lvList.setAdapter(listAdapter);
                    }
                    lvList.onRefreshComplete(true);
                }else {
                    Toast.makeText(NearActivityRecruit.this,"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class MyAdapter extends BaseAdapter {

        private BitmapUtils utils;

        public MyAdapter() {
            utils = new BitmapUtils(NearActivityRecruit.this);
            //设置加载过程中的默认图片
            utils.configDefaultLoadingImage(R.mipmap.umeng_socialize_share_pic);
        }

        @Override
        public int getCount() {
            return mListRecruit.size();
        }

        @Override
        public Recruit_information getItem(int i) {
            return mListRecruit.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            if (view == null) {
                view = View.inflate(NearActivityRecruit.this, R.layout.item_recruit, null);

                holder = new ViewHolder();
                holder.iv_recruit = (ImageView) view.findViewById(R.id.iv_recruit);
                holder.tv_recruit = (TextView) view.findViewById(R.id.tv_recruit);
                holder.tv_activity_time = (TextView) view.findViewById(R.id.tv_activity_time);
                holder.tv_activity_address = (TextView) view.findViewById(R.id.tv_activity_address);

                utils.display(holder.iv_recruit,mListRecruit.get(i).getActivity_image().getFileUrl());

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_recruit.setText(mListRecruit.get(i).getGather_time() + mListRecruit.get(i).getAddress() + "--义工招募");
            holder.tv_activity_time.setText("时间："+mListRecruit.get(i).getGather_time());
            holder.tv_activity_address.setText("地点："+mListRecruit.get(i).getAddress());

            if (sp==null) {
                sp = getSharedPreferences("object_id",MODE_PRIVATE);
            }



            //设置字体颜色
            String objectId = sp.getString("ObjectId", "");
            if (objectId.contains(mListRecruit.get(i).getObjectId())){
                holder.tv_recruit.setTextColor(Color.GRAY);
                holder.tv_activity_time.setTextColor(Color.GRAY);
                holder.tv_activity_address.setTextColor(Color.GRAY);
            }else {
                holder.tv_recruit.setTextColor(Color.BLACK);
                holder.tv_activity_time.setTextColor(Color.GRAY);
                holder.tv_activity_address.setTextColor(Color.GRAY);
            }

            return view;
        }
    }

    public static class ViewHolder {
        public ImageView iv_recruit;
        public TextView tv_recruit;
        public TextView tv_activity_time;
        public TextView tv_activity_address;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
