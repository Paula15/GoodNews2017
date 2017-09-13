package com.java.no16.ui.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.java.no16.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareActivity extends AppCompatActivity {
    final private String AppId = "455fbcb9466032c67b179613f0958764";

    public static IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);

        api = WXAPIFactory.createWXAPI(this, AppId, false);
        api.registerApp(AppId);
        ((Button) findViewById(R.id.ShareToFriends)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Friend", "share");
                wechatShare(0);
            }
        });
        ((Button) findViewById(R.id.ShareToMoment)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("Moment", "share");
                wechatShare(1);
            }
        });
    }


    private void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = getIntent().getStringExtra("url");
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = getIntent().getStringExtra("title");
//        msg.description = getIntent().getStringExtra("content");
        msg.description = "Content";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_black_24dp);
        msg.setThumbImage(thumb);
        Log.e("title", msg.title);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }
}