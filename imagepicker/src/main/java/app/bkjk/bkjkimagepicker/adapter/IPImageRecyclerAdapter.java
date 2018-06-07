package app.bkjk.bkjkimagepicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.R;
import app.bkjk.bkjkimagepicker.bean.IPImageItem;
import app.bkjk.bkjkimagepicker.ui.IPImageBaseActivity;
import app.bkjk.bkjkimagepicker.ui.IPImageGridActivity;
import app.bkjk.bkjkimagepicker.util.IPUtils;
import app.bkjk.bkjkimagepicker.view.IPSuperCheckBox;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：加载相册图片的RecyclerView适配器,用于替换原项目的GridView，使用局部刷新解决选中照片出现闪动问题
 * 修订历史：
 * ================================================
 */
public class IPImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {


    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机
    private IPImagePicker mIPImagePicker;
    private Activity mActivity;
    private ArrayList<IPImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<IPImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private LayoutInflater mInflater;
    private OnImageItemClickListener listener;   //图片被点击的监听

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, IPImageItem IPImageItem, int position);
    }

    public void refreshData(ArrayList<IPImageItem> images) {
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        notifyDataSetChanged();
    }

    /**
     * 构造方法
     */
    public IPImageRecyclerAdapter(Activity activity, ArrayList<IPImageItem> images) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;

        mImageSize = IPUtils.getImageItemWidth(mActivity);
        mIPImagePicker = IPImagePicker.getInstance();
        isShowCamera = mIPImagePicker.isShowCamera();
        mSelectedImages = mIPImagePicker.getSelectedImages();
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA){
            return new CameraViewHolder(mInflater.inflate(R.layout.bkjk_imagepicker_adapter_camera_item,parent,false));
        }
        return new ImageViewHolder(mInflater.inflate(R.layout.bkjk_imagepicker_adapter_image_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder){
            ((CameraViewHolder)holder).bindCamera();
        }else if (holder instanceof ImageViewHolder){
            ((ImageViewHolder)holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    public IPImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    private class ImageViewHolder extends ViewHolder {

        View rootView;
        ImageView ivThumb;
        View mask;
        View checkView;
        IPSuperCheckBox cbCheck;


        ImageViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            mask = itemView.findViewById(R.id.mask);
            checkView=itemView.findViewById(R.id.checkView);
            cbCheck = (IPSuperCheckBox) itemView.findViewById(R.id.cb_check);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
        }

        void bind(final int position){
            final IPImageItem IPImageItem = getItem(position);
            ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onImageItemClick(rootView, IPImageItem, position);
                }
            });
            checkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbCheck.setChecked(!cbCheck.isChecked());
                    int selectLimit = mIPImagePicker.getSelectLimit();
                    if (cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.bkjk_imagepicker_s_select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        cbCheck.setChecked(false);
                        mask.setVisibility(View.GONE);
                    } else {
                        mIPImagePicker.addSelectedImageItem(position, IPImageItem, cbCheck.isChecked());
                        mask.setVisibility(View.VISIBLE);
                    }
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (mIPImagePicker.isMultiMode()) {
                cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(IPImageItem);
                if (checked) {
                    mask.setVisibility(View.VISIBLE);
                    cbCheck.setChecked(true);
                } else {
                    mask.setVisibility(View.GONE);
                    cbCheck.setChecked(false);
                }
            } else {
                cbCheck.setVisibility(View.GONE);
            }
            mIPImagePicker.getIPImageLoader().displayImage(mActivity, IPImageItem.path, ivThumb, mImageSize, mImageSize); //显示图片
        }

    }

    private class CameraViewHolder extends ViewHolder {

        View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        void bindCamera(){
            mItemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
            mItemView.setTag(null);
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((IPImageBaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, IPImageGridActivity.REQUEST_PERMISSION_CAMERA);
                    } else {
                        mIPImagePicker.takePicture(mActivity, IPImagePicker.REQUEST_CODE_TAKE);
                    }
                }
            });
        }
    }
}
