package app.bkjk.bkjkimagepicker.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.R;
import app.bkjk.bkjkimagepicker.bean.IPImageItem;
import app.bkjk.bkjkimagepicker.util.IPNavigationBarChangeListener;
import app.bkjk.bkjkimagepicker.util.IPUtils;
import app.bkjk.bkjkimagepicker.view.IPSuperCheckBox;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：IPImagePreviewActivity.java
 * 修订历史：
 * ================================================
 */
public class IPImagePreviewActivity extends IPImagePreviewBaseActivity implements IPImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String ISORIGIN = "isOrigin";

    private boolean isOrigin;                      //是否选中原图
    private IPSuperCheckBox mCbCheck;                //是否选中当前图片的CheckBox
    private IPSuperCheckBox mCbOrigin;               //原图
    private Button mBtnOk;                         //确认图片的选择
    private View bottomBar;
    private View marginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isOrigin = getIntent().getBooleanExtra(IPImagePreviewActivity.ISORIGIN, false);
        mIPImagePicker.addOnImageSelectedListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setOnClickListener(this);

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(View.VISIBLE);

        mCbCheck = (IPSuperCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (IPSuperCheckBox) findViewById(R.id.cb_origin);
        marginView = findViewById(R.id.margin_bottom);
        mCbOrigin.setText(getString(R.string.bkjk_imagepicker_s_origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);

        //初始化当前页面的状态
        onImageSelected(0, null, false);
        IPImageItem item = mIPImageItems.get(mCurrentPosition);
        boolean isSelected = mIPImagePicker.isSelect(item);
        mTitleCount.setText(getString(R.string.bkjk_imagepicker_s_preview_image_count, mCurrentPosition + 1, mIPImageItems.size()));
        mCbCheck.setChecked(isSelected);
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                IPImageItem item = mIPImageItems.get(mCurrentPosition);
                boolean isSelected = mIPImagePicker.isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.bkjk_imagepicker_s_preview_image_count, mCurrentPosition + 1, mIPImageItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPImageItem IPImageItem = mIPImageItems.get(mCurrentPosition);
                int selectLimit = mIPImagePicker.getSelectLimit();
                if (mCbCheck.isChecked() && selectedImages.size() >= selectLimit) {
                    Toast.makeText(IPImagePreviewActivity.this, getString(R.string.bkjk_imagepicker_s_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    mCbCheck.setChecked(false);
                } else {
                    mIPImagePicker.addSelectedImageItem(mCurrentPosition, IPImageItem, mCbCheck.isChecked());
                }
            }
        });
        IPNavigationBarChangeListener.with(this).setListener(new IPNavigationBarChangeListener.OnSoftInputStateChangeListener() {
            @Override
            public void onNavigationBarShow(int orientation, int height) {
                marginView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = marginView.getLayoutParams();
                if (layoutParams.height == 0) {
                    layoutParams.height = IPUtils.getNavigationBarHeight(IPImagePreviewActivity.this);
                    marginView.requestLayout();
                }
            }

            @Override
            public void onNavigationBarHide(int orientation) {
                marginView.setVisibility(View.GONE);
            }
        });
        IPNavigationBarChangeListener.with(this, IPNavigationBarChangeListener.ORIENTATION_HORIZONTAL)
                .setListener(new IPNavigationBarChangeListener.OnSoftInputStateChangeListener() {
                    @Override
                    public void onNavigationBarShow(int orientation, int height) {
                        topBar.setPadding(0, 0, height, 0);
                        bottomBar.setPadding(0, 0, height, 0);
                    }

                    @Override
                    public void onNavigationBarHide(int orientation) {
                        topBar.setPadding(0, 0, 0, 0);
                        bottomBar.setPadding(0, 0, 0, 0);
                    }
                });
    }



    /**
     * 图片添加成功后，修改当前图片的选中数量
     * 当调用 addSelectedImageItem 或 deleteSelectedImageItem 都会触发当前回调
     */
    @Override
    public void onImageSelected(int position, IPImageItem item, boolean isAdd) {
        if (mIPImagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.bkjk_imagepicker_s_select_complete, mIPImagePicker.getSelectImageCount(), mIPImagePicker.getSelectLimit()));
        } else {
            mBtnOk.setText(getString(R.string.bkjk_imagepicker_s_complete));
        }

        if (mCbOrigin.isChecked()) {
            long size = 0;
            for (IPImageItem IPImageItem : selectedImages)
                size += IPImageItem.size;
            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.bkjk_imagepicker_s_origin_size, fileSize));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            if (mIPImagePicker.getSelectedImages().size() == 0) {
                mCbCheck.setChecked(true);
                IPImageItem IPImageItem = mIPImageItems.get(mCurrentPosition);
                mIPImagePicker.addSelectedImageItem(mCurrentPosition, IPImageItem, mCbCheck.isChecked());
            }
            Intent intent = new Intent();
            intent.putExtra(IPImagePicker.EXTRA_RESULT_ITEMS, mIPImagePicker.getSelectedImages());
            setResult(IPImagePicker.RESULT_CODE_ITEMS, intent);
            finish();

        } else if (id == R.id.btn_back) {
            Intent intent = new Intent();
            intent.putExtra(IPImagePreviewActivity.ISORIGIN, isOrigin);
            setResult(IPImagePicker.RESULT_CODE_BACK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(IPImagePreviewActivity.ISORIGIN, isOrigin);
        setResult(IPImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (IPImageItem item : selectedImages)
                    size += item.size;
                String fileSize = Formatter.formatFileSize(this, size);
                isOrigin = true;
                mCbOrigin.setText(getString(R.string.bkjk_imagepicker_s_origin_size, fileSize));
            } else {
                isOrigin = false;
                mCbOrigin.setText(getString(R.string.bkjk_imagepicker_s_origin));
            }
        }
    }

    @Override
    protected void onDestroy() {
        mIPImagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bkjk_imagepicker_top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bkjk_imagepicker_fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(Color.TRANSPARENT);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bkjk_imagepicker_top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bkjk_imagepicker_fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(R.color.bkjk_imagepicker_c_primary_dark);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
