package com.beautypop.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.beautypop.R;
import com.beautypop.activity.AdminActivity;
import com.beautypop.activity.AdminCommentsActivity;
import com.beautypop.activity.AdminConversationListActivity;
import com.beautypop.activity.AdminLatestLoginsActivity;
import com.beautypop.activity.AdminMessageListActivity;
import com.beautypop.activity.AdminNewProductsActivity;
import com.beautypop.activity.AdminNewUsersActivity;
import com.beautypop.activity.CategoryActivity;
import com.beautypop.activity.ConversationListActivity;
import com.beautypop.activity.EditPostActivity;
import com.beautypop.activity.EditProfileActivity;
import com.beautypop.activity.FollowersActivity;
import com.beautypop.activity.FollowingsActivity;
import com.beautypop.activity.ForgetPasswordActivity;
import com.beautypop.activity.FullscreenImageActivity;
import com.beautypop.activity.GameBadgesActivity;
import com.beautypop.activity.LoginActivity;
import com.beautypop.activity.MainActivity;
import com.beautypop.activity.MessageListActivity;
import com.beautypop.activity.NewPostActivity;
import com.beautypop.activity.NotificationSettingsActivity;
import com.beautypop.activity.ProductActivity;
import com.beautypop.activity.CommentsActivity;
import com.beautypop.activity.ProductConversationListActivity;
import com.beautypop.activity.SearchActivity;
import com.beautypop.activity.SearchResultActivity;
import com.beautypop.activity.SelectImageActivity;
import com.beautypop.activity.SettingsActivity;
import com.beautypop.activity.SignupActivity;
import com.beautypop.activity.SignupDetailActivity;
import com.beautypop.activity.SplashActivity;
import com.beautypop.activity.TourActivity;
import com.beautypop.activity.UserProfileActivity;
import com.beautypop.activity.UserReviewsActivity;
import com.beautypop.activity.WelcomeActivity;
import com.beautypop.app.AppController;
import com.beautypop.viewmodel.AdminConversationVM;
import com.beautypop.viewmodel.CommentVM;
import com.beautypop.viewmodel.ConversationVM;
import com.beautypop.viewmodel.FeaturedItemVM;
import com.beautypop.viewmodel.PostVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.RetrofitError;

/**
 * Created by keithlei on 3/16/15.
 */
public class ViewUtil {
    private static final String TAG = ViewUtil.class.getName();

    public static final int PAGER_INDICATOR_DOT_DIMENSION = 10;

    public static final String BUNDLE_KEY_LOGIN_KEY = "loginKey";
    public static final String BUNDLE_KEY_ID = "id";
    public static final String BUNDLE_KEY_NAME = "name";
    public static final String BUNDLE_KEY_CATEGORY_ID = "catId";
    public static final String BUNDLE_KEY_SOURCE = "source";
    public static final String BUNDLE_KEY_FEED_TYPE = "feedType";
    public static final String BUNDLE_KEY_FEED_FILTER_CONDITION_TYPE = "feedFilterConditionType";
    public static final String BUNDLE_KEY_FEED_OBJECT_ID = "feedObjectId";
    public static final String BUNDLE_KEY_ACTION_TYPE = "actionType";
    public static final String BUNDLE_KEY_LISTS = "lists";
    public static final String BUNDLE_KEY_IMAGE_SOURCE = "imageSource";
    public static final String BUNDLE_KEY_INDEX = "index";
    public static final String BUNDLE_KEY_OBJECT = "object";

    public static final String BUNDLE_KEY_ARG1 = "arg1";
    public static final String BUNDLE_KEY_ARG2 = "arg2";
    public static final String BUNDLE_KEY_ARG3 = "arg3";

    public static final String INTENT_RESULT_REFRESH = "refresh";
    public static final String INTENT_RESULT_ID = "id";
    public static final String INTENT_RESULT_ITEM_CHANGED_STATE = "itemChangedState";
    public static final String INTENT_RESULT_OBJECT = "object";
    public static final String INTENT_RESULT_INDEX = "index";

    public static final String JSON_KEY_MESSAGE_KEY = "messages";

    public static final String GCM_LAUNCH_TARGET = "gcmLaunchTarget";

    public static final int START_ACTIVITY_REQUEST_CODE = 100;

    public static final int SELECT_GALLERY_IMAGE_REQUEST_CODE = 200;
    public static final int SELECT_CAMERA_IMAGE_REQUEST_CODE = 201;

    public static final int CROP_IMAGE_REQUEST_CODE = 202;
    public static final int PROCESS_IMAGE_REQUEST_CODE = 203;
	public static final int ADJUST_IMAGE_REQUEST_CODE = 204;

    public static final String HTML_LINE_BREAK = "<br>";

    public static final String[] LANG_OPTIONS = new String[]{
            AppController.getInstance().getString(R.string.lang_en),
            AppController.getInstance().getString(R.string.lang_zh)
    };

    private static Rect displayDimensions = null;

    public enum FeedItemPosition {
        UNKNOWN,
        HEADER,
        LEFT_COLUMN,
        MIDDLE_COLUMN,
        RIGHT_COLUMN
    }

