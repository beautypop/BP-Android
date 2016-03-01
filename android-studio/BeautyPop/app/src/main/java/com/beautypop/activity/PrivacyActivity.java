package com.beautypop.activity;

import com.beautypop.R;

public class PrivacyActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.signup_privacy);
    }

    protected String getLoadUrl() {
        return PRIVACY_URL;
    }
}