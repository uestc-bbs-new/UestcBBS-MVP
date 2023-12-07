package com.scatl.uestcbbs.module.server;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.util.SharePrefUtil;

public class ServerActivity extends BaseActivity {

    private EditText urlField;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_server;
    }

    @Override
    protected void findView() {
        urlField = findViewById(R.id.server_url);
    }

    @Override
    protected void initView() {
        super.initView();
        urlField.setText(SharePrefUtil.getServerUrl(App.getContext()));
        urlField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharePrefUtil.setServerUrl(App.getContext(), charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    protected BasePresenter initPresenter() {
        return new ServerPresenter();
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucent(this);
    }

}
