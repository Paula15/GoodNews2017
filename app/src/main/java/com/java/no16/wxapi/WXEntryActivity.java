package com.java.no16.wxapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.java.no16.R;
import com.java.no16.service.GetNewsDetailService;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import java.util.List;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    final private String AppId = "wx56a8c54118422311";

    private IWXAPI api;

    Bitmap returnMap;
    private int type;

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
                wechatShare(0);
            }
        });
        ((Button) findViewById(R.id.ShareToMoment)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
        if (flag == 0) {
            type = SendMessageToWX.Req.WXSceneSession;
        } else {
            type = SendMessageToWX.Req.WXSceneTimeline;
        }
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = getIntent().getStringExtra("url");
                WXMediaMessage message = new WXMediaMessage(webpage);
                message.title = getIntent().getStringExtra("title");
                message.description = getIntent().getStringExtra("content").substring(0, 50);
                returnMap = ((Bitmap) msg.obj);
                message.setThumbImage(returnMap);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("Req");
                req.message = message;
                req.scene = type;
                boolean temp = api.sendReq(req);
                Log.e("temp", String.valueOf(temp));
            };
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = GetNewsDetailService.getImage(((List<String>) getIntent().getSerializableExtra("ImageUrls")).get(0));

                    Message message = Message.obtain();
                    message.obj = scaleDown(bitmap, 10, true);

                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.e("failed", "failed");
                    Message message = Message.obtain();
                    message.obj = BitmapFactory.decodeResource(getResources(), R.id.menu_star);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        Log.e("errCode: ", String.valueOf(baseResp.errCode));
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