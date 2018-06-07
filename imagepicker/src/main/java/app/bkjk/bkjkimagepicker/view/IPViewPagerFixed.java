package app.bkjk.bkjkimagepicker.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：
 * 创建日期：2018/6/4
 * 描    述：ViewPagerFixed.java
 * 修订历史：修复图片在ViewPager控件中缩放报错的BUG
 * ================================================
 */
public class IPViewPagerFixed extends ViewPager {

    public IPViewPagerFixed(Context context) {
        super(context);
    }

    public IPViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
