package com.radionov.vkfeeder.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.radionov.vkfeeder.R;
import com.radionov.vkfeeder.data.entities.VkFeedPost;
import com.radionov.vkfeeder.ui.common.BaseActivity;
import com.radionov.vkfeeder.ui.main.adapters.FeedPostAdapter;
import com.radionov.vkfeeder.viewmodels.MainViewModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

public class MainActivity extends BaseActivity {

    private static final int VISIBLE_THRESHOLD = 5;

    private MainViewModel mainViewModel;

    private CardStackView feedContainer;
    private FeedPostAdapter feedPostAdapter;
    private CardStackLayoutManager manager;

    private CardStackListener cardStackListener = initCardListener();
    private SwipeAnimationSetting swipeLeftSetting = initLeftSwipeSettings();
    private SwipeAnimationSetting swipeRightSetting = initRightSwipeSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFabListener();
        initCardsView();
    }

    @Override
    protected void initViewModel() {
        mainViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(MainViewModel.class);

        mainViewModel.getFeedLiveData()
                .observe(this, feedPosts -> {
                    if (feedPosts != null && !feedPosts.isEmpty()) {
                        feedPostAdapter.updateData(feedPosts);
                    }
                });

        mainViewModel.getActionErrorLiveData()
                .observe(this, error -> showError(getString(R.string.operation_error)));
    }

    @Override
    protected void onNetworkChange(boolean isConnected) {
        mainViewModel.onNetworkChange(isConnected);
        if (!isConnected) {
            showNoConnection();
        }
    }

    private void initFabListener() {
        View fabSkip = findViewById(R.id.fab_skip);
        View fabLike = findViewById(R.id.fab_like);
        fabSkip.setOnClickListener(v -> {
            manager.setSwipeAnimationSetting(swipeLeftSetting);
            feedContainer.swipe();
        });

        fabLike.setOnClickListener(v -> {
            manager.setSwipeAnimationSetting(swipeRightSetting);
            feedContainer.swipe();
        });
    }

    private void initCardsView() {
        feedContainer = findViewById(R.id.feed_container);
        feedPostAdapter = new FeedPostAdapter(this);

        manager = new CardStackLayoutManager(this, cardStackListener);
        manager.setStackFrom(StackFrom.None);
        manager.setMaxDegree(20f);
        manager.setVisibleCount(2);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);

        feedContainer.setLayoutManager(manager);
        feedContainer.setAdapter(feedPostAdapter);
    }

    private SwipeAnimationSetting initLeftSwipeSettings() {
        return new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .build();
    }

    private SwipeAnimationSetting initRightSwipeSettings() {
        return new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .build();
    }

    private CardStackListener initCardListener() {
        return new CardStackListener() {
            @Override
            public void onCardSwiped(Direction direction) {
                VkFeedPost topPost = feedPostAdapter.getTopElement();

                if (direction == Direction.Left) {
                    //todo
                    //                mainViewModel.like(topPost);
                } else {
                    //                mainViewModel.skip(topPost);
                }
                if (manager.getTopPosition() >= feedPostAdapter.getItemCount() - VISIBLE_THRESHOLD) {
                    mainViewModel.loadMore();
                }
            }

            @Override
            public void onCardDragging(Direction direction, float ratio) {/*NOP*/}

            @Override
            public void onCardRewound() {/*NOP*/}

            @Override
            public void onCardCanceled() {/*NOP*/}
        };
    }
}
