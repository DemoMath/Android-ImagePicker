package app.bkjk.bkjkimagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.bkjk.bkjkimagepicker.IPImagePicker;
import app.bkjk.bkjkimagepicker.R;
import app.bkjk.bkjkimagepicker.bean.IPImageFolder;
import app.bkjk.bkjkimagepicker.util.IPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：IPImageFolderAdapter.java
 * 修订历史：
 * ================================================
 */
public class IPImageFolderAdapter extends BaseAdapter {

    private IPImagePicker mIPImagePicker;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private int mImageSize;
    private List<IPImageFolder> mIPImageFolders;
    private int lastSelected = 0;

    public IPImageFolderAdapter(Activity activity, List<IPImageFolder> folders) {
        mActivity = activity;
        if (folders != null && folders.size() > 0) mIPImageFolders = folders;
        else mIPImageFolders = new ArrayList<>();

        mIPImagePicker = IPImagePicker.getInstance();
        mImageSize = IPUtils.getImageItemWidth(mActivity);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<IPImageFolder> folders) {
        if (folders != null && folders.size() > 0) mIPImageFolders = folders;
        else mIPImageFolders.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mIPImageFolders.size();
    }

    @Override
    public IPImageFolder getItem(int position) {
        return mIPImageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bkjk_imagepicker_adapter_folder_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        IPImageFolder folder = getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(mActivity.getString(R.string.bkjk_imagepicker_s_folder_image_count, folder.images.size()));
        mIPImagePicker.getIPImageLoader().displayImage(mActivity, folder.cover.path, holder.cover, mImageSize, mImageSize);

        if (lastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        public ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.iv_cover);
            folderName = (TextView) view.findViewById(R.id.tv_folder_name);
            imageCount = (TextView) view.findViewById(R.id.tv_image_count);
            folderCheck = (ImageView) view.findViewById(R.id.iv_folder_check);
            view.setTag(this);
        }
    }
}
