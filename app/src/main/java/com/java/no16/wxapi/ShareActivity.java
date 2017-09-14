package com.java.no16.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.java.no16.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class ShareActivity extends AppCompatActivity implements IWXAPIEventHandler {
    final private String AppId = "wx56a8c54118422311";

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);

        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp(AppId);
        api.handleIntent(getIntent(), this);
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

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    private void wechatShare(int flag){
        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = "http://www.baidu.com";
        webpage.webpageUrl = getIntent().getStringExtra("url");
        WXMediaMessage msg = new WXMediaMessage(webpage);
        //api.handleIntent(getIntent(), this);
        msg.title = getIntent().getStringExtra("title");
//        msg.description = getIntent().getStringExtra("content");
//        msg.title = "ggg";
        msg.description = "Content";
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_black_24dp);
//        msg.setThumbImage(thumb);
        Log.e("title", msg.title);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("Req");
        req.message = msg;
        if (flag == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        Log.e("transaction", req.transaction);
        Log.e("message", req.message.description);
        boolean temp = api.sendReq(req);
        Log.e("api status", String.valueOf(temp));
        Log.e("sent","send");
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("call:", "OnReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        Log.e("Onresp","onresp");

        switch (baseResp.errCode)
        {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消分享";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                break;
            default:
                result = "发送返回";
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        finish();

    }
    private String buildTransaction(final String type) {
        return (type == null)?String.valueOf(System.currentTimeMillis()):type + System.currentTimeMillis();
    }
}