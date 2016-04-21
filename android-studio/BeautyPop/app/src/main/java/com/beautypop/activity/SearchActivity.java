package com.beautypop.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.fragment.SearchProductFragment;
import com.beautypop.fragment.SearchUserFragment;
import com.beautypop.util.ViewUtil;

public class SearchActivity extends FragmentActivity {

	private static final String TAG = SearchActivity.class.getName();

	private SearchView searchView;
	private String searchKey;
	private ImageView backImage;
	private TextView productText,userText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);

		searchView = (SearchView) findViewById(R.id.searchView);
		backImage = (ImageView) findViewById(R.id.backImage);
		productText = (TextView) findViewById(R.id.productText);
		userText = (TextView) findViewById(R.id.userText);

		searchView.setIconified(false);

		productText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSearchProductTab();
            }
        });

		userText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                clickSearchUserTab();
			}
		});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchKey = s;

                return false;
            }
        });

		backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        clickSearchProductTab();
	}

	private void clickSearchProductTab() {
        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        bundle.putString(ViewUtil.BUNDLE_KEY_NAME,searchKey);
        bundle.putLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID,getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1L));
        fragment = new SearchProductFragment();
        fragment.setArguments(bundle);
        fragment.setTrackedOnce();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeHolder, fragment).commit();

        productText.setBackgroundColor(getResources().getColor(R.color.gray));
        productText.setTextColor(getResources().getColor(R.color.white));

        userText.setBackgroundColor(getResources().getColor(R.color.white));
        userText.setTextColor(getResources().getColor(R.color.gray));
    }

    private void clickSearchUserTab() {
        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        bundle.putString(ViewUtil.BUNDLE_KEY_NAME,searchKey);
        fragment = new SearchUserFragment();
        fragment.setArguments(bundle);
        fragment.setTrackedOnce();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeHolder, fragment).commit();

        userText.setBackgroundColor(getResources().getColor(R.color.gray));
        userText.setTextColor(getResources().getColor(R.color.white));

        productText.setBackgroundColor(getResources().getColor(R.color.white));
        productText.setTextColor(getResources().getColor(R.color.gray));
    }
}
