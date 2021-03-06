package com.beautypop.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fix ViewPager WRAP_CONTENT - http://stackoverflow.com/a/20784791
 * Get height of the biggest child
 */
public class AdaptiveViewPager extends ViewPager {
    private static final String TAG = AdaptiveViewPager.class.getName();

    private static final int DEFAULT_OFFSCREEN_PAGE_LIMIT = 1;

    private boolean isPagingEnabled = true;

    public void setPagingEnabled(boolean isPagingEnabled) {
        this.isPagingEnabled = isPagingEnabled;
    }

    public AdaptiveViewPager(Context context) {
        super(context);
        setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGE_LIMIT);
    }

    public AdaptiveViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGE_LIMIT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }
}
