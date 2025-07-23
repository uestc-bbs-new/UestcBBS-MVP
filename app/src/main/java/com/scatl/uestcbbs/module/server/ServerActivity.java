package com.scatl.uestcbbs.module.server;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.jaeger.library.StatusBarUtil;
import com.scatl.uestcbbs.App;
import com.scatl.uestcbbs.R;
import com.scatl.uestcbbs.base.BaseActivity;
import com.scatl.uestcbbs.base.BasePresenter;
import com.scatl.uestcbbs.util.SharePrefUtil;

public class ServerActivity extends BaseActivity {

    private Switch enabledField;
    private EditText userField;
    private EditText passField;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.activity_server;
    }

    @Override
    protected void findView() {
        enabledField = findViewById(R.id.server_vpn_switch);
        userField = findViewById(R.id.vpn_user);
        passField = findViewById(R.id.vpn_pass);
    }

    @Override
    protected void initView() {
        super.initView();
        enabledField.setChecked(SharePrefUtil.isVpnEnabled(App.getContext()));
        enabledField.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharePrefUtil.setVpnEnabled(App.getContext(), b);
            }
        });
        userField.setText(SharePrefUtil.getVpnUser(App.getContext()));
        userField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharePrefUtil.setVpnUser(App.getContext(), charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        passField.setText(SharePrefUtil.getVpnPass(App.getContext()));
        passField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SharePrefUtil.setVpnPass(App.getContext(), charSequence.toString());
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
