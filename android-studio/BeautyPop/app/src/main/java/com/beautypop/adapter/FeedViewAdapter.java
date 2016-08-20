package com.beautypop.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.activity.ProductActivity;
import com.beautypop.app.AppController;
import com.beautypop.app.CategoryCache;
import com.beautypop.app.CountryCache;
import com.beautypop.app.UserInfoCache;
import com.beautypop.fragment.AbstractFeedViewFragment.FeedViewItemsLayout;
import com.beautypop.util.DefaultValues;
import com.beautypop.util.ImageUtil;
import com.beautypop.util.ViewUtil;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.CountryVM;
import com.beautypop.viewmodel.PostVM;
import com.beautypop.viewmodel.PostVMLite;
import com.beautypop.viewmodel.ResponseStatusVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 * https://github.com/chiuki/android-recyclerview
 */
public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.FeedViewHolder> {
    private static final String TAG = FeedViewAdapter.class.getName();

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private FeedViewItemsLayout feedViewItemsLayout;

    private View headerView;

    private Activity activity;

    private List<PostVMLite> items;

    private boolean showSeller = false;

    private int clickedPosition = -1;

    private boolean userSelect = false;

    private boolean pending = false;

    public FeedViewAdapter(Activity activity, List<PostVMLite> items,
                           FeedViewItemsLayout feedViewItemsLayout, View header) {
        this(activity, items, feedViewItemsLayout, header, false);
    }

    public FeedViewAdapter(Activity activity, List<PostVMLite> items,
                           FeedViewItemsLayout feedViewItemsLayout, View header, boolean showSeller) {
        this.activity = activity;
        this.items = items;
        this.feedViewItemsLayout = feedViewItemsLayout;
        this.headerView = header;
        this.showSeller = showSeller;
    }

    public int getClickedPosition() {
        return clickedPosition;
    }

    public boolean hasHeader() {
        return headerView != null;
    }

    public boolean isHeader(int position) {
        if (hasHeader()) {
            return position == 0;
        }
        return false;
    }

    public PostVMLite getItem(int position) {
        if (hasHeader()) {
            return items.get(position - 1);
        }
        return items.get(position);
    }

    public PostVMLite removeItem(int position) {
        if (hasHeader()) {
            return items.remove(position - 1);
        }
        return items.remove(position);
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    public PostVMLite getLastItem() {
        return getItem(getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        if (hasHeader()) {
            return items.size() + 1;
        }
        return items.size();
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new FeedViewHolder(headerView);  // Dummy!! This will not be binded!!
        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_view_item, parent, false);
        FeedViewHolder holder = new FeedViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }

        final PostVMLite item = getItem(position);

        if (item.getImages() != null && item.getImages().length != 0) {
            loadImage(item.getImages()[0], holder.image);
        }

        //holder.itemCard.setPreventCornerOverlap(false);

        if (showSeller) {
            ImageUtil.displayThumbnailProfileImage(item.getOwnerId(), holder.sellerImage);
            holder.sellerImage.setVisibility(View.VISIBLE);
            holder.sellerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.startUserProfileActivity(activity, item.getOwnerId());
                }
            });
        } else {
            holder.sellerImage.setVisibility(View.INVISIBLE);
        }

        if (item.sold) {
            holder.soldImage.setVisibility(View.VISIBLE);
        } else {
            holder.soldImage.setVisibility(View.INVISIBLE);
        }

        if (!StringUtils.isEmpty(item.countryCode) &&
                !item.countryCode.equalsIgnoreCase(CountryCache.COUNTRY_CODE_NA)) {
            ImageUtil.displayImage(item.countryIcon, holder.countryImage);
            holder.countryImage.setVisibility(View.VISIBLE);
        } else {
            holder.countryImage.setVisibility(View.GONE);
        }

        ViewUtil.setHtmlText(item.getTitle(), holder.title, activity, true);

        holder.price.setText(ViewUtil.formatPrice(item.getPrice()));
        if (item.getOriginalPrice() > 0) {
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.originalPrice.setText(ViewUtil.formatPrice(item.getOriginalPrice()));
            ViewUtil.strikeText(holder.originalPrice);
        } else {
            holder.originalPrice.setVisibility(View.GONE);
        }

        if (item.freeDelivery) {
            holder.freeDeliveryImage.setVisibility(View.VISIBLE);
        } else {
            holder.freeDeliveryImage.setVisibility(View.GONE);
        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPosition = position;
                ViewUtil.startProductActivity(activity, item.getId());
            }
        });

        PostVMLite lastItem = getLastItem();
        if (lastItem != null) {
            ((View) holder.itemLayout.getParent()).setTag(lastItem.getOffset());
            //holder.itemLayout.setTag(item.getOffset());
        }

        // like
        if (item.isLiked()) {
            ViewUtil.selectLikeTipsStyle(holder.likeImage, holder.likeText, item.getNumLikes());
        } else {
            ViewUtil.unselectLikeTipsStyle(holder.likeImage, holder.likeText, item.getNumLikes());
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isLiked) {
                    unlike(item, holder);
                } else {
                    like(item, holder);
                }
            }
        });

        // layout
        if (FeedViewItemsLayout.TWO_COLUMNS.equals(feedViewItemsLayout)) {
            holder.titleLayout.setVisibility(View.VISIBLE);
            holder.price.setTextColor(activity.getResources().getColor(R.color.green));
            holder.price.setTypeface(null, Typeface.NORMAL);

            // font
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            holder.price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            holder.likeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            holder.originalPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        } else if (FeedViewItemsLayout.THREE_COLUMNS.equals(feedViewItemsLayout)) {
            holder.titleLayout.setVisibility(View.GONE);
            holder.price.setTextColor(activity.getResources().getColor(R.color.light_gray));
            holder.price.setTypeface(null, Typeface.BOLD);

            // font
            holder.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            holder.price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            holder.likeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            holder.originalPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }

        // admin
        if (AppController.isUserAdmin()) {
            holder.timeScoreText.setVisibility(View.VISIBLE);
            holder.timeScoreText.setText(ViewUtil.formatDouble(item.timeScore, DefaultValues.DEFAULT_DOUBLE_SCALE) + "");
            holder.adminLayout.setVisibility(View.VISIBLE);

            // theme

            initThemeSpinner(holder, item);

            holder.themeSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        userSelect = true;
                    }
                    return false;
                }
            });

            holder.themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!userSelect) {
                        return;
                    }

                    userSelect = false;

                    if (!AppController.isUserAdmin()) {
                        return;
                    }

                    if (holder.themeSpinner.getSelectedItem() != null) {
                        String value = holder.themeSpinner.getSelectedItem().toString();
                        long themeId = -1;
                        CategoryVM themeCategory = CategoryCache.getThemeCategoryWithName(value);
                        if (themeCategory != null) {
                            themeId = themeCategory.id;
                        }

                        final long setThemeId = themeId;
                        AppController.getApiService().setProductTheme(item.id, setThemeId, new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                item.themeId = setThemeId;
                                Toast.makeText(activity, activity.getString(R.string.admin_set_product_theme_success), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(activity, activity.getString(R.string.admin_set_product_theme_failed), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "setProductTheme: failure", error);
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            // trend

            initTrendSpinner(holder, item);

            holder.trendSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        userSelect = true;
                    }
                    return false;
                }
            });

            holder.trendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!userSelect) {
                        return;
                    }

                    userSelect = false;

                    if (!AppController.isUserAdmin()) {
                        return;
                    }

                    if (holder.trendSpinner.getSelectedItem() != null) {
                        String value = holder.trendSpinner.getSelectedItem().toString();

                        long trendId = -1;
                        CategoryVM trendCategory = CategoryCache.getTrendCategoryWithName(value);
                        if (trendCategory != null) {
                            trendId = trendCategory.id;
                        }

                        final long setTrendId = trendId;
                        AppController.getApiService().setProductTrend(item.id, setTrendId, new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                item.trendId = setTrendId;
                                Toast.makeText(activity, activity.getString(R.string.admin_set_product_trend_success), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(activity, activity.getString(R.string.admin_set_product_trend_failed), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "setProductTrend: failure", error);
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            holder.timeScoreText.setVisibility(View.GONE);
            holder.adminLayout.setVisibility(View.GONE);
        }
    }

    private void initThemeSpinner(FeedViewHolder holder, PostVMLite post) {
        List<String> items = new ArrayList<>();
        items.add(activity.getString(R.string.spinner_select));
        for (CategoryVM theme : CategoryCache.getThemeCategories()) {
            items.add(theme.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,
                R.layout.spinner_item_admin,
                items);
        holder.themeSpinner.setAdapter(adapter);

        // select
        CategoryVM theme = CategoryCache.getCategory(post.themeId);
        if (theme != null) {
            int pos = ((ArrayAdapter)holder.themeSpinner.getAdapter()).getPosition(theme.name);
            holder.themeSpinner.setSelection(pos);
        }
    }

    private void initTrendSpinner(FeedViewHolder holder, PostVMLite post) {
        List<String> items = new ArrayList<>();
        items.add(activity.getString(R.string.spinner_select));
        for (CategoryVM theme : CategoryCache.getTrendCategories()) {
            items.add(theme.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,
                R.layout.spinner_item_admin,
                items);
        holder.trendSpinner.setAdapter(adapter);

        // select
        CategoryVM trend = CategoryCache.getCategory(post.trendId);
        if (trend != null) {
            int pos = ((ArrayAdapter)holder.trendSpinner.getAdapter()).getPosition(trend.name);
            holder.trendSpinner.setSelection(pos);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    private void loadImage(final Long imageId, final ImageView imageView) {
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(0, 0, 0, 0);
        ImageUtil.displayPostImage(imageId, imageView);

        /*
        imageView.setImageResource(ImageUtil.getImageLoadingResId(imageId));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ImageUtil.displayPostImage(imageId, imageView);
            }
        }, 0);
        */
    }

    private void like(final PostVMLite post, final FeedViewHolder holder) {
        if (pending) {
            return;
        }

        post.isLiked = true;
        post.numLikes++;
        ViewUtil.selectLikeTipsStyle(holder.likeImage, holder.likeText, post.getNumLikes());

        pending = true;
        AppController.getApiService().likePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                UserInfoCache.incrementNumLikes();
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "like: failure", error);
                pending = false;
            }
        });
    }

    private void unlike(final PostVMLite post, final FeedViewHolder holder) {
        if (pending) {
            return;
        }

        if (post.numLikes <= 0) {
            return;
        }

        post.isLiked = false;
        post.numLikes--;
        ViewUtil.unselectLikeTipsStyle(holder.likeImage, holder.likeText, post.getNumLikes());

        pending = true;
        AppController.getApiService().unlikePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                UserInfoCache.decrementNumLikes();
                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "unlike: failure", error);
                pending = false;
            }
        });
    }

    /**
     * View item.
     */
    class FeedViewHolder extends RecyclerView.ViewHolder {
        CardView itemCard;
        LinearLayout itemLayout;
        LinearLayout titleLayout;
        ImageView image;
        ImageView sellerImage;
        ImageView freeDeliveryImage;
        ImageView soldImage;
        ImageView countryImage;
        TextView timeScoreText;
        TextView title;
        TextView price;
        TextView originalPrice;
        LinearLayout likeLayout;
        ImageView likeImage;
        TextView likeText;
        LinearLayout adminLayout;
        Spinner themeSpinner;
        Spinner trendSpinner;

        public FeedViewHolder(View holder) {
            super(holder);

            itemCard = (CardView) holder.findViewById(R.id.itemCard);
            itemLayout = (LinearLayout) holder.findViewById(R.id.itemLayout);
            titleLayout = (LinearLayout) holder.findViewById(R.id.titleLayout);
            image = (ImageView) holder.findViewById(R.id.image);
            sellerImage = (ImageView) holder.findViewById(R.id.sellerImage);
            freeDeliveryImage = (ImageView) holder.findViewById(R.id.freeDeliveryImage);
            soldImage = (ImageView) holder.findViewById(R.id.soldImage);
            countryImage = (ImageView) holder.findViewById(R.id.countryImage);
            timeScoreText = (TextView) holder.findViewById(R.id.timeScoreText);
            title = (TextView) holder.findViewById(R.id.title);
            price = (TextView) holder.findViewById(R.id.price);
            originalPrice = (TextView) holder.findViewById(R.id.originalPrice);
            likeLayout = (LinearLayout) holder.findViewById(R.id.likeLayout);
            likeImage = (ImageView) holder.findViewById(R.id.likeImage);
            likeText = (TextView) holder.findViewById(R.id.likeText);
            adminLayout = (LinearLayout) holder.findViewById(R.id.adminLayout);
            themeSpinner = (Spinner) holder.findViewById(R.id.themeSpinner);
            trendSpinner = (Spinner) holder.findViewById(R.id.trendSpinner);
        }

        void setTag(long score) {
            itemLayout.setTag(score);
        }
    }
}