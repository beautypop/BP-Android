package com.beautypop.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.fragment.CategoryFeedViewFragment;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ViewUtil;

public class CategoryActivity extends TrackedFragmentActivity {

	private SearchView searchView;
	private View toolBar;
	private ImageView newPostAction,searchImage;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_activity);


		toolBar = findViewById(R.id.toolbar);
		searchImage = (ImageView) findViewById(R.id.searchImage);
		searchView = (SearchView) findViewById(R.id.searchView);
		newPostAction = (ImageView)findViewById(R.id.newPostAction);

		searchImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CategoryActivity.this,SearchActivity.class);
				intent.putExtra(ViewUtil.BUNDLE_KEY_ID,getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L));
				startActivity(intent);
			}
		});

        // feed filter keys
		long catId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        if (catId == -1) {
            catId = ViewUtil.getIntentFilterLastPathSegment(getIntent());
        }

		

        Bundle bundle = new Bundle();
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, DefaultValues.DEFAULT_CATEGORY_FEED_TYPE.name());
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE, DefaultValues.DEFAULT_FEED_FILTER_CONDITION_TYPE.name());
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, catId);

        CategoryFeedViewFragment fragment = new CategoryFeedViewFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.childLayout, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_RESULT_REFRESH, false);
            if (refresh) {
                // refresh parent activity
                ViewUtil.setActivityResult(this, true);
                finish();
            }
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
