package com.beautypop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.fragment.UserProfileFeedViewFragment;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ViewUtil;

public class UserProfileActivity extends TrackedFragmentActivity {

    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_activity);

        backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        long userId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        if (userId <= 0L) {
            userId = ViewUtil.getIntentFilterLastPathSegment(getIntent());
        }

        Bundle bundle = new Bundle();
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, FeedFilter.FeedType.USER_POSTED.name());
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE, DefaultValues.DEFAULT_FEED_FILTER_CONDITION_TYPE.name());
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, userId);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserProfileFeedViewFragment profileFragment = new UserProfileFeedViewFragment();
        profileFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolder, profileFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}


