package app.bkjk.bkjkimagepicker;

import app.bkjk.bkjkimagepicker.bean.IPImageItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ================================================
 * 作    者：wudi
 * 版    本：
 * 创建日期：2018/6/4
 * 描    述：新的DataHolder
 * 修订历史：使用单例和弱引用解决崩溃问题
 * ================================================
 */
public class IPDataHolder {
    public static final String DH_CURRENT_IMAGE_FOLDER_ITEMS = "dh_current_image_folder_items";

    private static IPDataHolder mInstance;
    private Map<String, List<IPImageItem>> data;

    public static IPDataHolder getInstance() {
        if (mInstance == null){
            synchronized (IPDataHolder.class){
                if (mInstance == null){
                    mInstance = new IPDataHolder();
                }
            }
        }
        return mInstance;
    }

    private IPDataHolder() {
        data = new HashMap<>();
    }

    public void save(String id, List<IPImageItem> object) {
        if (data != null){
            data.put(id, object);
        }
    }

    public Object retrieve(String id) {
        if (data == null || mInstance == null){
            throw new RuntimeException("你必须先初始化");
        }
        return data.get(id);
    }
}
