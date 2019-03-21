package com.smargav.api.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * FontsOverride.setDefaultFont(this, "DEFAULT", "MyFontAsset.ttf");
 * FontsOverride.setDefaultFont(this, "MONOSPACE", "MyFontAsset2.ttf");
 * FontsOverride.setDefaultFont(this, "SERIF", "MyFontAsset3.ttf");
 * FontsOverride.setDefaultFont(this, "SANS_SERIF", "MyFontAsset4.ttf");
 * Use this class at the beginning of the Activity.
 */
public final class FontsOverride {
    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);

        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName, Typeface newTypeface) {
        try {
            Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}