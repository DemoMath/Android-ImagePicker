package app.bkjk.bkjkimagepicker.util;

import android.content.Context;

/**
 * ================================================
 * 作    者：wudi
 * 版    本：1.0.0
 * 创建日期：2018/6/4
 * 描    述：用于解决provider冲突的util
 * 修订历史：
 * ================================================
 */
public class IPProviderUtil {

    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
