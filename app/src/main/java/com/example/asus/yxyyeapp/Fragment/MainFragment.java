package com.example.asus.yxyyeapp.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.yxyyeapp.Activity.NewRecruitActivity;
import com.example.asus.yxyyeapp.NearActivityRecruit;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.Recruit_information;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by 威威 on 2016/8/16.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public ViewPager vp_header; //图片
    private ViewGroup ll_point; //小圆点
    private ImageView[] tips;//装小圆点的ImageView数组
    private ImageView[] mImageViews;//装ImageView数组
    private int[] imgIdArray; //图片资源id
    private View vw;

    private TextView tv_activity_recruit;//近期活动招募
    private ImageView tv_recruit_pic;//活动招募照片
    private TextView tv_recruit_time;//活动招募截止时间
    private TextView tv_activity_theme;//活动主题
    private RelativeLayout rl_recruit_time;

    private String ImageUrl;
    private Bitmap bitmap;
    private  String apply_time;
    private  String gather_time;
    private String address;

    private Handler mHandler;
    private Handler handler2;
    private BitmapUtils utils;
    private List<Recruit_information> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        vw = inflater.inflate(R.layout.fragmen_main_layout,container,false);

        initView();

        initData();



        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    int currentItem = vp_header.getCurrentItem();
                    if (currentItem < (mImageViews.length - 1)) {
                        currentItem++;
                    } else {
                        currentItem = 0;
                    }
                    vp_header.setCurrentItem(currentItem);
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }
            };
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }


        /**
         * 招募信息
         */
        rl_recruit_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),NewRecruitActivity.class);
                intent.putExtra("informatoin",mList.get(mList.size()-1));
                startActivity(intent);
            }
        });

//
//        if (handler2 == null) {
//            final ProgressDialog progress = new ProgressDialog(getActivity());
//            progress.setMessage("正在加载，请稍后...");
//            progress.setCanceledOnTouchOutside(false);
//            progress.show();
//            handler2 = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    tv_recruit_pic.setImageBitmap(bitmap);
//                    tv_recruit_time.setText("截至招募时间:"+apply_time);
//                    tv_activity_theme.setText(address+gather_time+"招募义工");
//
//                }
//            };
//            progress.dismiss();
//        }

        return vw;
    }

    private void initView() {
        vp_header = (ViewPager) vw.findViewById(R.id.vp_header);
        ll_point = (ViewGroup) vw.findViewById(R.id.ll_point);
        imgIdArray = new int[]{R.mipmap.image3, R.mipmap.image7, R.mipmap.image8, R.mipmap.image6};  //载入图片资源ID
        tips = new ImageView[imgIdArray.length];

        tv_activity_recruit = (TextView) vw.findViewById(R.id.tv_activity_recruit);
        tv_recruit_pic = (ImageView) vw.findViewById(R.id.iv_recruit_pic);
        tv_recruit_time = (TextView) vw.findViewById(R.id.tv_recruit_time);
        tv_activity_theme = (TextView) vw.findViewById(R.id.tv_activity_theme);
        rl_recruit_time = (RelativeLayout) vw.findViewById(R.id.rl_recruit_time);


    }

    private void initData() {
        Load_pic_point();

        vp_header.setAdapter(new MyAdapter());
        vp_header.setOnPageChangeListener(this);
        tv_activity_recruit.setOnClickListener(this);

        QueryData();

    }


    private void QueryData() {

        BmobQuery<Recruit_information> query = new BmobQuery<Recruit_information>();
        query.setLimit(50);
        query.findObjects(new FindListener<Recruit_information>() {
            @Override
            public void done(final List<Recruit_information> list, BmobException e) {
                if (e == null) {
                    apply_time = list.get(list.size()-1).getApply_time();
                    gather_time = list.get(list.size()-1).getGather_time();
                    address = list.get(list.size()-1).getAddress();

                    tv_recruit_time.setText("截至招募时间:"+apply_time);
                    tv_activity_theme.setText(address+gather_time+"招募义工");

                    utils = new BitmapUtils(getActivity());
                    //设置加载过程中的默认图片
                    utils.configDefaultLoadingImage(R.mipmap.image7);

                    utils.display(tv_recruit_pic,list.get(list.size()-1).getActivity_image().getFileUrl());

                    mList = list;

//                    new Thread() {
//                        @Override
//                        public void run() {
//                            ImageUrl = list.get(list.size()-1).getActivity_image().getFileUrl();
//                            try {
//                                URL url = null;
//                                url = new URL(ImageUrl);
//                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                conn.setConnectTimeout(5000);
//                                conn.setRequestMethod("GET");
//                                if (conn.getResponseCode() == 200) {
//                                    InputStream inputStream = conn.getInputStream();
//                                    bitmap = BitmapFactory.decodeStream(inputStream);
//                                }
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            handler2.sendEmptyMessage(0);
//                        }
//                    }.start();
                } else {
                    Toast.makeText(getActivity(), "查询失败"+list.size(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * 加载小圆点
     */
    private void Load_pic_point() {
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.mvx);
            } else {
                tips[i].setBackgroundResource(R.mipmap.mvy);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 8;
            layoutParams.rightMargin = 8;
            ll_point.addView(imageView, layoutParams);
        }
        //将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }
    }

    //ViewPager的滑动
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
       // int currentItem = vp_header.getCurrentItem();//得到当前页面
        setImageBackground(position % mImageViews.length);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), NearActivityRecruit.class);
        startActivity(intent);
        //getActivity().finish();
    }


    /**
     * viewpager的适配器
     */
    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViews[position]);
            return mImageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.mipmap.mvx);
            } else {
                tips[i].setBackgroundResource(R.mipmap.mvy);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
