package com.beautypop.activity;

import com.beautypop.R;
import com.beautypop.util.ViewUtil;

public class ForgetPasswordActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.login_forgot_password);
    }

    protected String getLoadUrl() {
        return FORGET_PASSWORD_URL;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}