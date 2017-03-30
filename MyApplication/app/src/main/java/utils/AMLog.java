package utils;


import android.text.TextUtils;
import android.util.Log;

import com.example.wangxin.myapplication.BuildConfig;

public final class AMLog {
    public static void sysLog(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if(BuildConfig.DEBUG) {
                System.out.println(msg);
            }
        }
    }

    public static void e(String tag,String msg){
        if(BuildConfig.DEBUG) {
             if(!TextUtils.isEmpty(msg)){
                 Log.e(tag,msg);
              }
        }
    }
}
