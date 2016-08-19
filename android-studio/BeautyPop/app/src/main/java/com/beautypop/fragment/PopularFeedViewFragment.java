package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.app.CategoryCache;
import com.beautypop.util.FeedFilter;

import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.bumptech.glide.Glide;

public class PopularFeedViewFragment extends FeedViewFragment {
    private static final String TAG = PopularFeedViewFragment.class.getName();

    private CategoryVM category;
    private Long catId;

    private FeedFilter.FeedType feedType;
    private FeedFilter.ConditionType conditionType;
	private TextView titleText,infoText;

    private ImageView backImage;
    private ImageView image;

    @Override
    protected FeedViewItemsLayout getFeedViewItemsLayout() {
        return FeedViewItemsLayout.TWO_COLUMNS;
    }

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
            headerView = inflater.inflate(R.layout.popular_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        backImage = (ImageView) getActivity().findViewById(R.id.backImage);

		infoText = (TextView)headerView. findViewById(R.id.infoText);
		titleText = (TextView)headerView. findViewById(R.id.titleText);
		image = (ImageView) headerView.findViewById(R.id.image);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

		CategoryVM categoryVM = CategoryCache.getCategory(getArguments().getLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1L));

		titleText.setText(categoryVM.getName());
		infoText.setText(categoryVM.getDescription());

			Glide
				.with(getActivity())
				.load(categoryVM.getIcon())
				.dontAnimate()
				.placeholder(R.drawable.ic_image_load)
				.into(image);


        selectFeedFilter(FeedFilter.FeedType.CATEGORY_POPULAR, false);

        return view;
    }

    public void selectFeedFilter(FeedFilter.FeedType feedType, boolean loadFeed) {
        setFeedType(feedType);

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    getFeedType(),
                    getFeedFilterConditionType(),
                    catId));
        }
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

}