package com.beautypop.util;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beautypop.R;
import com.beautypop.app.AppController;
import com.beautypop.viewmodel.CategoryVM;
import com.beautypop.viewmodel.PostVMLite;
import com.beautypop.viewmodel.UserVMLite;

/**
 * Created by keithlei on 3/16/15.
 */
public class UrlUtil {
    public static final String ENCODE_CHARSET_UTF8 = "UTF-8";

    private static final String SELLER_URL = AppController.BASE_URL + "/seller/%d";
    private static final String PRODUCT_URL = AppController.BASE_URL + "/product/%d";
    private static final String CATEGORY_URL = AppController.BASE_URL + "/category/%d";

    //private static final String APPS_DOWNLOAD_URL = AppController.BASE_URL + "/apps";
    private static final String APPS_DOWNLOAD_URL = "https://play.google.com/store/apps/details?id=com.beautypop.app";
    private static final String REFERRAL_URL = AppController.BASE_URL + "/signup-code/%s";

    private static String SELLER_URL_REGEX = ".*/seller/(\\d+)";
    private static String PRODUCT_URL_REGEX = ".*/product/(\\d+)";
    private static String CATEGORY_URL_REGEX = ".*/category/(\\d+)";

    private static String VALID_SELLER_URL_REGEX = "[A-Za-z0-9._-]";

    private static String[] HTTP_PREFIXES = {
            "http://www.",
            "https://www.",
            "http://",
            "https://",
            "www."
    };

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, ENCODE_CHARSET_UTF8);
        } catch (Exception e) {
        }
        return value;
    }

    public static String getFullUrl(String url) {
        if (!url.startsWith("http")) {      // !url.startsWith(AppController.BASE_URL)) {
            url = AppController.BASE_URL + url;
        }
        return url;
    }

    public static String createSellerUrl(UserVMLite user) {
        return AppController.BASE_URL + "/" + user.displayName.toLowerCase();
    }

    public static String createProductUrl(PostVMLite post) {
        return String.format(PRODUCT_URL, post.getId());
    }

    public static String createCategoryUrl(CategoryVM category) {
        return String.format(CATEGORY_URL, category.getId());
    }

    public static String createAppsDownloadUrl() {
        return APPS_DOWNLOAD_URL;
    }

    public static long parseSellerUrlId(String url) {
        return parseUrlMatcher(SELLER_URL_REGEX, url);
    }

    public static long parseProductUrlId(String url) {
        return parseUrlMatcher(PRODUCT_URL_REGEX, url);
    }

    public static long parseCategoryUrlId(String url) {
        return parseUrlMatcher(CATEGORY_URL_REGEX, url);
    }

    public static String createShortSellerUrl(UserVMLite user) {
        //return AppController.getInstance().getString(R.string.seller_url) + ": " + stripHttpPrefix(createSellerUrl(user));
        return stripHttpPrefix(createSellerUrl(user));
    }

    public static String stripHttpPrefix(String url) {
        for (String prefix : HTTP_PREFIXES) {
            if (url.startsWith(prefix)) {
                return url.replace(prefix, "");
            }
        }
        return url;
    }

    /**
     * Group always starts at 1. Group 0 is whole string.
     *
     * @param regex
     * @param url
     * @param pos
     * @return
     */
    private static long parseUrlMatcherAtPosition(String regex, String url, int pos) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        if (m.find()) {
            return Long.parseLong(m.group(pos));
        }
        return -1;
    }

    private static long parseUrlMatcher(String regex, String url) {
        return parseUrlMatcherAtPosition(regex, url, 1);
    }
}