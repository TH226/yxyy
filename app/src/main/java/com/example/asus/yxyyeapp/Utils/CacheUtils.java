package com.example.asus.yxyyeapp.Utils;

import android.content.Context;

/**
 * Created by asus on 2016/8/12.
 */
public class CacheUtils {

    /**
     * 设置缓存 key是url  value是json
     */
    public static void setCache(String key, String value, Context ctx){
        PrefUtils.setString(ctx,key,value);
        //可以将缓存放在文件中，文件名是（MD5）url，文件内容是json
    }
    /**
     * 获取缓存key  是url
     */
    public static String getCache(String key,Context ctx){
       return PrefUtils.getString(ctx,key,null);
    }
}
