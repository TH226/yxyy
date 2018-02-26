package com.example.asus.yxyyeapp.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.yxyyeapp.Adapter.ChatMessageAdapter;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.Utils.HttpUtils;
import com.example.asus.yxyyeapp.bean.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 威威 on 2016/8/16.
 */
public class MessageFragment extends Fragment {

    private View view;

    private ListView mMsgs;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mDates;

    private EditText mInputMsg;
    private Button mSendMsg;

    private TextView myTv = null;
    private static final int UPDATE_MY_TV = 1;
    Message message = null;

    private Handler mHandler= new Handler(){
        public void handleMessage(Message msg){
            ChatMessage fromMessage = (ChatMessage) msg.obj;
            mDates .add(fromMessage);
            mAdapter.notifyDataSetChanged();
        };
    };



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case UPDATE_MY_TV:
                    String currentTime = (String)msg.obj;
                    myTv.setText(currentTime);
                    break;
            }
        }
    };
    private Thread updateCurrentTime = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.on_chat_layout, container, false);

        mMsgs = (ListView) view.findViewById(R.id.listview_msg);
        mInputMsg= (EditText) view.findViewById(R.id.id_edit_msg);
        mSendMsg = (Button) view.findViewById(R.id.id_btn);

        myTv = (TextView) view.findViewById(R.id.id_from_msg_date);


        updateCurrentTime = new Thread(){
            @Override
            public void run() {
                //Time Controller
                //Modify Time After 3000 ms
                SimpleDateFormat formatter =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date curDate = new Date(System.currentTimeMillis());
                String currentTime = formatter.format(curDate);
                message = handler.obtainMessage(UPDATE_MY_TV, currentTime);
                handler.sendMessage(message);
                //use Handler to control the time
                handler.postDelayed(this, 60000);
            }
        };
        updateCurrentTime.start();


        initDatas();
        //初始化事件
        initListener();
        return view;
    }


    private void initListener() {
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toMsg =  mInputMsg.getText().toString();
                if(TextUtils.isEmpty(toMsg)){
                    Toast.makeText(getActivity(), "发送消息不能为空", Toast.LENGTH_SHORT).show();
                    return ;
                }

                ChatMessage toMessage = new ChatMessage();
                toMessage.setDate(new Date());
                toMessage.setMsg(toMsg);
                toMessage.setType(ChatMessage.Type.OUTCOMING);
                mDates.add(toMessage);
                mAdapter.notifyDataSetChanged();
                mInputMsg.setText("");

                new Thread(){
                    public void run(){
                        ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
                        Message m =Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    };
                }.start();
            }
        });
    }

    private void initDatas() {
        mDates = new ArrayList<ChatMessage>();
        mDates.add(new ChatMessage("你好，我是志愿者，很高兴为你服务", ChatMessage.Type.INCOMING,new Date()));
        mAdapter = new ChatMessageAdapter(getActivity(),mDates);
        mMsgs.setAdapter(mAdapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }
}
