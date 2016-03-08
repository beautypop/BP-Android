package com.beautypop.app;

import android.util.Log;

import com.beautypop.util.SharedPreferencesUtil;
import com.beautypop.viewmodel.CategoryVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoryCache {
    private static final String TAG = CategoryCache.class.getName();

    // top level categories
    private static List<CategoryVM> categories = new ArrayList<>();

    // custom categories
    private static List<CategoryVM> customCategories = new ArrayList<>();

    // all categories
    private static Map<Long, CategoryVM> allCategoriesMap = new HashMap<>();

    private CategoryCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<List<CategoryVM>> callback) {
        Log.d(TAG, "refresh");

        AppController.getApiService().getCategories(new Callback<List<CategoryVM>>() {
            @Override
            public void success(List<CategoryVM> vms, Response response) {
                if (vms == null || vms.size() == 0)
                    return;

                initAllCategories(vms);
                SharedPreferencesUtil.getInstance().saveCategories(vms);
                if (callback != null) {
                    callback.success(vms, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(TAG, "refresh: failure", error);
            }
        });
    }

    public static List<CategoryVM> getCategories() {
        if (categories == null || categories.size() == 0) {
            categories = SharedPreferencesUtil.getInstance().getCategories();
            initAllCategories(categories);
        }
        return categories;
    }

    public static List<CategoryVM> getSubCategories(Long id) {
        CategoryVM category = getCategory(id);
        if (category != null) {
            return category.subCategories;
        }
        return new ArrayList<>();
    }

    public static List<CategoryVM> getCustomCategories() {
        return customCategories;
    }

    public static CategoryVM getCategory(Long id) {
        return allCategoriesMap.get(id);
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.CATEGORIES);
    }

    private static void initAllCategories(List<CategoryVM> list) {
        categories.clear();
        customCategories.clear();
        for (CategoryVM category : list) {
            if ("PUBLIC".equalsIgnoreCase(category.categoryType)) {
                categories.add(category);
            } else if ("CUSTOM".equalsIgnoreCase(category.categoryType)){
                customCategories.add(category);
            }
        }

        allCategoriesMap.clear();
        for (CategoryVM category : categories) {
            allCategoriesMap.put(category.id, category);
            for (CategoryVM subCategory : category.subCategories) {
                allCategoriesMap.put(subCategory.id, subCategory);
            }
        }
        for (CategoryVM customCategory : customCategories) {
            allCategoriesMap.put(customCategory.id, customCategory);
        }
    }
}
