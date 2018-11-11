package com.radionov.vkfeeder;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiPhotoSize;
import com.vk.sdk.api.model.VKPhotoSizes;

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
    public void destroyItem(ViewGroup container, int position, Object object) {
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
        bindPostPhoto(images.get(position), imageView);

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

    private void bindPostPhoto(VKApiPhotoSize photoSize, ImageView imageView) {
        Picasso.get()
                .load(photoSize.src)
                .fit()
                .centerCrop()
                .into(imageView);
    }

}
