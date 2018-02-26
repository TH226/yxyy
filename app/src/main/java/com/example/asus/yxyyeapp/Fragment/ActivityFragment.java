package com.example.asus.yxyyeapp.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.asus.yxyyeapp.Activity.RecruitActivity;
import com.example.asus.yxyyeapp.Activity.RecruitDetialActivity;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.Utils.CacheUtils;
import com.example.asus.yxyyeapp.View.RefreshListView;
import com.example.asus.yxyyeapp.bean.Recruit_information;
import com.lidroid.xutils.BitmapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 威威 on 2016/8/16.
 */
public class ActivityFragment extends Fragment {
    private View vw;
    private RefreshListView lvList;
    private List<Recruit_information> mListRecruit;


    private  ViewHolder holder;
    private MyAdapter listAdapter;
    private  Bitmap bitmap;

    private ImageButton IvRefresh;

    private static SharedPreferences sp = null;

    //private RefreshListView lvList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            holder.iv_recruit.setImageBitmap(bitmap);
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vw = inflater.inflate(R.layout.fragmen_activity_layout, container, false);

        lvList = (RefreshListView) vw.findViewById(R.id.lv_recruit_information);
//        IvRefresh = (ImageButton) vw.findViewById(R.id.refresh_activity);
//
//        IvRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listAdapter.notifyDataSetChanged();
//                initData();
//            }
//        });

        initView();
        initData();

        return vw;
    }

    private void initView() {

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                intent.setClass(getActivity(),RecruitActivity.class);
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
        final ProgressDialog progress = new ProgressDialog(getActivity());
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
                    Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class MyAdapter extends BaseAdapter {

        private BitmapUtils utils;

        public MyAdapter() {
            utils = new BitmapUtils(getActivity());
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
                view = View.inflate(getActivity(),R.layout.item_recruit,null);
                holder = new ViewHolder();
                holder.iv_recruit = (ImageView) view.findViewById(R.id.iv_recruit);
                holder.tv_recruit = (TextView) view.findViewById(R.id.tv_recruit);
                holder.tv_activity_time = (TextView) view.findViewById(R.id.tv_activity_time);
                holder.tv_activity_address = (TextView) view.findViewById(R.id.tv_activity_address);
                utils.display(holder.iv_recruit,mListRecruit.get(i).getActivity_image().getFileUrl());
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            new Thread() {
                @Override
                public void run() {
                    String fileUrl = mListRecruit.get(i).getActivity_image().getFileUrl();
                    try {
                        URL url = new URL(fileUrl);
                        try {
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setConnectTimeout(5000);
                            conn.setRequestMethod("GET");
                            if (conn.getResponseCode() == 200) {
                                InputStream inputStream = conn.getInputStream();
                                bitmap = BitmapFactory.decodeStream(inputStream);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }.start();
            holder.tv_recruit.setText(mListRecruit.get(i).getGather_time()+mListRecruit.get(i).getAddress()+"--义工招募");
            holder.tv_activity_time.setText(mListRecruit.get(i).getGather_time());
            holder.tv_activity_address.setText(mListRecruit.get(i).getAddress());

            if (sp==null) {
                sp = getActivity().getSharedPreferences("object_id", Context.MODE_PRIVATE);
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

    public static class ViewHolder{
        public ImageView iv_recruit;
        public TextView tv_recruit;
        public TextView tv_activity_time;
        public TextView tv_activity_address;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
