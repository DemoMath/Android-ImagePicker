package app.bkjk.bkjkimagepicker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.R;
import app.bkjk.bkjkimagepicker.bean.IPImageItem;
import app.bkjk.bkjkimagepicker.util.IPBitmapUtil;
import app.bkjk.bkjkimagepicker.view.IPCropImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：IPImageCropActivity.java
 * 修订历史：
 * ================================================
 */
public class IPImageCropActivity extends IPImageBaseActivity implements View.OnClickListener, IPCropImageView.OnBitmapSaveCompleteListener {

    private IPCropImageView mIPCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ArrayList<IPImageItem> mIPImageItems;
    private IPImagePicker mIPImagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bkjk_imagepicker_activity_image_crop);

        mIPImagePicker = IPImagePicker.getInstance();

        //初始化View
        findViewById(R.id.btn_back).setOnClickListener(this);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText(getString(R.string.bkjk_imagepicker_s_complete));
        btn_ok.setOnClickListener(this);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText(getString(R.string.bkjk_imagepicker_s_photo_crop));
        mIPCropImageView = (IPCropImageView) findViewById(R.id.cv_crop_image);
        mIPCropImageView.setOnBitmapSaveCompleteListener(this);

        //获取需要的参数
        mOutputX = mIPImagePicker.getOutPutX();
        mOutputY = mIPImagePicker.getOutPutY();
        mIsSaveRectangle = mIPImagePicker.isSaveRectangle();
        mIPImageItems = mIPImagePicker.getSelectedImages();
        String imagePath = mIPImageItems.get(0).path;

        mIPCropImageView.setFocusStyle(mIPImagePicker.getStyle());
        mIPCropImageView.setFocusWidth(mIPImagePicker.getFocusWidth());
        mIPCropImageView.setFocusHeight(mIPImagePicker.getFocusHeight());

        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(imagePath, options);
//        mCropImageView.setImageBitmap(mBitmap);
        //设置默认旋转角度
        mIPCropImageView.setImageBitmap(mIPCropImageView.rotate(mBitmap, IPBitmapUtil.getBitmapDegree(imagePath)));

//        mCropImageView.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_ok) {
            mIPCropImageView.saveBitmapToFile(mIPImagePicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
//        Toast.makeText(IPImageCropActivity.this, "裁剪成功:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        //裁剪后替换掉返回数据的内容，但是不要改变全局中的选中数据
        mIPImageItems.remove(0);
        IPImageItem IPImageItem = new IPImageItem();
        IPImageItem.path = file.getAbsolutePath();
        mIPImageItems.add(IPImageItem);

        Intent intent = new Intent();
        intent.putExtra(IPImagePicker.EXTRA_RESULT_ITEMS, mIPImageItems);
        setResult(IPImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIPCropImageView.setOnBitmapSaveCompleteListener(null);
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
