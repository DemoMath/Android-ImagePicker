package app.bkjk.bkjkimagepicker.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.bkjk.bkjkimagepicker.IPDataHolder;
import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.R;
import app.bkjk.bkjkimagepicker.adapter.IPImagePageAdapter;
import app.bkjk.bkjkimagepicker.bean.IPImageItem;
import app.bkjk.bkjkimagepicker.util.IPUtils;
import app.bkjk.bkjkimagepicker.view.IPViewPagerFixed;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：图片预览的基类
 * 修订历史：
 * ================================================
 */
public abstract class IPImagePreviewBaseActivity extends IPImageBaseActivity {

    protected IPImagePicker mIPImagePicker;
    protected ArrayList<IPImageItem> mIPImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    protected int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
    protected TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    protected ArrayList<IPImageItem> selectedImages;   //所有已经选中的图片
    protected View content;
    protected View topBar;
    protected IPViewPagerFixed mViewPager;
    protected IPImagePageAdapter mAdapter;
    protected boolean isFromItems = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bkjk_imagepicker_activity_image_preview);

        mCurrentPosition = getIntent().getIntExtra(IPImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        isFromItems = getIntent().getBooleanExtra(IPImagePicker.EXTRA_FROM_ITEMS, false);

        if (isFromItems) {
            // 据说这样会导致大量图片崩溃
            mIPImageItems = (ArrayList<IPImageItem>) getIntent().getSerializableExtra(IPImagePicker.EXTRA_IMAGE_ITEMS);
        } else {
            // 下面采用弱引用会导致预览崩溃
            mIPImageItems = (ArrayList<IPImageItem>) IPDataHolder.getInstance().retrieve(IPDataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS);
        }

        mIPImagePicker = IPImagePicker.getInstance();
        selectedImages = mIPImagePicker.getSelectedImages();

        //初始化控件
        content = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = IPUtils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        topBar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleCount = (TextView) findViewById(R.id.tv_des);

        mViewPager = (IPViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new IPImagePageAdapter(this, mIPImageItems);
        mAdapter.setPhotoViewClickListener(new IPImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.bkjk_imagepicker_s_preview_image_count, mCurrentPosition + 1, mIPImageItems.size()));
    }

    /** 单击时，隐藏头和尾 */
    public abstract void onImageSingleTap();

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        IPImagePicker.getInstance().restoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        IPImagePicker.getInstance().saveInstanceState(outState);
    }
}