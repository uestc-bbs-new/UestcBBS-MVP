package com.scatl.uestcbbs.module.webview.view

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.RelativeLayout
import com.alibaba.fastjson.JSONObject
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient
import com.scatl.uestcbbs.R
import com.scatl.uestcbbs.api.ApiConstant
import com.scatl.uestcbbs.base.BaseVBActivity
import com.scatl.uestcbbs.databinding.ActivityWebViewBinding
import com.scatl.uestcbbs.module.webview.presenter.WebViewPresenter
import com.scatl.uestcbbs.util.CommonUtil
import com.scatl.uestcbbs.util.Constant
import com.scatl.uestcbbs.util.SharePrefUtil
import com.scatl.uestcbbs.util.VpnLoginInterceptor
import com.scatl.util.ColorUtil.getAttrColor

/**
 * Created by sca_tl at 2023/6/2 13:40
 */
class WebViewActivity: BaseVBActivity<WebViewPresenter, WebViewView, ActivityWebViewBinding>(), WebViewView {

    private var url: String? = "https://www.example.com"
    private var agentWeb: AgentWeb? = null
    private var runInitScript = false

    override fun getViewBinding() = ActivityWebViewBinding.inflate(layoutInflater)

    override fun initPresenter() = WebViewPresenter()

    override fun getIntent(intent: Intent?) {
        super.getIntent(intent)
        intent?.let {
            url = it.getStringExtra(Constant.IntentKey.URL)
        }
    }

    override fun initView(theftProof: Boolean) {
        super.initView(false)
        agentWeb = AgentWeb
                .with(this)
                .setAgentWebParent(mBinding.container, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(getAttrColor(this, R.attr.colorPrimary))
                .setWebChromeClient(object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        if (newProgress == 100 && !runInitScript && view?.url?.startsWith(ApiConstant.UESTC_VPN_AUTH_URL) == true) {
                            runInitScript = true
                            val user = JSONObject.toJSONString(SharePrefUtil.getVpnUser(getContext()))
                            val pass = JSONObject.toJSONString(SharePrefUtil.getVpnPass(getContext()))
                            view.evaluateJavascript("""(function() {
                              document.querySelector('#pwdFromId input[name=username]').value=$user;
                              document.querySelector('#pwdFromId input[name=passwordText], #pwdFromId input[name=userPassword]').value=$pass;
                              document.querySelector('#pwdFromId #login_submit').click();
                            })()""", { result -> android.util.Log.d("webvpn", result) })
                        }
                        super.onProgressChanged(view, newProgress)
                    }
                    override fun onReceivedTitle(view: WebView, title: String) {
                        mBinding.toolbar.title = title
                        if (view.url?.startsWith(ApiConstant.UESTC_VPN_AUTH_URL) == true) {
                        } else if (url?.startsWith(ApiConstant.UESTC_VPN_ROOT_URL) == true) {
                            val cookieManager = CookieManager.getInstance()
                            val cookies = cookieManager.getCookie(ApiConstant.UESTC_VPN_ROOT_URL)
                            val authCookie = cookies.split(Regex(";\\s*")).find { cookie -> cookie.startsWith(ApiConstant.UESTC_VPN_AUTH_COOKIE + "=") }
                            if (authCookie?.isNotEmpty() == true) {
                                SharePrefUtil.setVpnAuthCookie(getContext(), authCookie)
                            }
                        }
                        super.onReceivedTitle(view, title)
                    }
                })
                .createAgentWeb()
                .ready()
                .go(url)
    }

    override fun onOptionsSelected(item: MenuItem?) {
        if (item?.itemId == R.id.menu_open_by_system) {
            CommonUtil.openBrowser(this, url)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (agentWeb?.handleKeyEvent(keyCode, event) == true) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onDestroy() {
        VpnLoginInterceptor.vpnWebViewOpen = false;
        super.onDestroy()
        agentWeb?.destroy()
    }

    override fun getContext() = this
}