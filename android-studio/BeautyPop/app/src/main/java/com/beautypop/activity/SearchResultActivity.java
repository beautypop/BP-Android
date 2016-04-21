package com.beautypop.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.fragment.SearchResultProductFragment;
import com.beautypop.fragment.SearchResultUserFragment;
import com.beautypop.util.ViewUtil;

public class SearchResultActivity extends FragmentActivity {
	private static final String TAG = SearchResultActivity.class.getName();

	private ImageView backImage;
	public SearchView searchView;
	private TextView titleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result_activity);

		backImage = (ImageView) findViewById(R.id.backImage);
		searchView = (SearchView) findViewById(R.id.searchView);
		titleText = (TextView) findViewById(R.id.titleText);

		searchView.setVisibility(View.GONE);
		titleText.setText(getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_NAME));
		//searchView.setIconified(false);
		//searchView.setVisibility(View.GONE);

		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		Bundle bundle = new Bundle();
		TrackedFragment fragment = null;
		if (getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_ACTION_TYPE).equals("user")) {
			fragment = new SearchResultUserFragment();
			bundle.putString(ViewUtil.BUNDLE_KEY_NAME, getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_NAME));
			fragment.setArguments(bundle);
			fragment.setTrackedOnce();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.placeHolder, fragment).commit();

		} else {
			fragment = new SearchResultProductFragment();
			bundle.putString(ViewUtil.BUNDLE_KEY_NAME, getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_NAME));
			bundle.putLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1L));
			fragment.setArguments(bundle);
			fragment.setTrackedOnce();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.placeHolder, fragment).commit();
		}
	}
}
