package com.beautypop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beautypop.R;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.util.ViewUtil;

public class AdminActivity extends TrackedFragmentActivity {
    private static final String TAG = AdminActivity.class.getName();

    private LinearLayout newUsersLayout, latestLoginsLayout, latestCommentsLayout, latestConversationsLayout, newProductsLayout;
    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_activity);

        setToolbarTitle(getString(R.string.admin));

        setTracked(false);

        newUsersLayout = (LinearLayout) findViewById(R.id.newUsersLayout);
        latestLoginsLayout = (LinearLayout) findViewById(R.id.latestLoginsLayout);
        latestCommentsLayout = (LinearLayout) findViewById(R.id.latestCommentsLayout);
        latestConversationsLayout = (LinearLayout) findViewById(R.id.latestConversationsLayout);
        newProductsLayout = (LinearLayout) findViewById(R.id.newProductsLayout);

        // new users
        newUsersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminNewUsersActivity(AdminActivity.this);
            }
        });

        // latest logins
        latestLoginsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminLatestLoginsActivity(AdminActivity.this);
            }
        });

        // latest comments
        latestCommentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminCommentsActivity(AdminActivity.this);
            }
        });

        // latest conversations
        latestConversationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminConversationListActivity(AdminActivity.this);
            }
        });

        // new products
        newProductsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminNewProductsActivity(AdminActivity.this);
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}


