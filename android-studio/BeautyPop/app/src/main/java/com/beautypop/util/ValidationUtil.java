package com.beautypop.util;

import android.widget.EditText;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beautypop.R;
import com.beautypop.app.AppController;

public class ValidationUtil {

    public static final int USER_DISPLAYNAME_MIN_CHAR = 2;
    public static final int USER_DISPLAYNAME_MAX_CHAR = 30;
    public static final int USER_NAME_MAX_CHAR = 20;

    private static final String EMAIL_FORMAT_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String USER_DISPLAYNAME_FORMAT_REGEX =
            "^[_A-Za-z0-9]+([\\._A-Za-z0-9]+)*[_A-Za-z0-9]$";
    private static final String USER_NAME_FORMAT_REGEX =
            "^[_\\p{L}0-9]+([\\._\\p{L}0-9]+)*$";      // \p{L} matches letter in any language

    private static final String ERROR_REQUIRED = AppController.getInstance().getString(R.string.signup_error_field_required);
    private static final String ERROR_EMAIL_FORMAT = AppController.getInstance().getString(R.string.signup_error_email_format);
    private static final String ERROR_USER_DISPLAYNAME_FORMAT = AppController.getInstance().getString(R.string.signup_error_displayname_format);
    private static final String ERROR_USER_DISPLAYNAME_MIN_MAX_CHAR = AppController.getInstance().getString(R.string.signup_error_displayname_min_max_char);
    private static final String ERROR_USER_DISPLAYNAME_NO_SPACE = AppController.getInstance().getString(R.string.signup_error_displayname_no_space);
    private static final String ERROR_USER_DISPLAYNAME_PERIODS = AppController.getInstance().getString(R.string.signup_error_displayname_periods);
    private static final String ERROR_USER_NAME_MIN_MAX_CHAR = AppController.getInstance().getString(R.string.signup_error_name_min_max_char);

    /**
     * Email validation
     *
     * @param editText
     * @return
     */
    public static boolean isEmailValid(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // whitespace
        if (hasWhitespace(text)) {
            editText.setError(ERROR_EMAIL_FORMAT);
            return false;
        }

        return isValid(editText, EMAIL_FORMAT_REGEX, ERROR_EMAIL_FORMAT, true);
    }

    public static boolean hasWhitespace(String text) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * Displayname validation
     * - no whitespace chars, special chars
     * - min 2 chars
     * - max 20 chars
     *
     * @param editText
     * @return
     */
    public static boolean isValidDisplayName(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // char 2-30
        if (text.length() < USER_DISPLAYNAME_MIN_CHAR || text.length() > USER_DISPLAYNAME_MAX_CHAR) {
            editText.setError(ERROR_USER_DISPLAYNAME_MIN_MAX_CHAR);
            return false;
        }

        // whitespace
        if (hasWhitespace(text)) {
            editText.setError(ERROR_USER_DISPLAYNAME_NO_SPACE);
            return false;
        }

        // .. in a row
        if (text.contains("..")) {
            editText.setError(ERROR_USER_DISPLAYNAME_PERIODS);
            return false;
        }

        return isValid(editText, USER_DISPLAYNAME_FORMAT_REGEX, ERROR_USER_DISPLAYNAME_FORMAT, true);
    }

    public static boolean isValidUserName(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // char max 20
        if (text.length() > USER_NAME_MAX_CHAR) {
            editText.setError(ERROR_USER_NAME_MIN_MAX_CHAR);
            return false;
        }

        return true;
    }

    /**
     * return true if the input field is valid, based on the parameter passed
     *
     * @param editText
     * @param regex
     * @param errMsg
     * @param required
     * @return
     */
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText))
            return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    /**
     * check the input field has any text or not
     * return true if it contains text otherwise false
     *
     * @param editText
     * @return
     */
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(ERROR_REQUIRED);
            return false;
        }

        return true;
    }

    public static String appendError(String error, String newError) {
        if (!StringUtils.isEmpty(error))
            error += "\n";
        return error + newError;
    }
}