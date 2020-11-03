package com.addcn.yin_pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import io.flutter.plugin.common.MethodChannel.Result;

import java.util.Map;

public class AlipayUtil {
    private Result result;
    private final Activity context;
    private static final int SDK_PAY_FLAG = 1;

    public AlipayUtil(Activity context, Result result) {
        this.result = result;
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。

                    }
                    System.out.println("yin_pay alipay result =" + payResult);
                    result.success(payResult);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    ///调用支付宝
    public void invokeAlipay(final String orderInfoValue) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(context);
                Map<String, String> result = alipay.payV2(orderInfoValue, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(runnable);
        payThread.start();
    }

}