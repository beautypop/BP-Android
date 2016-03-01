package com.beautypop.activity;

import com.beautypop.R;

public class SellerRulesActivity extends AbstractWebViewActivity {

    protected String getActionBarTitle() {
        return getString(R.string.seller_rules_title);
    }

    protected String getLoadUrl() {
        return SELLER_RULES_URL;
    }
}