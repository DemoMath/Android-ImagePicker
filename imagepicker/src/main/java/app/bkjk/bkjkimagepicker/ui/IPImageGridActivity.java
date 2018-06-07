package app.bkjk.bkjkimagepicker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import app.bkjk.bkjkimagepicker.IPDataHolder;
import app.bkjk.bkjkimagepicker.IPImageDataSource;
import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.R;
import app.bkjk.bkjkimagepicker.adapter.IPImageFolderAdapter;
import app.bkjk.bkjkimagepicker.adapter.IPImageRecyclerAdapter;
import app.bkjk.bkjkimagepicker.adapter.IPImageRecyclerAdapter.OnImageItemClickListener;
import app.bkjk.bkjkimagepicker.bean.IPImageFolder;
import app.bkjk.bkjkimagepicker.bean.IPImageItem;
import app.bkjk.bkjkimagepicker.util.IPUtils;
import app.bkjk.bkjkimagepicker.view.IPFolderPopUpWindow;
import app.bkjk.bkjkimagepicker.view.IPGridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：IPImageGridActivity.java
 * 修订历史：新增可直接传递是否裁剪参数，以及直接拍照
 * ================================================
 */
public class IPImageGridActivity extends IPImageBaseActivity implements IPImageDataSource.OnImagesLoadedListener, OnImageItemClickListener, IPImagePicker.OnImageSelectedListener, View.OnClickListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";

    private IPImagePicker mIPImagePicker;

    private boolean isOrigin = false;  //是否选中原图
    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private View mllDir; //文件夹切换按钮
    private TextView mtvDir; //显示当前文件夹
    private TextView mBtnPre;      //预览按钮
    private IPImageFolderAdapter mIPImageFolderAdapter;    //图片文件夹的适配器
    private IPFolderPopUpWindow mIPFolderPopupWindow;  //ImageSet的PopupWindow
    private List<IPImageFolder> mIPImageFolders;   //所有的图片文件夹
    //    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器
    private boolean directPhoto = false; // 默认不是直接调取相机
    private RecyclerView mRecyclerView;
    private IPImageRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        directPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, directPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bkjk_imagepicker_activity_image_grid);

        mIPImagePicker = IPImagePicker.getInstance();
        mIPImagePicker.clear();
        mIPImagePicker.addOnImageSelectedListener(this);

        Intent data = getIntent();
        // 新增可直接拍照
        if (data != null && data.getExtras() != null) {
            directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false); // 默认不是直接打开相机
            if (directPhoto) {
                if (!(checkPermission(Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, IPImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    mIPImagePicker.takePicture(this, IPImagePicker.REQUEST_CODE_TAKE);
                }
            }
            ArrayList<IPImageItem> images = (ArrayList<IPImageItem>) data.getSerializableExtra(EXTRAS_IMAGES);
            mIPImagePicker.setSelectedImages(images);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnPre = (TextView) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mFooterBar = findViewById(R.id.footer_bar);
        mllDir = findViewById(R.id.ll_dir);
        mllDir.setOnClickListener(this);
        mtvDir = (TextView) findViewById(R.id.tv_dir);
        if (mIPImagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

//        mImageGridAdapter = new ImageGridAdapter(this, null);
        mIPImageFolderAdapter = new IPImageFolderAdapter(this, null);
        mRecyclerAdapter = new IPImageRecyclerAdapter(this, null);

        onImageSelected(0, null, false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new IPImageDataSource(this, null, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            new IPImageDataSource(this, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new IPImageDataSource(this, null, this);
            } else {
                showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mIPImagePicker.takePicture(this, IPImagePicker.REQUEST_CODE_TAKE);
            } else {
                showToast("权限被禁止，无法打开相机");
            }
        }
    }

    @Override
    protected void onDestroy() {
        mIPImagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(IPImagePicker.EXTRA_RESULT_ITEMS, mIPImagePicker.getSelectedImages());
            setResult(IPImagePicker.RESULT_CODE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
            finish();
        } else if (id == R.id.ll_dir) {
            if (mIPImageFolders == null) {
                Log.i("IPImageGridActivity", "您的手机没有图片");
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            mIPImageFolderAdapter.refreshData(mIPImageFolders);  //刷新数据
            if (mIPFolderPopupWindow.isShowing()) {
                mIPFolderPopupWindow.dismiss();
            } else {
                mIPFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mIPImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mIPFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(IPImageGridActivity.this, IPImagePreviewActivity.class);
            intent.putExtra(IPImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(IPImagePicker.EXTRA_IMAGE_ITEMS, mIPImagePicker.getSelectedImages());
            intent.putExtra(IPImagePreviewActivity.ISORIGIN, isOrigin);
            intent.putExtra(IPImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intent, IPImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mIPFolderPopupWindow = new IPFolderPopUpWindow(this, mIPImageFolderAdapter);
        mIPFolderPopupWindow.setOnItemClickListener(new IPFolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mIPImageFolderAdapter.setSelectIndex(position);
                mIPImagePicker.setCurrentImageFolderPosition(position);
                mIPFolderPopupWindow.dismiss();
                IPImageFolder IPImageFolder = (IPImageFolder) adapterView.getAdapter().getItem(position);
                if (null != IPImageFolder) {
//                    mImageGridAdapter.refreshData(IPImageFolder.images);
                    mRecyclerAdapter.refreshData(IPImageFolder.images);
                    mtvDir.setText(IPImageFolder.name);
                }
            }
        });
        mIPFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }

    @Override
    public void onImagesLoaded(List<IPImageFolder> IPImageFolders) {
        this.mIPImageFolders = IPImageFolders;
        mIPImagePicker.setIPImageFolders(IPImageFolders);
        if (IPImageFolders.size() == 0) {
//            mImageGridAdapter.refreshData(null);
            mRecyclerAdapter.refreshData(null);
        } else {
//            mImageGridAdapter.refreshData(IPImageFolders.get(0).images);
            mRecyclerAdapter.refreshData(IPImageFolders.get(0).images);
        }
//        mImageGridAdapter.setOnImageItemClickListener(this);
        mRecyclerAdapter.setOnImageItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new IPGridSpacingItemDecoration(3, IPUtils.dp2px(this, 2), false));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mIPImageFolderAdapter.refreshData(IPImageFolders);
    }

    @Override
    public void onImageItemClick(View view, IPImageItem IPImageItem, int position) {
        //根据是否有相机按钮确定位置
        position = mIPImagePicker.isShowCamera() ? position - 1 : position;
        if (mIPImagePicker.isMultiMode()) {
            Intent intent = new Intent(IPImageGridActivity.this, IPImagePreviewActivity.class);
            intent.putExtra(IPImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);

            /**
             * 2017-03-20
             *
             * 依然采用弱引用进行解决，采用单例加锁方式处理
             */

            // 据说这样会导致大量图片的时候崩溃
//            intent.putExtra(IPImagePicker.EXTRA_IMAGE_ITEMS, mIPImagePicker.getCurrentImageFolderItems());

            // 但采用弱引用会导致预览弱引用直接返回空指针
            IPDataHolder.getInstance().save(IPDataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS, mIPImagePicker.getCurrentImageFolderItems());
            intent.putExtra(IPImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, IPImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
            mIPImagePicker.clearSelectedImages();
            mIPImagePicker.addSelectedImageItem(position, mIPImagePicker.getCurrentImageFolderItems().get(position), true);
            if (mIPImagePicker.isCrop()) {
                Intent intent = new Intent(IPImageGridActivity.this, IPImageCropActivity.class);
                startActivityForResult(intent, IPImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(IPImagePicker.EXTRA_RESULT_ITEMS, mIPImagePicker.getSelectedImages());
                setResult(IPImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, IPImageItem item, boolean isAdd) {
        if (mIPImagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.bkjk_imagepicker_s_select_complete, mIPImagePicker.getSelectImageCount(), mIPImagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
            mBtnPre.setText(getResources().getString(R.string.bkjk_imagepicker_s_preview_count, mIPImagePicker.getSelectImageCount()));
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.bkjk_imagepicker_c_primary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.bkjk_imagepicker_c_primary_inverted));
        } else {
            mBtnOk.setText(getString(R.string.bkjk_imagepicker_s_complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
            mBtnPre.setText(getResources().getString(R.string.bkjk_imagepicker_s_preview));
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.bkjk_imagepicker_c_secondary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.bkjk_imagepicker_c_secondary_inverted));
        }
//        mImageGridAdapter.notifyDataSetChanged();
//        mRecyclerAdapter.notifyItemChanged(position); // 17/4/21 fix the position while click img to preview
//        mRecyclerAdapter.notifyItemChanged(position + (mIPImagePicker.isShowCamera() ? 1 : 0));// 17/4/24  fix the position while click right bottom preview button
        for (int i = mIPImagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == IPImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(IPImagePreviewActivity.ISORIGIN, false);
            } else {
                //从拍照界面返回
                //点击 X , 没有选择照片
                if (data.getSerializableExtra(IPImagePicker.EXTRA_RESULT_ITEMS) == null) {
                    //什么都不做 直接调起相机
                } else {
                    //说明是从裁剪页面过来的数据，直接返回就可以
                    setResult(IPImagePicker.RESULT_CODE_ITEMS, data);
                }
                finish();
            }
        } else {
            //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
            if (resultCode == RESULT_OK && requestCode == IPImagePicker.REQUEST_CODE_TAKE) {
                //发送广播通知图片增加了
                IPImagePicker.galleryAddPic(this, mIPImagePicker.getTakeImageFile());

                /**
                 * 2017-03-21 对机型做旋转处理
                 */
                String path = mIPImagePicker.getTakeImageFile().getAbsolutePath();
//                int degree = IPBitmapUtil.getBitmapDegree(path);
//                if (degree != 0){
//                    Bitmap bitmap = IPBitmapUtil.rotateBitmapByDegree(path,degree);
//                    if (bitmap != null){
//                        File file = new File(path);
//                        try {
//                            FileOutputStream bos = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                            bos.flush();
//                            bos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

                IPImageItem IPImageItem = new IPImageItem();
                IPImageItem.path = path;
                mIPImagePicker.clearSelectedImages();
                mIPImagePicker.addSelectedImageItem(0, IPImageItem, true);
                if (mIPImagePicker.isCrop()) {
                    Intent intent = new Intent(IPImageGridActivity.this, IPImageCropActivity.class);
                    startActivityForResult(intent, IPImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(IPImagePicker.EXTRA_RESULT_ITEMS, mIPImagePicker.getSelectedImages());
                    setResult(IPImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                }
            } else if (directPhoto) {
                finish();
            }
        }
    }

}