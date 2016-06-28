package com.beautypop.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.app.TrackedFragmentActivity;
import com.beautypop.fragment.PurchasedReviewsFragment;
import com.beautypop.fragment.SoldReviewsFragment;
import com.beautypop.util.ViewUtil;

public class UserReviewsActivity extends TrackedFragmentActivity {

    public static final int PAGER_TAB_BUYER_REVIEWS = 0;
    public static final int PAGER_TAB_SELLER_REVIEWS = 1;

    private ViewPager viewPager;
	private ReviewPagerAdapter adapter;
	private PagerSlidingTabStrip tabs;
    private ImageView backImage;

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public void onStart() {
        super.onStart();

        final boolean buyerReviews = getIntent().getBooleanExtra(ViewUtil.BUNDLE_KEY_ARG1, true);

        if (buyerReviews) {
            getViewPager().setCurrentItem(PAGER_TAB_BUYER_REVIEWS);
        } else {
            getViewPager().setCurrentItem(PAGER_TAB_SELLER_REVIEWS);
        }
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.user_reviews_activity);

        setToolbarTitle(getString(R.string.reviews));

        final Long userId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.sellerTabs);
		viewPager = (ViewPager) findViewById(R.id.sellerPager);
		adapter = new ReviewPagerAdapter(getSupportFragmentManager(), userId);
        backImage = (ImageView) findViewById(R.id.backImage);

		int pageMargin = ViewUtil.getRealDimension(0);
		viewPager.setPageMargin(pageMargin);
		viewPager.setAdapter(adapter);

		tabs.setViewPager(viewPager);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}

class ReviewPagerAdapter extends FragmentStatePagerAdapter {

    private Long userId;

	public ReviewPagerAdapter(FragmentManager fm, Long userId) {
		super(fm);
        this.userId = userId;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ViewUtil.REVIEW_MAIN_TITLES[position];
	}

	@Override
	public int getCount() {
		return ViewUtil.REVIEW_MAIN_TITLES.length;
	}

	@Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item position=" + position);

		Bundle bundle = new Bundle();
		TrackedFragment fragment = null;
		switch (position) {
			case 0: {
				fragment = new SoldReviewsFragment();
                ((SoldReviewsFragment)fragment).setUserId(userId);
				break;
			}
			case 1: {
				fragment = new PurchasedReviewsFragment();
                ((PurchasedReviewsFragment)fragment).setUserId(userId);
				break;
			}
			default: {
				Log.e(this.getClass().getSimpleName(), "getItem: Unknown item position=" + position);
				break;
			}
		}

		if (fragment != null) {
			fragment.setArguments(bundle);
			fragment.setTrackedOnce();
		}
		return fragment;
	}
}
