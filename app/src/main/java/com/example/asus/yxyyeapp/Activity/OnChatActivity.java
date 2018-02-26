package com.example.asus.yxyyeapp.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.yxyyeapp.Adapter.ChatMessageAdapter;
import com.example.asus.yxyyeapp.R;
import com.example.asus.yxyyeapp.bean.ChatMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OnChatActivity extends Activity{
    private ListView mMsgs;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mDates;

    private EditText mInputMsg;
    private Button mSendMsg;



    private TextView myTv = null;
    private static final int UPDATE_MY_TV = 1;
    Message message = null;

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
//    private Handler mHandler= new Handler(){
//        public void handleMessage(Message msg){
//                ChatMessage fromMessage = (ChatMessage) msg.obj;
//                mDates .add(fromMessage);
//                mAdapter.notifyDataSetChanged();
//        };
//    };
    private Thread updateCurrentTime = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_chat_layout);
        mMsgs = (ListView) findViewById(R.id.listview_msg);
        mInputMsg= (EditText) findViewById(R.id.id_edit_msg);
        mSendMsg = (Button) findViewById(R.id.id_btn);


        myTv = (TextView) findViewById(R.id.id_from_msg_date);


        updateCurrentTime = new Thread(){
            @Override
            public void run() {
                //Time Controller
                //Modify Time After 3000 ms
                SimpleDateFormat formatter =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String currentTime = formatter.format(curDate);
                message = handler.obtainMessage(UPDATE_MY_TV, currentTime);
                handler.sendMessage(message);
                //use Handler to control the time
                handler.postDelayed(this, 1000);
            }
        };
        updateCurrentTime.start();


       // initDatas();
        //初始化事件
        //initListener();
    }

//    private void initListener() {
//        mSendMsg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String toMsg =  mInputMsg.getText().toString();
//                if(TextUtils.isEmpty(toMsg)){
//                    Toast.makeText(OnChatActivity.this, "发送消息不能为空", Toast.LENGTH_SHORT).show();
//                    return ;
//                }
//
//                ChatMessage toMessage = new ChatMessage();
//                toMessage.setDate(new Date());
//                toMessage.setMsg(toMsg);
//                toMessage.setType(ChatMessage.Type.OUTCOMING);
//                mDates.add(toMessage);
//                mAdapter.notifyDataSetChanged();
//                mInputMsg.setText("");
//
////                new Thread(){
////                    public void run(){
////                        ChatMessage fromMessage = HttpUtils.sendMessage(toMsg);
////                        Message m =Message.obtain();
////                        m.obj = fromMessage;
////                        mHandler.sendMessage(m);
////                    };
////                }.start();
//            }
//        });
//    }
//    private void initDatas() {
//        mDates = new ArrayList<ChatMessage>();
//        mDates.add(new ChatMessage("你好，小小浩为您服务", ChatMessage.Type.INCOMING,new Date()));
//        mAdapter = new ChatMessageAdapter(this,mDates);
//        mMsgs.setAdapter(mAdapter);
//    }
}
