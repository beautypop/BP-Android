package com.beautypop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.util.CategorySelectorViewUtil;
import com.beautypop.util.FeedFilter;
import com.beautypop.util.ImageMapping;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoryFeedViewFragment extends FeedViewFragment {
    private static final String TAG = CategoryFeedViewFragment.class.getName();

    private int[] catsRowLayoutIds = { R.id.catsRow1Layout, R.id.catsRow2Layout };
    private int[] catLayoutIds = { R.id.cat1, R.id.cat2, R.id.cat3, R.id.cat4, R.id.cat5, R.id.cat6, R.id.cat7, R.id.cat8 };
    private int[] imageIds = { R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5, R.id.image6, R.id.image7, R.id.image8 };
    private int[] nameIds = { R.id.name1, R.id.name2, R.id.name3, R.id.name4, R.id.name5, R.id.name6, R.id.name7, R.id.name8 };

    private CategorySelectorViewUtil catSelectorViewUtil;

    private CategoryVM category;
    private Long catId;

    private FeedFilter.FeedType feedType;
    private FeedFilter.ConditionType conditionType;

    private ImageView backImage, newPostAction;

    private Button popularFilterButton, newestFilterButton, priceLowHighFilterButton, priceHighLowFilterButton;
    private Button newFilterButton, usedFilterButton, allFilterButton;

    private FrameLayout tipsLayout;
    private ImageView dismissTipsButton;

    @Override
    protected void initFeedFilter() {
        FeedFilter.FeedType feedType = getFeedType(
                getArguments().getString(ViewUtil.BUNDLE_KEY_FEED_TYPE));
        FeedFilter.ConditionType conditionType = getFeedFilterConditionType(
                getArguments().getString(ViewUtil.BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE));
        Long objId = getArguments().getLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1);
        setFeedFilter(new FeedFilter(feedType, conditionType, objId));
    }

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.category_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        backImage = (ImageView) getActivity().findViewById(R.id.backImage);
        newPostAction = (ImageView) getActivity().findViewById(R.id.newPostAction);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        newPostAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startNewPostActivity(getActivity(), catId);
            }
        });

        // feed type filters
        popularFilterButton = (Button) headerView.findViewById(R.id.popularFilterButton);
        newestFilterButton = (Button) headerView.findViewById(R.id.newestFilterButton);
        priceLowHighFilterButton = (Button) headerView.findViewById(R.id.priceLowHighFilterButton);
        priceHighLowFilterButton = (Button) headerView.findViewById(R.id.priceHighLowFilterButton);

        newFilterButton = (Button) headerView.findViewById(R.id.newFilterButton);
        usedFilterButton = (Button) headerView.findViewById(R.id.usedFilterButton);
        allFilterButton = (Button) headerView.findViewById(R.id.allFilterButton);

        popularFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_POPULAR);
            }
        });
        newestFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_NEWEST);
            }
        });
        priceLowHighFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_PRICE_LOW_HIGH);
            }
        });
        priceHighLowFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_PRICE_HIGH_LOW);
            }
        });
        selectFeedFilter(FeedFilter.FeedType.CATEGORY_POPULAR, false);

        allFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(FeedFilter.ConditionType.ALL);
            }
        });
        newFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(FeedFilter.ConditionType.NEW);
            }
        });
        usedFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(FeedFilter.ConditionType.USED);
            }
        });
        selectProductFilter(FeedFilter.ConditionType.ALL, false);

        // tips
        /*
        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        if (SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.CATEGORY_TIPS)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);

            dismissTipsButton = (ImageView) headerView.findViewById(R.id.dismissTipsButton);
            dismissTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.CATEGORY_TIPS);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }
        */

        // init
        catId = getArguments().getLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1L);
        setCategory(catId);

        return view;
    }

    private void selectFeedFilter(FeedFilter.FeedType feedType) {
        selectFeedFilter(feedType, true);
    }

    public void selectFeedFilter(FeedFilter.FeedType feedType, boolean loadFeed) {
        if (FeedFilter.FeedType.CATEGORY_POPULAR.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(popularFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(popularFilterButton);
        }

        if (FeedFilter.FeedType.CATEGORY_NEWEST.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(newestFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(newestFilterButton);
        }

        if (FeedFilter.FeedType.CATEGORY_PRICE_LOW_HIGH.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(priceLowHighFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(priceLowHighFilterButton);
        }

        if (FeedFilter.FeedType.CATEGORY_PRICE_HIGH_LOW.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(priceHighLowFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(priceHighLowFilterButton);
        }

        setFeedType(feedType);

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    getFeedType(),
                    getFeedFilterConditionType(),
                    catId));
        }
    }

    private void selectProductFilter(FeedFilter.ConditionType conditionType) {
        selectProductFilter(conditionType, true);
    }

    private void selectProductFilter(FeedFilter.ConditionType conditionType, boolean loadFeed) {
        if (FeedFilter.ConditionType.ALL.equals(conditionType)) {
            ViewUtil.selectFilterButtonStyle(allFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(allFilterButton);
        }

        if (FeedFilter.ConditionType.NEW.equals(conditionType)) {
            ViewUtil.selectFilterButtonStyle(newFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectFilterButtonStyle(newFilterButton);
        }

        if (FeedFilter.ConditionType.USED.equals(conditionType)) {
            ViewUtil.selectFilterButtonStyle(usedFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectFilterButtonStyle(usedFilterButton);
        }

        setFeedFilterConditionType(conditionType);

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    getFeedType(),
                    getFeedFilterConditionType(),
                    catId));
        }
    }

    private void setCategory(Long catId) {
        category = CategoryCache.getCategory(catId);
        setActionBarTitle(category.name);

        // subcategory views
        List<CategoryVM> subCategories = CategoryCache.getSubCategories(catId);
        catSelectorViewUtil = new CategorySelectorViewUtil(
                subCategories, catsRowLayoutIds, catLayoutIds, imageIds, nameIds, headerView, getActivity());
        catSelectorViewUtil.setNumCategoriesPerRow(4);
        catSelectorViewUtil.initLayout();
    }

    public FeedFilter.FeedType getFeedType() {
        return feedType;
    }

    public void setFeedType(FeedFilter.FeedType feedType) {
        this.feedType = feedType;
    }

    public FeedFilter.ConditionType getFeedFilterConditionType() {
        return conditionType;
    }

    public void setFeedFilterConditionType(FeedFilter.ConditionType conditionType) {
        this.conditionType = conditionType;
    }
}