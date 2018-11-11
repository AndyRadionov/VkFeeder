package com.radionov.vkfeeder.ui.main.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiPhotoSize;

import java.util.List;

/**
 * @author Andrey Radionov
 */
public class SlidingImageAdapter extends PagerAdapter {
    private List<VKApiPhotoSize> images;
    private Context context;


    public SlidingImageAdapter(Context context, List<VKApiPhotoSize> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, int position) {
        ImageView imageView = new ImageView(context);
        Picasso.get()
                .load(images.get(position).src)
                .fit()
                .centerCrop()
                .into(imageView);
        view.addView(imageView);

        return imageView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
