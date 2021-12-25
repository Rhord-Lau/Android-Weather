package com.example.myweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    //对okhttp进行封装，方便后面调用
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        //注册回调来处理服务器响应
        client.newCall(request).enqueue(callback);
    }
}
