package com.beautypop.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.adapter.FeedViewAdapter;
import com.beautypop.adapter.PopupCategoryListAdapter;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.TrackedFragment;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.PostVMLite;

import java.util.ArrayList;
import java.util.List;

public class SearchProductFragment extends TrackedFragment {
    private static final String TAG = SearchProductFragment.class.getName();

	protected LinearLayout selectCatLayout, selectSubCatLayout;

	public static int RECYCLER_VIEW_COLUMN_SIZE = 2;
	protected PopupCategoryListAdapter adapter;

	private static final int TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_TOP_MARGIN);
	private static final int BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_BOTTOM_MARGIN);
	private static final int SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_SIDE_MARGIN);
	private static final int LEFT_SIDE_MARGIN = (SIDE_MARGIN * 2) + ViewUtil.getRealDimension(2);
	private static final int RIGHT_SIDE_MARGIN = (SIDE_MARGIN * 2) + ViewUtil.getRealDimension(2);

    private RelativeLayout searchLayout;
    private PopupWindow categoryPopup;
    private PopupWindow subCategoryPopup;
    private RecyclerView feedView;
	protected FeedViewAdapter feedAdapter;
    private GridLayoutManager layoutManager;
    private List<PostVMLite> items;
    private TextView selectCatText, selectSubCatText;
    private ImageView selectCatIcon, selectSubCatIcon,catIcon,subCatIcon;
    private CategoryVM category;
    private CategoryVM subCategory;
    private TextView catName, subCatName;
	private SearchView searchView;
    private Long catId = -1L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_product_fragment, container, false);

		selectCatText = (TextView)view. findViewById(R.id.selectCatText);
		selectSubCatText = (TextView)view. findViewById(R.id.selectSubCatText);
		selectCatIcon = (ImageView) view.findViewById(R.id.selectCatIcon);
		selectSubCatIcon = (ImageView) view.findViewById(R.id.selectSubCatIcon);
		subCatName = (TextView) view.findViewById(R.id.subCatName);
		selectCatLayout = (LinearLayout) view.findViewById(R.id.selectCatLayout);
		selectSubCatLayout = (LinearLayout) view.findViewById(R.id.selectSubCatLayout);
		catIcon = (ImageView) view.findViewById(R.id.catIcon);
		catName = (TextView) view.findViewById(R.id.catName);
		subCatIcon = (ImageView) view.findViewById(R.id.subCatIcon);
		searchLayout = (RelativeLayout) view.findViewById(R.id.searchLayout);
		searchView = (SearchView) getActivity().findViewById(R.id.searchView);

		items = new ArrayList<>();

		catId = getArguments().getLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, -1);
		if (catId == null || catId <= 0) {
			setCategory(null);
			setSubCategory(null);
		} else {
			// set category, subcategory
			CategoryVM cat = CategoryCache.getCategory(catId);
			if (cat.parentId > 0) {
				category = CategoryCache.getCategory(cat.parentId);
				subCategory = cat;
			} else {
				category = cat;
				subCategory = null;
			}

			setCategory(category);
			setSubCategory(subCategory);
		}

		searchLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (searchView.getQuery().toString() != null && !searchView.getQuery().toString().equals("")) {
                    ViewUtil.startSearchResultActivity(getActivity(), "product", searchView.getQuery().toString(), catId);
				} else {
					Toast.makeText(getActivity(),"Enter search Text",Toast.LENGTH_LONG).show();
				}
			}
		});


		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {

				if (s != null && !s.equals("")) {
					ViewUtil.startSearchResultActivity(getActivity(), "product", s, catId);
				} else {
					Toast.makeText(getActivity(),"Enter search Text",Toast.LENGTH_LONG).show();
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}
		});

		feedView = (RecyclerView) view.findViewById(R.id.feedView);
		feedView.addItemDecoration(
				new RecyclerView.ItemDecoration() {
					@Override
					public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
						ViewUtil.FeedItemPosition feedItemPosition =
								ViewUtil.getFeedItemPosition(view, RECYCLER_VIEW_COLUMN_SIZE, false);
						if (feedItemPosition == ViewUtil.FeedItemPosition.HEADER) {
							outRect.set(0, 0, 0, 0);
						} else if (feedItemPosition == ViewUtil.FeedItemPosition.LEFT_COLUMN) {
							outRect.set(LEFT_SIDE_MARGIN, TOP_MARGIN, SIDE_MARGIN, BOTTOM_MARGIN);
						} else if (feedItemPosition == ViewUtil.FeedItemPosition.RIGHT_COLUMN) {
							outRect.set(SIDE_MARGIN, TOP_MARGIN, RIGHT_SIDE_MARGIN, BOTTOM_MARGIN);
						}
					}
				});

		feedAdapter = new FeedViewAdapter(getActivity(), items, null, false);
		feedView.setAdapter(feedAdapter);

		// layout manager
		layoutManager = new GridLayoutManager(getActivity(), RECYCLER_VIEW_COLUMN_SIZE);
		layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return feedAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
		feedView.setLayoutManager(layoutManager);

		selectCatLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initCategoryPopup(categoryPopup, CategoryCache.getCategories(), false);
			}
		});

		selectSubCatLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (category == null) {
					Toast.makeText(getActivity(), getString(R.string.invalid_post_no_category), Toast.LENGTH_SHORT).show();
					return;
				}
				initCategoryPopup(subCategoryPopup, CategoryCache.getSubCategories(category.id), true);
			}
		});

		ViewUtil.popupInputMethodWindow(getActivity(), searchView);

		return view;
	}

	protected void setCategory(CategoryVM cat) {
		this.category = cat;
		updateSelectCategoryLayout();
	}

	protected void setSubCategory(CategoryVM cat) {
		this.subCategory = cat;

		if(cat != null) {
			catId = cat.getId();
		}

		updateSelectSubCategoryLayout();
	}

	protected void updateSelectCategoryLayout() {
		if (category != null) {
			catName.setText(category.getName());
			ImageUtil.displayImage(category.getIcon(), catIcon);
			selectCatText.setVisibility(View.GONE);
			selectCatIcon.setVisibility(View.GONE);
			catIcon.setVisibility(View.VISIBLE);
			catName.setVisibility(View.VISIBLE);
		} else {
			selectCatText.setVisibility(View.VISIBLE);
			selectCatIcon.setVisibility(View.VISIBLE);
			catIcon.setVisibility(View.GONE);
			catName.setVisibility(View.GONE);
		}
	}

	protected void updateSelectSubCategoryLayout() {
		if (subCategory != null) {
			subCatName.setText(subCategory.getName());
			ImageUtil.displayImage(subCategory.getIcon(), subCatIcon);
			selectSubCatText.setVisibility(View.GONE);
			selectSubCatIcon.setVisibility(View.GONE);
			subCatIcon.setVisibility(View.VISIBLE);
			subCatName.setVisibility(View.VISIBLE);
		} else {
			selectSubCatText.setVisibility(View.VISIBLE);
			selectSubCatIcon.setVisibility(View.VISIBLE);
			subCatIcon.setVisibility(View.GONE);
			subCatName.setVisibility(View.GONE);
		}
	}

	protected void initCategoryPopup(PopupWindow popup, List<CategoryVM> categories, final boolean isSubCategory) {
		try {
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View layout = inflater.inflate(R.layout.category_popup_window,
					(ViewGroup) getActivity().findViewById(R.id.popupElement));

			if (popup == null) {
				popup = new PopupWindow(
						layout,
						ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_WIDTH),
						ViewGroup.LayoutParams.WRAP_CONTENT,    //ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_HEIGHT),
						true);
			}

			popup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
			popup.setOutsideTouchable(false);
			popup.setFocusable(true);
			popup.showAtLocation(layout, Gravity.CENTER, 0, 0);

			ListView listView = (ListView) layout.findViewById(R.id.categoryList);
			adapter = new PopupCategoryListAdapter(getActivity(), categories);
			listView.setAdapter(adapter);

			final PopupWindow thisPopup = popup;
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CategoryVM cat = adapter.getItem(position);
					if (!isSubCategory) {
						setCategory(cat);
						setSubCategory(null);
					} else {
						setSubCategory(cat);
					}
                    catId = cat.id;

					thisPopup.dismiss();
					Log.d(TAG, "initCategoryPopup: listView.onItemClick: category=" + category.getId() + "|" + category.getName());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
