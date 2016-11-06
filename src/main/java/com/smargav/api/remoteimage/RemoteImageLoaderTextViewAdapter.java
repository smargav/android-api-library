package com.smargav.api.remoteimage;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class RemoteImageLoaderTextViewAdapter extends
    RemoteImageLoaderHandler.RemoteImageLoaderViewAdapter
{

    private boolean left, top, right, bottom;

    public RemoteImageLoaderTextViewAdapter(String imageUrl, TextView textView,
                                            Drawable errorDrawable, boolean left, boolean top, boolean right, boolean bottom)
    {
        super(imageUrl, textView, errorDrawable);
        this.left = left;
        this.top = top;
        this.right = right;
    }

    @Override
    protected void onImageLoadedFailed()
    {
        setCompoundDrawable(errorDrawable);
    }

    @Override
    protected void onImageLoadedSuccess(Bitmap bitmap)
    {
        Bitmap processedBitmap = processBitmap(bitmap);
        @SuppressWarnings("deprecation")
        Drawable drawable = new BitmapDrawable(processedBitmap);
        setCompoundDrawable(drawable);
    }

    @Override
    public void setDummyDrawableForView(Drawable dummyDrawable)
    {
        setCompoundDrawable(dummyDrawable);
    }

    @Override
    public TextView getView()
    {
        return (TextView) view;
    }

    private void setCompoundDrawable(Drawable drawable)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = view.getContext().getResources().getDisplayMetrics();
        if(drawable instanceof BitmapDrawable)
        {
            // For some reason this must be set explicitly or otherwise the target density will be
            // wrong.
            ((BitmapDrawable) drawable).setTargetDensity(metrics.densityDpi);
        }
        Drawable leftDrawable = left ? drawable : null;
        Drawable topDrawable = top ? drawable : null;
        Drawable rightDrawable = right ? drawable : null;
        Drawable bottomDrawable = bottom ? drawable : null;
        ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable,
                rightDrawable, bottomDrawable);
    }
}
