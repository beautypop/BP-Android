package com.beautypop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beautypop.R;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.CategorySelectorViewUtil;
import com.beautypop.viewmodel.CategoryVM;

import java.util.List;

public class HomeCategoryPagerFragment extends TrackedFragment {
    private static final String TAG = HomeCategoryPagerFragment.class.getName();

    private int[] catsRowLaoutIds = { R.id.catsRow1Layout, R.id.catsRow2Layout, R.id.catsRow3Layout };
    private int[] catLayoutIds = { R.id.cat1, R.id.cat2, R.id.cat3, R.id.cat4, R.id.cat5, R.id.cat6, R.id.cat7, R.id.cat8, R.id.cat9 };
    private int[] imageIds = { R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5, R.id.image6, R.id.image7, R.id.image8, R.id.image9 };
    private int[] nameIds = { R.id.name1, R.id.name2, R.id.name3, R.id.name4, R.id.name5, R.id.name6, R.id.name7, R.id.name8, R.id.name9 };

    private CategorySelectorViewUtil catSelectorViewUtil;

    private int page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_category_pager_fragment, container, false);
        initLayout(view);
        return view;
    }

    private void initLayout(View view) {
        List<CategoryVM> categories = HomeCategoryPagerAdapter.getCategoriesForPosition(page);
        catSelectorViewUtil = new CategorySelectorViewUtil(
                categories, catsRowLaoutIds, catLayoutIds, imageIds, nameIds, view, getActivity());
        catSelectorViewUtil.initLayout();
    }

    public void setPage(int page) {
        this.page = page;
    }
}
