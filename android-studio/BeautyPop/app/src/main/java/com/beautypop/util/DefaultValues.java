package com.beautypop.util;

/**
 * Should read from server.
 */
public class DefaultValues {

    // Config
    public static final boolean IMAGE_ADJUST_ENABLED = true;

    // Email signup need verification email
    public static final boolean EMAIL_SIGNUP_VERIFICATION_REQUIRED = false;

    // Http return code
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_BAD_REQUEST = 400;

    // Default feeds
    public static final FeedFilter.FeedType DEFAULT_CATEGORY_FEED_TYPE = FeedFilter.FeedType.CATEGORY_POPULAR;
    public static final FeedFilter.FeedType DEFAULT_USER_FEED_TYPE = FeedFilter.FeedType.USER_POSTED;
    public static final FeedFilter.ConditionType DEFAULT_FEED_FILTER_CONDITION_TYPE = FeedFilter.ConditionType.ALL;

    // From server
    public static final int DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD = 2;

    public static final int CONVERSATION_MESSAGE_COUNT = 20;

    // UI
    public static final String LANG_EN = "en";
    public static final String LANG_ZH = "zh";
    public static final String DEFAULT_LANG = LANG_EN;

    public static final int MIN_CHAR_SIGNUP_PASSWORD = 4;
    public static final int DEFAULT_SHORT_TITLE_COUNT = 12;
    public static final int DEFAULT_TITLE_COUNT = 20;

    public static final int SPLASH_DISPLAY_MILLIS = 500;
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 3000;
    public static final int DEFAULT_HANDLER_DELAY = 100;

    public static final int MAIN_TOOLBAR_MIN_HEIGHT = 12;
    public static final int DEFAULT_SLIDER_DURATION = 4000;

    public static final int PULL_TO_REFRESH_DELAY = 250;

    public static final int FEED_LAYOUT_2COL_TOP_MARGIN = 5;
    public static final int FEED_LAYOUT_2COL_BOTTOM_MARGIN = 0;
    public static final int FEED_LAYOUT_2COL_SIDE_MARGIN = 2;
    public static final int FEED_LAYOUT_2COL_LEFT_SIDE_MARGIN = FEED_LAYOUT_2COL_SIDE_MARGIN * 2;
    public static final int FEED_LAYOUT_2COL_RIGHT_SIDE_MARGIN = FEED_LAYOUT_2COL_SIDE_MARGIN * 2;

    public static final int FEED_LAYOUT_3COL_TOP_MARGIN = 3;
    public static final int FEED_LAYOUT_3COL_BOTTOM_MARGIN = 0;
    public static final int FEED_LAYOUT_3COL_SIDE_MARGIN = 0;
    public static final int FEED_LAYOUT_3COL_LEFT_SIDE_MARGIN = FEED_LAYOUT_3COL_SIDE_MARGIN;
    public static final int FEED_LAYOUT_3COL_RIGHT_SIDE_MARGIN = FEED_LAYOUT_3COL_SIDE_MARGIN;

    public static final int LISTVIEW_SLIDE_IN_ANIM_START = 10;
    public static final int LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR = 2;

    public static final int CATEGORY_PICKER_POPUP_WIDTH = 220;
    public static final int CATEGORY_PICKER_POPUP_HEIGHT = 380;

    public static final int MAX_POST_IMAGES = 4;
    public static final int MAX_MESSAGE_IMAGES = 1;
    public static final int MAX_POST_IMAGE_DIMENSION = 100;
    public static final int LATEST_COMMENTS_COUNT = 3;

    public static final int IMAGE_ROUNDED_RADIUS = 15;
    public static final int IMAGE_CIRCLE_RADIUS = 120;

    public static final int NEW_POST_DAYS_AGO = 3;
    public static final int NEW_POST_NOC = 3;
    public static final int HOT_POST_NOV = 200;
    public static final int HOT_POST_NOL = 5;
    public static final int HOT_POST_NOC = 5;

    public static final long POST_SCORE_POINTS_ADJUST = 100;
    public static final long POST_SCORE_POINTS_ADJUST2 = 500;

    public static final int DEFAULT_DOUBLE_SCALE = 3;
}
