package com.smargav.api.utils;

import com.google.gson.Gson;

import java.lang.reflect.Field;

public class GsonUtil {

    public static Gson gson = new Gson();

    static {
        try {
            Class realmGsonUtilClass = Class.forName("com.smargav.api.db.RealmGsonUtil");
            Object o = realmGsonUtilClass.newInstance();
            Field f = o.getClass().getDeclaredField("gson");
            gson = (Gson) f.get(o);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
