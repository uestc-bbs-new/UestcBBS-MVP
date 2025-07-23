package com.scatl.uestcbbs.util;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.api.ApiConstant;
import com.scatl.uestcbbs.module.webview.view.WebViewActivity;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class VpnLoginInterceptor implements Interceptor {
    public static boolean vpnWebViewOpen = false;
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response r = chain.proceed(chain.request());
        String location;
        if (r.isRedirect() && (location = r.header("Location")) != null && location.startsWith(ApiConstant.UESTC_VPN_LOGIN_URL)) {
            android.util.Log.d("webvpn", "in");
            if (!vpnWebViewOpen) {
                android.util.Log.d("webvpn", "open webview");
                vpnWebViewOpen = true;
                Intent intent = new Intent(App.getContext(), WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.IntentKey.URL, ApiConstant.UESTC_VPN_LOGIN_URL);
                App.getContext().startActivity(intent);
            }
            throw new IOException("请登录 VPN。");
        }
        return r;
    }
}