    public enum PostConditionType {
        NEW_WITH_TAG,
        NEW_WITHOUT_TAG,
        USED
    }

    public enum ConversationOrderTransactionState {
        NA,
        ORDERED,
        CANCELLED,
        PAID,
        DELIVERED,
        SPECIAL_REQUEST,
        MISSING_DETAILS
    }

    public enum ReportedType {
        // Product
        POST_WRONG_CATEGORY,
        POST_COUNTERFEIT,
        POST_PROHIBITED_ITEM,
        POST_REPEATED_LISTING,
        POST_SPAM,
        // Seller
        USER_DID_NOT_SELL,
        USER_DID_NOT_SHIP_PRODUCT,
        USER_SHIP_WRONG_PRODUCT,
        USER_LEAVE_INACCURATE_REVIEW,
        USER_COUNTERFEIT,
        USER_PROHIBITED_ITEM,
        USER_REPEATED_LISTING,
        USER_SPAM,
        // Other
        OTHER
    }

    public enum HighlightColor {
        NONE,
        YELLOW,
        PINK,
        PURPLE,
        BLUE,
        RED
    }

    public static String[] SELLER_MAIN_TITLES;

    public static String[] SEARCH_MAIN_TITLES;

	public static String[] REVIEW_MAIN_TITLES;

    private static Map<PostConditionType, String> postConditionTypeMap = new HashMap<>();

    private static Map<ConversationOrderTransactionState, String> conversationOrderTransactionStateMap = new HashMap<>();

    private static Map<HighlightColor, Integer> colorMap = new HashMap<>();

    static {
        initCachedLocaleStrings();
    }

    private ViewUtil() {}

    //
    // Helper
    //

