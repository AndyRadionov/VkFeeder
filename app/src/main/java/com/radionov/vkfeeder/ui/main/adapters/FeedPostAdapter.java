package com.radionov.vkfeeder.ui.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.radionov.vkfeeder.R;
import com.radionov.vkfeeder.data.entities.VkFeedPost;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoSize;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKAttachments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrey Radionov
 */
public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder> {
    private final List<VkFeedPost> posts = new ArrayList<>();
    private final DateFormat dateFormat;
    private final DateFormat todayFormat;
    private final Transformation transformation = new RoundedTransformationBuilder()
            .cornerRadiusDp(30)
            .oval(false)
            .build();

    public FeedPostAdapter(Context context) {
        String dateFormatString = context.getString(R.string.date_format);
        String todayFormatString = context.getString(R.string.today_format);
        dateFormat = new SimpleDateFormat(dateFormatString, Locale.ROOT);
        todayFormat = new SimpleDateFormat(todayFormatString, Locale.ROOT);
    }

    public VkFeedPost getTopElement() {
        return posts.get(0);
    }

    public void updateData(@NonNull final List<VkFeedPost> newPosts) {
        int startPosition = posts.size();
        int lastPosition = newPosts.size();
        posts.addAll(newPosts);
        notifyItemRangeInserted(startPosition, startPosition + lastPosition);
    }

    @NonNull
    @Override
    public FeedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new FeedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedPostViewHolder holder, int position) {
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return posts.size();
    }

    class FeedPostViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView authorName;
        private final ImageView authorPhoto;
        private final TextView dateView;
        private final TextView postText;
        private final TextView btnMore;

        private final TabLayout tabLayout;
        private final ViewPager photoPager;

        private final View prevPage;
        private final View nextPage;

        private FeedPostViewHolder(View itemView) {
            super(itemView);

            authorName = itemView.findViewById(R.id.author_name);
            authorPhoto = itemView.findViewById(R.id.author_photo);
            dateView = itemView.findViewById(R.id.date);
            postText = itemView.findViewById(R.id.post_text);
            btnMore = itemView.findViewById(R.id.btn_more);

            tabLayout = itemView.findViewById(R.id.tab_indicator);
            photoPager = itemView.findViewById(R.id.photo_pager);

            prevPage = itemView.findViewById(R.id.pager_prev);
            nextPage = itemView.findViewById(R.id.pager_next);
        }

        private void bind(int position) {
            final VkFeedPost feedPost = posts.get(position);

            postText.setText(feedPost.getText());
            postText.setMaxLines(5);
            if (feedPost.getText().length() > 200) {
                btnMore.setVisibility(View.VISIBLE);
                btnMore.setOnClickListener(this);
            } else {
                btnMore.setVisibility(View.GONE);
            }

            Picasso.get()
                    .load(feedPost.getFeedAuthor().getPhoto())
                    .fit()
                    .transform(transformation)
                    .into(authorPhoto);

            setAuthorName(feedPost.getFeedAuthor().getName());
            setDate(feedPost.getDate());
            setPhotos(feedPost);
        }

        private void setAuthorName(String name) {
            if (name.length() > 30) name = name.substring(0, 30) + "...";
            authorName.setText(name);
        }

        private void setDate(long unixDate) {
            long date = unixDate * 1000;
            if (DateUtils.isToday(date)) {
                dateView.setText(todayFormat.format(new Date(date)));
            } else {
                dateView.setText(dateFormat.format(new Date(date)));
            }
        }

        private void setPhotos(VkFeedPost feedPost) {
            if (feedPost.getAttachments() != null) {
                List<VKApiPhotoSize> photoSizes = new ArrayList<>();
                for (int i = 0; i < feedPost.getAttachments().size(); i++) {
                    VKAttachments.VKApiAttachment attachment = feedPost.getAttachments().get(i);
                    if (attachment.getType().equals(VKAttachments.TYPE_PHOTO)) {
                        VKApiPhoto photo = (VKApiPhoto) attachment;
                        VKApiPhotoSize photoSize = photo.src.get(photo.src.getCount() - 1);
                        photoSizes.add(photoSize);
                    } else if (attachment.getType().equals(VKAttachments.TYPE_VIDEO)) {
                        VKApiVideo video = (VKApiVideo) attachment;
                        VKApiPhotoSize photoSize = video.photo.get(video.photo.getCount() - 1);
                        photoSizes.add(photoSize);
                    }
                }

                photoPager.setAdapter(new SlidingImageAdapter(itemView.getContext(), photoSizes));
                if (photoSizes.size() == 1) {
                    photoPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.INVISIBLE);
                } else if (photoSizes.size() > 1) {
                    photoPager.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.VISIBLE);
                    photoPager.setOffscreenPageLimit(photoSizes.size());
                    tabLayout.setupWithViewPager(photoPager);
                    prevPage.setOnClickListener(this);
                    nextPage.setOnClickListener(this);
                } else {
                    photoPager.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.INVISIBLE);
                    prevPage.setOnClickListener(null);
                    nextPage.setOnClickListener(null);
                }
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pager_prev: {
                    int currentPage = photoPager.getCurrentItem();
                    if (currentPage == 0) return;
                    photoPager.setCurrentItem(--currentPage);
                    break;
                } case R.id.pager_next: {
                    int currentPage = photoPager.getCurrentItem();
                    if (currentPage == photoPager.getAdapter().getCount()) return;
                    photoPager.setCurrentItem(++currentPage);
                    break;
                } case R.id.btn_more: {
                    btnMore.setVisibility(View.GONE);
                    postText.setMaxLines(Integer.MAX_VALUE);
                }
            }
        }
    }
}
