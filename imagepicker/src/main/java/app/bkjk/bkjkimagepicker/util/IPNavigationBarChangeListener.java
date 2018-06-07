package app.bkjk.bkjkimagepicker.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：用于监听导航栏的显示和隐藏，主要用于适配华为EMUI系统上虚拟导航栏可随时收起和展开的情况
 * 修订历史：
 * ================================================
 */
public class IPNavigationBarChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

    public static final int ORIENTATION_VERTICAL = 1;           //监听竖屏模式导航栏的显示和隐藏
    public static final int ORIENTATION_HORIZONTAL = 2;         //监听横屏模式导航栏的显示和隐藏

    private Rect rect;
    private View rootView;
    private boolean isShowNavigationBar = false;
    private int orientation;
    private OnSoftInputStateChangeListener listener;

    public IPNavigationBarChangeListener(View rootView, int orientation) {
        this.rootView = rootView;
        this.orientation = orientation;
        rect = new Rect();
    }

    @Override
    public void onGlobalLayout() {
        rect.setEmpty();
        rootView.getWindowVisibleDisplayFrame(rect);
        int heightDiff = 0;
        if (orientation == ORIENTATION_VERTICAL) {
            heightDiff = rootView.getHeight() - (rect.bottom - rect.top);
        } else if (orientation == ORIENTATION_HORIZONTAL) {
            heightDiff = rootView.getWidth() - (rect.right - rect.left);
        }
        int navigationBarHeight = IPUtils.hasVirtualNavigationBar(rootView.getContext()) ? IPUtils.getNavigationBarHeight(rootView.getContext()) : 0;
        if (heightDiff >= navigationBarHeight && heightDiff < navigationBarHeight * 2) {
            if (!isShowNavigationBar && listener != null) {
                listener.onNavigationBarShow(orientation, heightDiff);
            }
            isShowNavigationBar = true;
        } else {
            if (isShowNavigationBar && listener != null) {
                listener.onNavigationBarHide(orientation);
            }
            isShowNavigationBar = false;
        }
    }

    public void setListener(OnSoftInputStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSoftInputStateChangeListener {
        void onNavigationBarShow(int orientation, int height);

        void onNavigationBarHide(int orientation);
    }

    public static IPNavigationBarChangeListener with(View rootView) {
        return with(rootView, ORIENTATION_VERTICAL);
    }

    public static IPNavigationBarChangeListener with(Activity activity) {
        return with(activity.findViewById(android.R.id.content), ORIENTATION_VERTICAL);
    }

    public static IPNavigationBarChangeListener with(View rootView, int orientation) {
        IPNavigationBarChangeListener changeListener = new IPNavigationBarChangeListener(rootView, orientation);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(changeListener);
        return changeListener;
    }

    public static IPNavigationBarChangeListener with(Activity activity, int orientation) {
        return with(activity.findViewById(android.R.id.content), orientation);
    }
}