    public static void initCachedLocaleStrings() {
        SELLER_MAIN_TITLES = new String[] {
                AppController.getInstance().getString(R.string.main_tab_following),
                AppController.getInstance().getString(R.string.main_tab_seller)

        };

        SEARCH_MAIN_TITLES = new String[] {
				AppController.getInstance().getString(R.string.search_tab_products),
				AppController.getInstance().getString(R.string.search_tab_users)
		};

		REVIEW_MAIN_TITLES = new String[] {
				AppController.getInstance().getString(R.string.title_sold),
				AppController.getInstance().getString(R.string.title_purchased)
		};


		postConditionTypeMap.clear();
        postConditionTypeMap.put(PostConditionType.NEW_WITH_TAG, AppController.getInstance().getString(R.string.new_with_tag));
        postConditionTypeMap.put(PostConditionType.NEW_WITHOUT_TAG, AppController.getInstance().getString(R.string.new_without_tag));
        postConditionTypeMap.put(PostConditionType.USED, AppController.getInstance().getString(R.string.used));

        conversationOrderTransactionStateMap.clear();
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.NA, "---");
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.ORDERED, ConversationOrderTransactionState.ORDERED.name());
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.CANCELLED, ConversationOrderTransactionState.CANCELLED.name());
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.PAID, ConversationOrderTransactionState.PAID.name());
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.DELIVERED, ConversationOrderTransactionState.DELIVERED.name());
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.SPECIAL_REQUEST, ConversationOrderTransactionState.SPECIAL_REQUEST.name());
        conversationOrderTransactionStateMap.put(ConversationOrderTransactionState.MISSING_DETAILS, ConversationOrderTransactionState.MISSING_DETAILS.name());

        colorMap.clear();
        colorMap.put(HighlightColor.NONE, AppController.getInstance().getResources().getColor(R.color.white));
        colorMap.put(HighlightColor.PINK, AppController.getInstance().getResources().getColor(R.color.pink));
        colorMap.put(HighlightColor.YELLOW, AppController.getInstance().getResources().getColor(R.color.light_yellow));
        colorMap.put(HighlightColor.PURPLE, AppController.getInstance().getResources().getColor(R.color.light_purple));
        colorMap.put(HighlightColor.BLUE, AppController.getInstance().getResources().getColor(R.color.pale_blue));
        colorMap.put(HighlightColor.RED, AppController.getInstance().getResources().getColor(R.color.red));
    }

    public static int random(int low, int high) {
        return low + (int)(Math.random() * (high - low));
    }

    public static String shortenString(String str, int length) {
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0,length).concat("...");
    }

    // PostConditionType

    public static List<String> getPostConditionTypeValues() {
        List<String> conditionTypes = new ArrayList<>();
        for (PostConditionType conditionType : PostConditionType.values()) {
            if (postConditionTypeMap.get(conditionType) != null) {
                conditionTypes.add(postConditionTypeMap.get(conditionType));
            }
        }
        return conditionTypes;
    }

    public static String getPostConditionTypeValue(PostConditionType conditionType) {
        if (conditionType != null) {
            return postConditionTypeMap.get(conditionType);
        }
        return null;
    }

    public static PostConditionType parsePostConditionTypeFromValue(String value) {
        for (Map.Entry<PostConditionType, String> entrySet : postConditionTypeMap.entrySet()) {
            if (entrySet.getValue().equals(value)) {
                return entrySet.getKey();
            }
        }
        return null;
    }

    public static PostConditionType parsePostConditionType(String conditionType) {
        try {
            return Enum.valueOf(PostConditionType.class, conditionType);
        } catch (Exception e) {
            return null;
        }
    }

    // ConversationOrderTransactionState

    public static List<String> getConversationOrderTransactionStateValues() {
        List<String> states = new ArrayList<>();
        for (ConversationOrderTransactionState state : ConversationOrderTransactionState.values()) {
            if (conversationOrderTransactionStateMap.get(state) != null) {
                states.add(conversationOrderTransactionStateMap.get(state));
            }
        }
        return states;
    }

    public static String getConversationOrderTransactionStateValue(ConversationOrderTransactionState state) {
        if (state != null) {
            return conversationOrderTransactionStateMap.get(state);
        }
        return null;
    }

    public static ConversationOrderTransactionState parseConversationOrderTransactionStateFromValue(String value) {
        for (Map.Entry<ConversationOrderTransactionState, String> entrySet : conversationOrderTransactionStateMap.entrySet()) {
            if (entrySet.getValue().equals(value)) {
                return entrySet.getKey();
            }
        }
        return null;
    }

    public static ConversationOrderTransactionState parseConversationOrderTransactionState(String state) {
        try {
            return Enum.valueOf(ConversationOrderTransactionState.class, state);
        } catch (Exception e) {
            return null;
        }
    }

    // HighlightColor

    public static List<Integer> getHighlightColorValues() {
        List<Integer> colors = new ArrayList<>();
        for (HighlightColor color : HighlightColor.values()) {
            if (colorMap.get(color) != null) {
                colors.add(colorMap.get(color));
            }
        }
        return colors;
    }

    public static Integer getHighlightColorValue(HighlightColor color) {
        if (color != null) {
            return colorMap.get(color);
        }
        return null;
    }

    public static HighlightColor parseHighlightColorFromValue(int value) {
        for (Map.Entry<HighlightColor, Integer> entrySet : colorMap.entrySet()) {
            if (entrySet.getValue().equals(value)) {
                return entrySet.getKey();
            }
        }
        return null;
    }

    public static HighlightColor parseHighlightColor(String color) {
        try {
            return Enum.valueOf(HighlightColor.class, color);
        } catch (Exception e) {
            return null;
        }
    }

    //
    // View
    //

    public static String urlAppendSessionId(String url) {
        return url + "?key="+ UrlUtil.encode(AppController.getInstance().getSessionId());
    }

    public static Locale getAppLocale() {
        Locale locale = AppController.getInstance().getResources().getConfiguration().locale;
        //Log.d(TAG, "getAppLocale: locale="+locale.getLanguage());
        return locale;
    }

    public static void setLocale(Activity activity) {
        setLocale(activity, SharedPreferencesUtil.getInstance().getLang());
    }

    /**
     * zh or en
     * @param activity
     * @param lang
     */
    public static void setLocale(Activity activity, String lang) {
        if (StringUtils.isEmpty(lang)) {
            lang = DefaultValues.DEFAULT_LANG;
        }

        // set already
        if (getAppLocale().getLanguage().equalsIgnoreCase(SharedPreferencesUtil.getInstance().getLang())) {
            return;
        }

        Locale locale = new Locale(lang);
        Log.d(TAG, "lang="+lang+" locale="+locale.getDisplayName());

        Resources resource = activity.getResources();
        DisplayMetrics dm = resource.getDisplayMetrics();
        Configuration conf = resource.getConfiguration();
        conf.locale = locale;
        resource.updateConfiguration(conf, dm);
        //SharedPreferencesUtil.getInstance().saveLang(lang);

        // cached locale strings
        initCachedLocaleStrings();
    }

    public static void addDots(Activity activity, final int numPages, LinearLayout dotsLayout, final List<ImageView> dots, ViewPager viewPager) {
        if (dotsLayout == null) {
            return;
        }

        Log.d(TAG, "addDots: numPages="+numPages);

        dotsLayout.removeAllViews();

        int imageResource = R.drawable.ic_dot_sel;      // select the first dot by default
        for (int i = 0; i < numPages; i++) {
            ImageView dot = new ImageView(activity);
            dot.setImageDrawable(AppController.getInstance().getResources().getDrawable(imageResource));
            //dot.setAlpha(80F);

            int dimension = getRealDimension(PAGER_INDICATOR_DOT_DIMENSION);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimension, dimension);
            params.gravity = Gravity.CENTER_VERTICAL;
            dotsLayout.addView(dot, params);

            dots.add(dot);
            imageResource = R.drawable.ic_dot;
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position, numPages, dots);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        dotsLayout.setVisibility(numPages > 1 ? View.VISIBLE : View.GONE);
    }

    public static void selectDot(int index, int numPages, List<ImageView> dots) {
        if (dots.size() == 0) {
            return;
        }

        Resources res = AppController.getInstance().getResources();
        for (int i = 0; i < numPages; i++) {
            int drawableId = (i == index)? R.drawable.ic_dot_sel : R.drawable.ic_dot;
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    public static void showSpinner(Activity activity) {
        if (activity != null) {
            ProgressBar spinner = (ProgressBar) activity.findViewById(R.id.spinner);
            if (spinner != null) {
                AnimationUtil.show(spinner);
            }
        }
    }

    public static void stopSpinner(Activity activity) {
        if (activity != null) {
            ProgressBar spinner = (ProgressBar) activity.findViewById(R.id.spinner);
            if (spinner != null) {
                AnimationUtil.cancel(spinner);
            }
        }
    }

    public static int getRealDimension(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                AppController.getInstance().getResources().getDisplayMetrics());
    }

    public static Rect getDisplayDimensions(Activity activity) {
        if (displayDimensions == null) {
            int padding = 0;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels - padding;
            int height = displaymetrics.heightPixels - padding;
            displayDimensions = new Rect(0, 0, width, height);
        }
        return displayDimensions;
    }

    public static void setHeightBasedOnChildren(Activity activity, ListView listView) {
        BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
        if (adapter == null) {
            // pre-condition
            return;
        }

        // http://stackoverflow.com/questions/19908003/getting-height-of-text-view-before-rendering-to-layout
        int widthSpec = View.MeasureSpec.makeMeasureSpec(getDisplayDimensions(activity).width(), View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(widthSpec, heightSpec);
            totalHeight += item.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void scrollTop(final ScrollView scrollView) {
        scrollTop(scrollView, true);
    }

    public static void scrollTop(final ScrollView scrollView, boolean delay) {
        if (delay) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_UP);
                }
            }, DefaultValues.DEFAULT_HANDLER_DELAY);
        } else {
            scrollView.fullScroll(View.FOCUS_UP);
        }
    }

    public static List<CommentVM> getLatestComments(List<CommentVM> comments, int n) {
        int start = Math.max(0, comments.size() - n);
        return comments.subList(start, comments.size());
    }

    public static void setClickableText(TextView textView, String clickableText, String text, View.OnClickListener listener) {
        setClickableText(textView, clickableText, text, true, listener);
    }

    public static void setClickableText(TextView textView, String clickableText, String text, boolean prepend, View.OnClickListener listener) {
        if (!StringUtils.isEmpty(clickableText)) {
            SpannableString link = setLinkSpan(clickableText, listener);
            if (prepend) {
                textView.setText(link);
                textView.append(" " + text);
            } else {
                textView.setText(text + " ");
                textView.append(link);
            }
        } else {
            textView.setText(text);
        }

        setLinksClickable(textView);
    }

    public static SpannableString setLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        return link;
    }

    public static void setLinksClickable(TextView textView) {
        textView.setTextIsSelectable(true);
        textView.setFocusable(true);
        textView.setLinksClickable(true);
        textView.setLinkTextColor(textView.getResources().getColor(R.color.link));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        MovementMethod m = textView.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (textView.getLinksClickable()) {
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    public static void setConversationImageTag(Activity activity, ConversationVM conversation) {
        TextView buyText = (TextView) activity.findViewById(R.id.buyText);
        TextView sellText = (TextView) activity.findViewById(R.id.sellText);
        TextView soldText = (TextView) activity.findViewById(R.id.soldText);
        setConversationImageTag(buyText, sellText, soldText, conversation);
    }

    public static void setConversationImageTag(View view, ConversationVM conversation) {
        TextView buyText = (TextView) view.findViewById(R.id.buyText);
        TextView sellText = (TextView) view.findViewById(R.id.sellText);
        TextView soldText = (TextView) view.findViewById(R.id.soldText);
        setConversationImageTag(buyText, sellText, soldText, conversation);
    }

    private static void setConversationImageTag(TextView buyText, TextView sellText, TextView soldText, ConversationVM conversation) {
        buyText.setVisibility(View.GONE);
        sellText.setVisibility(View.GONE);
        soldText.setVisibility(View.GONE);

        if (conversation.postSold) {
            soldText.setVisibility(View.VISIBLE);
        } else if (conversation.postOwner){
            sellText.setVisibility(View.VISIBLE);
        } else {
            buyText.setVisibility(View.VISIBLE);
        }
    }

    public static void selectLikeButtonStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_liked);
        text.setText(likes+"");
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.pink));
    }

    public static void unselectLikeButtonStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_like);
        text.setText(AppController.getInstance().getString(R.string.like));
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.dark_gray));
    }

    public static void selectLikeTipsStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_liked_tips);
        text.setText(likes+"");
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
    }

    public static void unselectLikeTipsStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_like_tips);
        text.setText(likes + "");
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
    }

    public static void selectFollowButtonStyleLite(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
        button.setBackgroundResource(R.drawable.button_gray_border);
        button.setText(R.string.following);
    }

    public static void unselectFollowButtonStyleLite(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.pink));
        button.setBackgroundResource(R.drawable.button_pink_border);
        button.setText(R.string.follow);
    }

    public static void selectFollowButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.button_following);
        button.setText(R.string.following);
    }

    public static void unselectFollowButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.button_follow);
        button.setText(R.string.follow);
    }

    public static void selectFilterButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.button_filter);
    }

    public static void unselectFilterButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.dark_gray));
        button.setBackgroundResource(R.drawable.button_filter_unselect);
    }

    public static void selectProfileFeedButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.pink));
        button.setBackgroundResource(R.drawable.button_profile_feed);
    }

    public static void unselectProfileFeedButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
        button.setBackgroundResource(R.drawable.button_profile_feed_unselect);
    }

    public static FeedItemPosition getFeedItemPosition(View feedItemView, int numColumns, boolean hasHeader) {
        int pos = ((RecyclerView.LayoutParams) feedItemView.getLayoutParams()).getViewLayoutPosition();
        if (hasHeader) {
            if (pos == 0) {
                return FeedItemPosition.HEADER;
            }
            // real position
            pos = pos - 1;
        }

        //Log.d(TAG, "getFeedItemPosition: pos="+pos);

        pos = pos % numColumns;
        if (pos == 0) {
            return FeedItemPosition.LEFT_COLUMN;
        } else if (pos == numColumns-1) {
            return FeedItemPosition.RIGHT_COLUMN;
        }
        return FeedItemPosition.MIDDLE_COLUMN;
    }

    public static void showTips(final SharedPreferencesUtil.Screen screen, final ViewGroup tipsLayout, final View dismissTipsButton) {
        if (SharedPreferencesUtil.getInstance().isScreenViewed(screen)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);
            dismissTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(screen);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    public static void fullscreenImagePopup(Activity activity, String url) {
        startFullscreenImageActivity(activity, url);
    }

    //
    // HTML
    //

    public static void setHtmlText(String text, TextView textView, Activity activity) {
        setHtmlText(text, textView, activity, false);
    }

    public static void setHtmlText(String text, TextView textView, Activity activity, boolean longClickSelectAll) {
        setHtmlText(text, textView, activity, longClickSelectAll, false);
    }

    public static void setHtmlText(String text, TextView textView, Activity activity, boolean longClickSelectAll, boolean linkMovement) {
        if (StringUtils.isEmpty(text)) {
            text = "";
        }

        PostImageGetter imageGetter = new PostImageGetter(activity);

        text = text.replace("\n", HTML_LINE_BREAK);

        imageGetter.setTextView(textView);
        Spannable spannedText = (Spannable) Html.fromHtml(text, imageGetter, null);

        if (linkMovement) {
            setLinksClickable(textView);
            //spannedText = stripTextUnderlines(spannedText);
        }

        if (longClickSelectAll) {
            setLongClickSelectAll(textView);
        }

        textView.setText(spannedText);
    }

    private static Spannable stripTextUnderlines(Spannable spannedText) {
        URLSpan[] spans = spannedText.getSpans(0, spannedText.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = spannedText.getSpanStart(span);
            int end = spannedText.getSpanEnd(span);
            spannedText.removeSpan(span);
            span = new URLSpanString(span.getURL());
            spannedText.setSpan(span, start, end, 0);
        }
        return spannedText;
    }

    //
    // Text
    //

    public static boolean copyToClipboard(String text) {
        return ClipboardUtil.copyToClipboard(AppController.getInstance(), text);
    }

    public static boolean copyToClipboard(TextView textView) {
        return ClipboardUtil.copyToClipboard(AppController.getInstance(), textView.getText().toString());
    }

    public static void readFromClipboardTo(TextView textView) {
        String text = ClipboardUtil.readFromClipboard(AppController.getInstance());
        if (!StringUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    public static void setLongClickSelectAll(TextView textView) {
        textView.setTextIsSelectable(true);
        textView.setFocusable(true);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    //textView.setSelectAllOnFocus(true);
                    //textView.setSelected(true);
                    Selection.selectAll((Spannable) textView.getText());
                    textView.setCursorVisible(true);
                    textView.requestFocus();
                }
                return false;
            }
        });
    }

    public static EditText createPriceEditText(Activity activity, String priceValue) {
        final EditText editText = new EditText(activity);
        //editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setSingleLine(true);
        editText.setTextSize(18);
        editText.setText(priceValue);
        editText.setHint(priceValue);
        editText.setSelection(priceValue.length());
        return editText;
    }

    public static EditText initOfferPriceAlertLayout(Activity activity, long price, AlertDialog.Builder alertDialogBuilder) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.offer_price_popup_window, (ViewGroup) activity.findViewById(R.id.popupElement));
        final EditText priceEdit = (EditText) layout.findViewById(R.id.priceEdit);
        priceEdit.setText(price + "");
        priceEdit.setHint(price + "");
        priceEdit.setSelection(priceEdit.length());
        alertDialogBuilder.setView(layout);
        return priceEdit;
    }

    public static long validateAndGetPriceFromInput(Activity activity, TextView textView) {
        String value = textView.getText().toString().trim();
        if (StringUtils.isEmpty(value)) {
            Toast.makeText(activity, activity.getString(R.string.invalid_post_price_empty), Toast.LENGTH_SHORT).show();
            return -1L;
        }

        Long price = -1L;
        try {
            price = Long.valueOf(value);
            if (price < 0) {
                Toast.makeText(activity, activity.getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                return -1L;
            }
            return price;
        } catch (NumberFormatException e) {
            Toast.makeText(activity, activity.getString(R.string.invalid_post_price_not_number), Toast.LENGTH_SHORT).show();
            return -1L;
        }
    }

    public static void strikeText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static double formatDouble(double value, int scale) {
        return new BigDecimal(value).setScale(scale, RoundingMode.UP).doubleValue();
    }

    public static String formatScore(double value, double scoreAdjust, int scale) {
        String score = new BigDecimal(value).setScale(scale, RoundingMode.UP).doubleValue() + "";
        if (scoreAdjust > 0) {
            score = "*" + score;
        }
        return score;
    }

    public static String formatPrice(double value) {
        //return String.format("$%.2f", value);
        return String.format("$%.0f", value);
    }

    public static String formatSellerLastActive(Long value) {
        if (DefaultValues.LANG_EN.equalsIgnoreCase(getAppLocale().getLanguage())) {
            return AppController.getInstance().getString(R.string.last_active) + " " + DateTimeUtil.getTimeAgo(value);
        }
        return DateTimeUtil.getTimeAgo(value) + AppController.getInstance().getString(R.string.last_active);
    }

    public static String formatUserProducts(Long value) {
        return AppController.getInstance().getString(R.string.products) + ": " + value;
    }

    public static String formatUserFollowers(Long value) {
        return AppController.getInstance().getString(R.string.user_followers) + ": " + value;
    }

    public static String formatFollowers(Long value) {
        return AppController.getInstance().getString(R.string.followers) + " " + value;
    }

    public static String formatFollowings(Long value) {
        return AppController.getInstance().getString(R.string.followings) + " " + value;
    }

    public static String formatProductsTab(Long value) {
        return AppController.getInstance().getString(R.string.products) + "\n" + value;
    }

    public static String formatLikesTab(Long value) {
        return AppController.getInstance().getString(R.string.likes) + "\n" + value;
    }

    //
    // Popup soft keyboard
    //

    public static void popupInputMethodWindow(final Activity activity, final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
                //imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }

    public static void hideInputMethodWindow(final Activity activity, final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 100);
    }

    //
    // Alert dialog
    //

    public static void alert(Context context, String message) {
        alert(context, null, message);
    }

    public static void alert(Context context, String title, String message) {
        alert(context, title, message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public static void alert(Context context, String title, String message, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, onClick);
        if (!StringUtils.isEmpty(title)) {
            alertBuilder.setTitle(title);
        }
        alertBuilder.show();
    }

    public static Dialog alert(Context context, int dialogResourceId) {
        return alert(context, dialogResourceId, -1, null);
    }

    public static Dialog alert(Context context, int dialogResourceId, int buttonResourceId, final View.OnClickListener onClick) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(dialogResourceId, null);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogView);
        //dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.yes), onClick);
        if (buttonResourceId != -1) {
            Button button = (Button) dialogView.findViewById(buttonResourceId);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (onClick != null) {
                        onClick.onClick(view);
                    }
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
        dialog.show();
        return dialog;
    }

    public static Dialog alertGameStatus(Context context, String desc) {
        return alertGameStatus(context, null, desc, -1, 3000);
    }

    public static Dialog alertGameStatus(Context context, String image, String desc) {
        return alertGameStatus(context, image, desc, -1, 3000);
    }

    public static Dialog alertGameStatus(Context context, String desc, int points) {
        return alertGameStatus(context, null, desc, points, 3000);
    }

    public static Dialog alertGameStatus(Context context, String image, String desc, int points, long delayMillis) {
        final Dialog dialog = alert(context, R.layout.game_popup_window);
        ImageView mascot = (ImageView) dialog.findViewById(R.id.mascot);
        TextView descText = (TextView) dialog.findViewById(R.id.descText);
        TextView pointsText = (TextView) dialog.findViewById(R.id.pointsText);

        descText.setText(desc);
        if (points == -1) {
            mascot.setVisibility(View.GONE);
            pointsText.setVisibility(View.GONE);
        } else {
            pointsText.setText("+"+points);
        }

        if (image != null) {
            mascot.setVisibility(View.VISIBLE);
            ImageUtil.displayImage(image, mascot);
        }

        ImageView dismissImage = (ImageView) dialog.findViewById(R.id.dismissImage);
        dismissImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        // auto dismiss
        if (delayMillis != -1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, delayMillis);
        }
        return dialog;
    }

    //
    // Retrofit
    //

    public static String getResponseBody(retrofit.client.Response response) {
        if (response == null || response.getBody() == null) {
            return "";
        }

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static int getErrorStatusCode(RetrofitError error) {
        if (error.isNetworkError()) {
            return 550; // Use another code if you'd prefer
        }

        return error.getResponse().getStatus();
    }

    //
    // Post
    //

    public static boolean isNewPost(PostVM post) {
        return post.getNumComments() <= DefaultValues.NEW_POST_NOC &&
                DateTimeUtil.getDaysAgo(post.getCreatedDate()) <= DefaultValues.NEW_POST_DAYS_AGO;
    }

    public static boolean isHotPost(PostVM post) {
        return post.getNumComments() > DefaultValues.HOT_POST_NOC ||
                post.getNumLikes() > DefaultValues.HOT_POST_NOL ||
                post.getNumViews() > DefaultValues.HOT_POST_NOV;
    }

    //
    // Start Activities
    //

    public static Long getIntentFilterLastPathSegment(Intent intent) {
        if (intent.getData() != null) {
            return Long.parseLong(intent.getData().getLastPathSegment());
        }
        return -1L;
    }

    public static void startWelcomeActivity(Activity activity) {
        Intent intent = new Intent(activity, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void startLoginActivity(Activity activity) {
        startLoginActivity(activity, "");
    }

    public static void startLoginActivity(Activity activity, String email) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra(BUNDLE_KEY_NAME, email);
        activity.startActivity(intent);
    }

    public static void startSignupActivity(Activity activity) {
        activity.startActivity(new Intent(activity, SignupActivity.class));
    }

    public static void startSignupDetailActivity(Activity activity, String username) {
        Intent intent = new Intent(activity, SignupDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_NAME, username);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startForgetPasswordActivity(Activity activity) {
        activity.startActivity(new Intent(activity, ForgetPasswordActivity.class));
    }

    public static void startSplashActivity(Activity activity, String key) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(BUNDLE_KEY_LOGIN_KEY, key);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startTourActivity(Activity activity) {
        Intent intent = new Intent(activity, TourActivity.class);
        activity.startActivity(intent);
    }

    public static void startMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void startNewPostActivity(Activity activity) {
        startNewPostActivity(activity, -1L);
    }

    public static void startNewPostActivity(Activity activity, Long catId) {
        Intent intent = new Intent(activity, NewPostActivity.class);
        intent.putExtra(BUNDLE_KEY_CATEGORY_ID, catId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    public static void startEditPostActivity(Activity activity, Long postId, Long catId) {
        Intent intent = new Intent(activity, EditPostActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, postId);
        intent.putExtra(BUNDLE_KEY_CATEGORY_ID, catId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    public static void startCategoryActivity(Activity activity, Long catId) {
        Intent intent = new Intent(activity, CategoryActivity.class);
        intent.putExtra(BUNDLE_KEY_CATEGORY_ID, catId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    public static void startProductActivity(Activity activity, Long postId) {
        Intent intent = new Intent(activity, ProductActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, postId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    public static void startFullscreenImageActivity(Activity activity, String source) {
        Intent intent = new Intent(activity, FullscreenImageActivity.class);
        intent.putExtra(BUNDLE_KEY_IMAGE_SOURCE, source);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startUserProfileActivity(Activity activity, Long userId) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, userId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startEditProfileActivity(Activity activity) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        activity.startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    public static void startUserReviewstActivity(Activity activity, Long userId, boolean buyerReviews) {
        Intent intent = new Intent(activity, UserReviewsActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, userId);
        intent.putExtra(BUNDLE_KEY_ARG1, buyerReviews);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startGameBadgesActivity(Activity activity, Long userId) {
        Intent intent = new Intent(activity, GameBadgesActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, userId);
        activity.startActivity(intent);
    }

    public static void startFollowersActivity(Activity activity, Long userId) {
        Intent intent = new Intent(activity, FollowersActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, userId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startFollowingsActivity(Activity activity, Long userId) {
        Intent intent = new Intent(activity, FollowingsActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, userId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startCommentsActivity(Activity activity, Long postId) {
        Intent intent = new Intent(activity, CommentsActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, postId);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void startConversationListActivity(Activity activity) {
        Intent intent = new Intent(activity, ConversationListActivity.class);
        activity.startActivity(intent);
    }

    public static void startProductConversationListActivity(Activity activity, Long postId) {
        Intent intent = new Intent(activity, ProductConversationListActivity.class);
        intent.putExtra(BUNDLE_KEY_ID, postId);
        activity.startActivity(intent);
    }

    public static void startMessageListActivity(Activity activity, ConversationVM conversation) {
        startMessageListActivity(activity, conversation, false, -1L);
    }

    public static void startMessageListActivity(Activity activity, ConversationVM conversation, boolean buy, long offeredPrice) {
        Intent intent = new Intent(activity, MessageListActivity.class);
        intent.putExtra(BUNDLE_KEY_OBJECT, conversation);
        intent.putExtra(BUNDLE_KEY_ARG1, buy);
        intent.putExtra(BUNDLE_KEY_ARG2, offeredPrice);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    public static void startSearchActivity(Activity activity) {
        startSearchActivity(activity, null);
    }

    public static void startSearchActivity(Activity activity, Long catId) {
        Intent intent = new Intent(activity, SearchActivity.class);
        if (catId != null && catId > 0) {
            intent.putExtra(BUNDLE_KEY_CATEGORY_ID, catId);
        }
        activity.startActivity(intent);
    }

    public static void startSearchResultActivity(Activity activity, String actionType, String searchKey) {
        startSearchResultActivity(activity, actionType, searchKey, null);
    }
    public static void startSearchResultActivity(Activity activity, String actionType, String searchKey, Long catId) {
        Intent intent = new Intent(activity, SearchResultActivity.class);
        intent.putExtra(BUNDLE_KEY_ACTION_TYPE, actionType);
        intent.putExtra(ViewUtil.BUNDLE_KEY_NAME, searchKey);
        if (catId != null && catId > 0) {
            intent.putExtra(BUNDLE_KEY_CATEGORY_ID, catId);
        }
        activity.startActivity(intent);
    }

    public static void startSelectImageActivity(Activity activity, Uri imageUri) {
        Intent intent = new Intent(activity, SelectImageActivity.class);
        intent.putExtra(BUNDLE_KEY_IMAGE_SOURCE, 2);
        intent.setData(imageUri);
        activity.startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
        activity.overridePendingTransition(0, 0);
    }

    public static void startSettingsActivity(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    public static void startNotificationSettingsActivity(Activity activity) {
        Intent intent = new Intent(activity, NotificationSettingsActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminActivity(Activity activity) {
        Intent intent = new Intent(activity, AdminActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminNewUsersActivity(Activity activity) {
        Intent intent = new Intent(activity, AdminNewUsersActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminLatestLoginsActivity(Activity activity) {
        Intent intent = new Intent(activity, AdminLatestLoginsActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminCommentsActivity(Activity activity) {
        Intent intent = new Intent(activity, AdminCommentsActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminConversationListActivity(Activity activity) {
        Intent intent = new Intent(activity, AdminConversationListActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminNewProductsActivity(Activity activity) {
        Intent intent = new Intent(activity, AdminNewProductsActivity.class);
        activity.startActivity(intent);
    }

    public static void startAdminMessageListActivity(Activity activity, AdminConversationVM conversation) {
        Intent intent = new Intent(activity, AdminMessageListActivity.class);
        intent.putExtra(BUNDLE_KEY_OBJECT, conversation);
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.startActivity(intent);
    }

    public static void setActivityResult(Activity activity, Long id) {
        setActivityResult(activity, id, null);
    }

    public static void setActivityResult(Activity activity, Boolean refresh) {
        setActivityResult(activity, null, refresh);
    }

    public static void setActivityResult(Activity activity, Long id, Boolean refresh) {
        Intent intent = new Intent();
        if (id != null && id != -1L) {
            intent.putExtra(INTENT_RESULT_ID, id);
        }
        if (refresh != null) {
            intent.putExtra(INTENT_RESULT_REFRESH, refresh);
        }
        intent.putExtra(BUNDLE_KEY_SOURCE, activity.getClass().getSimpleName());
        activity.setResult(Activity.RESULT_OK, intent);
    }

    public static void handleFeaturedItemAction(Activity activity, FeaturedItemVM featuredItem) {
        Long destinationObjId = featuredItem.destinationObjId;
        String destinationObjName = featuredItem.destinationObjName;
        switch (featuredItem.destinationType) {
            case "CATEGORY":
                startCategoryActivity(activity, destinationObjId);
                break;
            case "POST":
                startProductActivity(activity, destinationObjId);
                break;
            case "USER":
                startUserProfileActivity(activity, destinationObjId);
                break;
            case "HASHTAG":
                // TODO start hashtag page
                break;
            case "URL":
                // TODO start url page
                break;
            case "NO_ACTION":
            default:
                break;
        }
    }

    public static void openPlayStoreForUpgrade(Activity activity) {
        final String appPackageName = AppController.getInstance().getPackageName();
        Log.d(TAG, "openPlayStoreForUpgrade() appPackageName="+appPackageName);
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static boolean isGcmLaunchTarget(Intent intent) {
        Bundle bundle = intent.getExtras();
        return bundle != null && bundle.getString(GCM_LAUNCH_TARGET) != null;
        //return bundle != null && Boolean.valueOf(bundle.getString(GCM_LAUNCH_TARGET));
        //return bundle != null && bundle.getBoolean(GCM_LAUNCH_TARGET);
        //return intent != null && intent.getBooleanExtra(GCM_LAUNCH_TARGET, false);
    }

    /**
     * Clickable string with onclick listener.
     */
    static class ClickableString extends ClickableSpan {
        private View.OnClickListener listener;
        private boolean drawUnderline;

        public ClickableString(View.OnClickListener listener) {
            this(listener, false);
        }

        public ClickableString(View.OnClickListener listener, boolean drawUnderline) {
            this.listener = listener;
            this.drawUnderline = drawUnderline;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(drawUnderline);
        }
    }

    /**
     *
     */
    static class URLSpanString extends URLSpan {
        private boolean drawUnderline;

        public URLSpanString(String url) {
            this(url, false);
        }

        public URLSpanString(String url, boolean drawUnderline) {
            super(url);
            this.drawUnderline = drawUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(drawUnderline);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<URLSpan> CREATOR = new Parcelable.Creator<URLSpan>() {
            @Override
            public URLSpan createFromParcel(Parcel in) {
                return new URLSpan(in);
            }

            @Override
            public URLSpan[] newArray(int size) {
                return new URLSpan[size];
            }
        };
    }
}
