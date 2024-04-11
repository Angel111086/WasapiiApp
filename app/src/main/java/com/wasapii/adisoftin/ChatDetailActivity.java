package com.wasapii.adisoftin;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wasapii.adisoftin.adapters.ChatRecyclerAdapter;

import com.wasapii.adisoftin.events.PushNotificationEvent;
import com.wasapii.adisoftin.fragments.ChatFragment;
import com.wasapii.adisoftin.model.ChatUser;


import net.steamcrafted.materialiconlib.MaterialIconView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


public class ChatDetailActivity extends AppCompatActivity //implements ChatContract.View{
{
    Toolbar toolbar;
    private Context ctx;
    ImageView btn_like,btn_send,btn_meet;
    EditText et_message;
    private RecyclerView mRecyclerViewChat;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    //private ChatPresenter mChatPresenter;
    ChatFragment fr;
    String rece_id;
    private FirebaseAuth mAuth;

    /*public static int SENDER_ID = 0;
    public static int RECEIVER_ID = 0;
    public static int BLOCK_STATUS = 0;
    public static String MY_RECEIVER;*/




    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /*@Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }*/




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_detail);

        ctx = ChatDetailActivity.this;
        mAuth = FirebaseAuth.getInstance();
      btn_like = (ImageView) findViewById(R.id.btn_like);
      btn_meet = (ImageView) findViewById(R.id.btn_meet);
      btn_send = (ImageView) findViewById(R.id.btn_send);
      et_message = (EditText) findViewById(R.id.et_message);
       // mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        //mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<ChatUser>());

        initToolBar();
        String rece_id = getIntent().getStringExtra("ChatUserID");
        Log.e("RECE",rece_id);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
                Log.e("Called","Meet");
            }
        });

    }



    private void init() {

        et_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //sendMessage();
                    return true;
                }
                return false;
            }
        });
        fr = new ChatFragment();
        //mChatPresenter = new ChatPresenter(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(ctx,"Null",Toast.LENGTH_LONG).show();
        }
        else{
            String uid = new ChatUser().getSenderUid();
            Log.e("UID",uid);
            //mChatPresenter.getMessage(uid,rece_id);
        }

//        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                fr.getArguments().getString(rece_id));

    }


//    private void sendMessage() {
//        fr = new ChatFragment();
//        String message = et_message.getText().toString();
//        String receiver = fr.getArguments().getString(Constants.ARG_RECEIVER);
//        String receiverUid = fr.getArguments().getString(Constants.ARG_RECEIVER_UID);
//
//        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String receiverFirebaseToken = fr.getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
//        ChatUser chat = new ChatUser(sender,
//                receiver,
//                senderUid,
//                receiverUid,
//                message,
//                new String(System.currentTimeMillis()+""));
//        mChatPresenter.sendMessage(ChatDetailActivity.this,
//                chat,
//                receiverFirebaseToken);
//    }
//
//    @Override
//    public void onSendMessageSuccess() {
//        et_message.setText("");
//        Toast.makeText(ctx, "Message sent", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onSendMessageFailure(String message) {
//        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onGetMessagesSuccess(ChatUser chat) {
//        if (mChatRecyclerAdapter == null) {
//            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
//            mRecyclerViewChat.setLayoutManager(new LinearLayoutManager(ChatDetailActivity.this));
//            mRecyclerViewChat.setHasFixedSize(true);
//        }
//        mChatRecyclerAdapter.add(chat);
//        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
//    }
//
//    @Override
//    public void onGetMessagesFailure(String message) {
//        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
//    }
//    @Subscribe
//    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
//        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
//            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                    pushNotificationEvent.getUid());
//        }
//    }

    public void initToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        MaterialIconView btn_back = (MaterialIconView) toolbar.findViewById(R.id.btn_back);

        //title.setText("");
        String number = getIntent().getStringExtra("Contact");
        title.setText(number);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }

        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            Window window = getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(Color.BLACK);//(ctx.getResources().getColor(R.color.statusbar));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false);
    }


   }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String not_type = intent.getStringExtra("not_type");
//
//            if (not_type.equals("msg")) {
//
//                int s_id = intent.getIntExtra("sender", -1);
//                int r_id = intent.getIntExtra("receiver", -1);
//                String message = intent.getStringExtra("message");
//                String sender_image = intent.getStringExtra("sender_image");
//                String messageType = intent.getStringExtra("messagetype");
//                String date = intent.getStringExtra("date");
//                long timeout = intent.getLongExtra("timeout", 20000);
//
//                if (s_id == -1 || r_id == -1) {
//
//                } else if (s_id == SENDER_ID && r_id == RECEIVER_ID || r_id == SENDER_ID && s_id == RECEIVER_ID) {
//                    arrayList.add(new ChatModel(s_id, r_id, sender_image, message, messageType, 0, date, timeout));
//                    adt.notifyDataSetChanged();
//                    listview.smoothScrollToPosition(adt.getCount() - 1);
//                    if (messageType.equals("text")) {
//                        messageReaded(s_id, r_id, message);
//                    }
//                }
//            } else if (not_type.equals("read")) {
//
//
//                int s_id = intent.getIntExtra("sender", -1);
//                int r_id = intent.getIntExtra("receiver", -1);
//                String message = intent.getStringExtra("message");
//                String type = intent.getStringExtra("msg_type");
//
//                if (type.equals("image")) {
//                    updateImagesArrayList(s_id, r_id, message);
//                } else if (type.equals("text")) {
//                    updateArrayList(s_id, r_id);
//                }
//            }
//        }
//    };
//
//}
