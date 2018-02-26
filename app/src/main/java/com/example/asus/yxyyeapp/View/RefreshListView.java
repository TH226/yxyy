package com.example.asus.yxyyeapp.View;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.asus.yxyyeapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 带有下拉刷新头布局的listView
 */
public class RefreshListView extends ListView implements OnScrollListener,OnItemClickListener{

    private static final int STATE_PULL_REFRESH = 0; //下拉刷新
    private static final int STATE_RELEASE_REFRESH = 1;//松开刷新
    private static final int STATE_REFRESHING = 2;//正在刷新

    private View mHeaderView;
    private int startY = -1;
    private int mHeaderViewHeight;

    private  int mCurrentState = STATE_PULL_REFRESH;//当前状态
    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivArrow;
    private ProgressBar pbProgress;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
//    private View mFooterView;
//    private int mFooterViewHeight;

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
       // initFooterView();
    }


    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
       // initFooterView();
    }

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
      //  initFooterView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.refresh_header, null);
        this.addHeaderView(mHeaderView);

        tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
        ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arr);
        pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.pb_progress);


        //获取最上边title高度
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();

        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        initArrowAnim();
        tvTime.setText("最后刷新时间:"+getCurrentTime());
    }


//    /**
//     *初始化脚布局
//     */
//    private void initFooterView(){
//        mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
//        this.addFooterView(mFooterView); //添加脚布局，类似添加头布局
//
//        mFooterView.measure(0,0);
//        //得到脚布局的高度
//        mFooterViewHeight = mFooterView.getMeasuredHeight();
//
//        mFooterView.setPadding(0,-mFooterViewHeight,0,0);//隐藏脚布局
//
//        this.setOnScrollListener(this);
//    }




    private boolean isLoadingMore;
    /**
     * 状态的变化
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_FLING ||
                scrollState == SCROLL_STATE_IDLE){  //惯性滑动和停止滑动
            if(getLastVisiblePosition() == getCount() -1 && !isLoadingMore){//滑到最后了
             //   Toast.makeText(getContext(), "到底了", Toast.LENGTH_SHORT).show();

             //   mFooterView.setPadding(0,0,0,0);//显示脚布局

                setSelection(getCount());//改变ListView的显示位置

                isLoadingMore = true;

                if(mListener !=null){
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) { //防止ACTION_DOWN拿不到值的情况下，可以再次拿值
                    startY = (int) ev.getRawY();
                }
                if(mCurrentState == STATE_REFRESHING){
                    break;
                }
                int endY = (int) ev.getRawY();
                int dy = endY - startY;  //移动偏移量

                if (dy > 0 && getFirstVisiblePosition() == 0) {//只有下拉并且当前是第一个item才允许下拉
                    int padding = dy - mHeaderViewHeight;//计算padding
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if(padding > 0 && mCurrentState!=STATE_RELEASE_REFRESH){//状态改为松开刷新
                        mCurrentState = STATE_RELEASE_REFRESH;
                        refreshState();//刷新下拉列表的View
                    }else if(padding < 0 && mCurrentState != STATE_PULL_REFRESH){//下拉刷新状态
                        mCurrentState = STATE_PULL_REFRESH;
                        refreshState();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;

                if(mCurrentState == STATE_RELEASE_REFRESH){ //当松手时处于松开刷新
                    mCurrentState = STATE_REFRESHING;//正在刷新
                    mHeaderView.setPadding(0,0,0,0);//此时显示全部的headerView
                    refreshState();
                }else if(mCurrentState == STATE_PULL_REFRESH){
                    mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void refreshState() {
        switch (mCurrentState){
            case STATE_PULL_REFRESH:
                tvTitle.setText("下拉刷新");
                ivArrow.setVisibility(VISIBLE);
                pbProgress.setVisibility(INVISIBLE);
                ivArrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_REFRESH:
                tvTitle.setText("松开刷新");
                ivArrow.setVisibility(VISIBLE);
                pbProgress.setVisibility(INVISIBLE);
                ivArrow.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("正在刷新...");
                ivArrow.clearAnimation();//清除动画隐藏箭头
                ivArrow.setVisibility(INVISIBLE);
                pbProgress.setVisibility(VISIBLE);

                if(mListener!=null){
                    mListener.onRefresh();
                }
                break;
        }
    }

    /**
     * 箭头动画
     */
    private void initArrowAnim(){

        //箭头向上动画
        animUp = new RotateAnimation(0,-180, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        //箭头向下动画
        animDown = new RotateAnimation(-180,0, Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    OnRefreshListener mListener;

    public void setOnRefershListener(OnRefreshListener listener){
        mListener = listener;

    }

    public interface OnRefreshListener{
        public void onRefresh();//

        public void onLoadMore();//加载下一页
    }

    /**
     * 收起下拉刷新的控件
     */
    public void onRefreshComplete(boolean success){
        if(isLoadingMore){//正在加载更多数据
      //      mFooterView.setPadding(0,-mFooterViewHeight,0,0);//加载完成，隐藏脚布局
            isLoadingMore = false;
        }else{
            mCurrentState = STATE_PULL_REFRESH;
            tvTitle.setText("下拉刷新");
            ivArrow.setVisibility(VISIBLE);
            pbProgress.setVisibility(INVISIBLE);

            mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);//隐藏

            if(success){
                tvTime.setText("最后刷新时间:"+getCurrentTime());
            }
        }
    }

    public String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //月份M大写指月份从1开始，小写从0开始   时间H大写为24小时制，小写12小时制
        return format.format(new Date());
    }


    OnItemClickListener mItemClickListener;
    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
        mItemClickListener = listener;
    }

    /**
     * 自动在加载更多数据时，减去两个头布局造成的ListView多的2
     * getHeaderViewsCount这是ListView有的头布局的数量
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(parent, view, position
                    - getHeaderViewsCount(), id);
        }
    }
}
