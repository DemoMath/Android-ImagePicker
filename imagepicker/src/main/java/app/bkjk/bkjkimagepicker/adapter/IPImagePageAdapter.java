package app.bkjk.bkjkimagepicker.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.bean.IPImageItem;
import app.bkjk.bkjkimagepicker.util.IPUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：IPImagePageAdapter.java
 * 修订历史：
 * ================================================
 */
public class IPImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private IPImagePicker mIPImagePicker;
    private ArrayList<IPImageItem> images = new ArrayList<>();
    private Activity mActivity;
    public PhotoViewClickListener listener;

    public IPImagePageAdapter(Activity activity, ArrayList<IPImageItem> images) {
        this.mActivity = activity;
        this.images = images;

        DisplayMetrics dm = IPUtils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        mIPImagePicker = IPImagePicker.getInstance();
    }

    public void setData(ArrayList<IPImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity);
        IPImageItem IPImageItem = images.get(position);
        mIPImagePicker.getIPImageLoader().displayImagePreview(mActivity, IPImageItem.path, photoView, screenWidth, screenHeight);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
