package com.example.asus.yxyyeapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 * SharedPreferences的封装
 * Created by asus on 2016/8/8.
 */
public class PrefUtils {
    public static final String PREF_NAME = "config";

    public static boolean getBoolean(Context ctx,String key,boolean defaultValue){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context ctx,String key,boolean value){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(Context ctx,String key,String defaultValue){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void setString(Context ctx,String key,String value){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }
}
