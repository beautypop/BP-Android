package com.beautypop.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beautypop.R;
import com.beautypop.activity.ThemeActivity;
import com.beautypop.app.CategoryCache;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;

import java.lang.reflect.Field;

public class ThemeFragment extends TrendFragment {
    private static final String TAG = ThemeFragment.class.getName();
	private LinearLayout themeImagesLayout;
	private HorizontalScrollView horizontalView;

	@Override
	protected View getHeaderView(LayoutInflater inflater) {
		if (headerView == null) {
			headerView = inflater.inflate(R.layout.theme_fragment, null);
		}
		return headerView;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		themeImagesLayout = (LinearLayout)headerView. findViewById(R.id.themeImagesLayout);
		horizontalView = (HorizontalScrollView) headerView.findViewById(R.id.horizontalView);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW,R.id.themeLabel);

		showThemes();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

	public void showThemes(){

		themeImagesLayout.removeAllViews();

		int imageWidth = (int) ((double) ViewUtil.getDisplayDimensions(getActivity()).width() / 4);  // fit around 4 items
		int margin = 0;
		for (final CategoryVM vm : CategoryCache.getThemeCategories()) {

			FrameLayout layout = new FrameLayout(getActivity());
			layout.setLayoutParams(new ViewGroup.LayoutParams(imageWidth + margin, imageWidth + margin));
			//layout.setGravity(Gravity.CENTER);
			ImageView imageView = new ImageView(getActivity());
			imageView.setLayoutParams(new ViewGroup.LayoutParams(imageWidth, imageWidth));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			ImageUtil.displayImage(vm.getIcon(), imageView);
			layout.addView(imageView);

			TextView textView = new TextView(getActivity());
			textView.setText(vm.getName());
			textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            textView.setPadding(0,0,0,10);
			textView.setTextColor(Color.WHITE);
            textView.setTextSize(15);
			textView.setBackground(getActivity().getResources().getDrawable(R.drawable.gradient_bottom));
			layout.addView(textView);

			layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ThemeActivity.class);
					intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, vm.getId());
					startActivity(intent);
				}
			});
			themeImagesLayout.addView(layout);
		}
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

	@Override
	public void onResume() {
		super.onResume();
		reloadFeed();
		showThemes();
	}
}
