package com.beautypop.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautypop.viewmodel.CategoryVM;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectorViewUtil {
    private static final String TAG = CategorySelectorViewUtil.class.getName();

    private int[] catsRowLayoutIds;
    private int[] catLayoutIds;
    private int[] imageIds;
    private int[] nameIds;

    private List<ViewGroup> catsRowLaouts;
    private List<ViewGroup> catLayouts;
    private List<ImageView> images;
    private List<TextView> names;

    private View view;
    private Activity activity;
    private List<CategoryVM> categories;

    public CategorySelectorViewUtil(
            List<CategoryVM> categories,
            int[] catsRowLayoutIds, int[] catLayoutIds, int[] imageIds, int[] nameIds,
            View view, Activity activity) {
        this.view = view;
        this.activity = activity;
        this.categories = categories;
        this.catsRowLayoutIds = catsRowLayoutIds;
        this.catLayoutIds = catLayoutIds;
        this.imageIds = imageIds;
        this.nameIds = nameIds;
    }

    public void initLayout() {
        catsRowLaouts = new ArrayList<>();
        catLayouts = new ArrayList<>();
        images = new ArrayList<>();
        names = new ArrayList<>();
        for (int i = 0; i < catLayoutIds.length; i++) {
            catsRowLaouts.add((ViewGroup) view.findViewById(catLayoutIds[i]));
            catLayouts.add((ViewGroup) view.findViewById(catLayoutIds[i]));
            images.add((ImageView) view.findViewById(imageIds[i]));
            names.add((TextView) view.findViewById(nameIds[i]));
        }

        for (int i = 0; i < catLayoutIds.length; i++) {
            ViewGroup catLayout = catLayouts.get(i);
            ImageView image = images.get(i);
            TextView name = names.get(i);
            if (i < categories.size()) {
                final CategoryVM category = categories.get(i);
                initCategoryLayout(category, catLayout, image, name);
            } else {
                catLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initCategoryLayout(
            final CategoryVM category, ViewGroup catLayout, ImageView catImage, TextView nameText) {

        nameText.setText(category.getName());
        ImageUtil.displayImage(category.getIcon(), catImage);

        catLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startCategoryActivity(activity, category.getId());
            }
        });
        catLayout.setVisibility(View.VISIBLE);
    }



}