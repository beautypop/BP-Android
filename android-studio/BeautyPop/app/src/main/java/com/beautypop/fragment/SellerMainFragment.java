package com.beautypop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ViewUtil;

public class SellerMainFragment extends TrackedFragment {
    private static final String TAG = SellerMainFragment.class.getName();

    public static final int PAGER_TAB_FOLLOWING_FEED = 0;
    public static final int PAGER_TAB_RECOMMENDED_SELLERS = 1;

    private ViewPager viewPager;
    private SellerMainPagerAdapter adapter;
    private PagerSlidingTabStrip tabs;
	private ImageView searchImage;

    private static int preselectedTab = 0;

    private static SellerMainFragment mInstance;

    public static synchronized SellerMainFragment getInstance() {
        return mInstance;
    }

    public static void selectTab(int tab) {
        if (tab == PAGER_TAB_FOLLOWING_FEED) {
            selectFollowingFeedTab();
        } else {
            selectRecommendedSellersTab();
        }
    }

    public static void selectFollowingFeedTab() {
        if (mInstance != null && mInstance.isVisible()) {
            getInstance().getViewPager().setCurrentItem(PAGER_TAB_FOLLOWING_FEED);
            preselectedTab = 0;
        } else {
            preselectedTab = PAGER_TAB_FOLLOWING_FEED;
        }
    }

    public static void selectRecommendedSellersTab() {
        if (mInstance != null && mInstance.isVisible()) {
            getInstance().getViewPager().setCurrentItem(PAGER_TAB_RECOMMENDED_SELLERS);
            preselectedTab = 0;
        } else {
            preselectedTab = PAGER_TAB_RECOMMENDED_SELLERS;
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (preselectedTab == PAGER_TAB_FOLLOWING_FEED) {
            selectFollowingFeedTab();
        } else if (preselectedTab == PAGER_TAB_RECOMMENDED_SELLERS){
            selectRecommendedSellersTab();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.seller_main_fragment, container, false);

        mInstance = this;

		searchImage = (ImageView) getActivity().findViewById(R.id.searchImage);

		searchImage.setVisibility(View.GONE);

        // pager

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.sellerTabs);
        viewPager = (ViewPager) view.findViewById(R.id.sellerPager);
        adapter = new SellerMainPagerAdapter(getChildFragmentManager());

        int pageMargin = ViewUtil.getRealDimension(0);
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(adapter);

        tabs.setViewPager(viewPager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == SellerMainFragment.PAGER_TAB_FOLLOWING_FEED
                        && SellerFollowingFeedViewFragment.isRefreshFeed()) {
                    SellerFollowingFeedViewFragment.refreshFeed();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /*
        // styles declared in xml
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.actionbar_selected_text));

        int indicatorHeight = ViewUtil.getRealDimension(5, this.getResources());
        tabs.setIndicatorHeight(indicatorHeight);

        final int textSize = ViewUtil.getRealDimension(16, this.getResources());
        tabs.setTextSize(textSize);
        */

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment != null) {
                    Log.d(TAG, "onActivityResult: propagate to fragment=" + fragment.getClass().getSimpleName());
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}

/**
 * https://guides.codepath.com/android/Sliding-Tabs-with-PagerSlidingTabStrip
 * https://android-arsenal.com/details/1/1100
 */
class SellerMainPagerAdapter extends FragmentStatePagerAdapter {

    public SellerMainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ViewUtil.SELLER_MAIN_TITLES[position];
    }

    @Override
    public int getCount() {
        return ViewUtil.SELLER_MAIN_TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item position=" + position);

        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        switch (position) {
            // Following
            case SellerMainFragment.PAGER_TAB_FOLLOWING_FEED: {
                bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, FeedFilter.FeedType.HOME_FOLLOWING.name());
                fragment = new SellerFollowingFeedViewFragment();
                break;
            }
            // Seller
            case SellerMainFragment.PAGER_TAB_RECOMMENDED_SELLERS: {
                fragment = new SellerFeedFragment();
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

