package com.radionov.vkfeeder;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int VISIBLE_TRESHOLD = 5;
    private CardStackView feedContainer;
    private FeedPostAdapter feedPostAdapter;
    private CardStackLayoutManager manager;
    private String nextPage;

    private CardStackListener cardStackListener = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {/*NOP*/}

        @Override
        public void onCardSwiped(Direction direction) {
            VkFeedPost topPost = feedPostAdapter.getTopElement();

            if (direction == Direction.Left) {
                //todo
//                like(topPost.source_id, topPost.post_id);
            } else {
//                skip(topPost.source_id, topPost.post_id);
            }
            if (manager.getTopPosition() >= feedPostAdapter.getItemCount() - VISIBLE_TRESHOLD) {
                //feedPostAdapter.removeSwiped(manager.getTopPosition());
                //manager.scrollToPosition(0);
                fetchNextFeed();
            }
        }

        @Override
        public void onCardRewound() {/*NOP*/}

        @Override
        public void onCardCanceled() {/*NOP*/}
    };

    private SwipeAnimationSetting swipeLeftSetting = new SwipeAnimationSetting.Builder()
            .setDirection(Direction.Left)
            .setDuration(200)
            .setInterpolator(new AccelerateInterpolator())
            .build();

    private SwipeAnimationSetting swipeRightSetting = new SwipeAnimationSetting.Builder()
            .setDirection(Direction.Right)
            .setDuration(200)
            .setInterpolator(new AccelerateInterpolator())
            .build();

    public static void changeStatusBarColor(Activity act, int colorRes) {
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        act.getWindow().setStatusBarColor(ContextCompat.getColor(act, colorRes));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeStatusBarColor(this, R.color.gray);

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

        View fabSkip = findViewById(R.id.fab_skip);
        View fabLike = findViewById(R.id.fab_like);
        fabSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setSwipeAnimationSetting(swipeLeftSetting);
                feedContainer.swipe();
            }
        });

        fabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setSwipeAnimationSetting(swipeRightSetting);
                feedContainer.swipe();
            }
        });
        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(this, VKScope.WALL);
        } else {
            fetchFeed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                fetchFeed();
            }

            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void like(VkFeedPost feedPost) {
        VKParameters parameters = VKParameters.from(
                "type", "post",
                "owner_id", feedPost.source_id,
                "item_id", feedPost.post_id);

        VKRequest request = new VKRequest("likes.add",
                parameters);


        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                System.out.println("====" + response.responseString);
                Toast.makeText(MainActivity.this, "Like", Toast.LENGTH_SHORT).show();
                System.out.println();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

//Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

//I don't really believe in progress
            }
        });
    }

    private void skip(VkFeedPost feedPost) {
        VKParameters parameters = VKParameters.from(
                "type", "wall",
                "owner_id", feedPost.source_id,
                "item_id", feedPost.post_id);

        VKRequest request = new VKRequest("newsfeed.ignoreItem",
                parameters);


        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                System.out.println("====" + response.responseString);
                Toast.makeText(MainActivity.this, "Dislike", Toast.LENGTH_SHORT).show();

                System.out.println();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

//Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

//I don't really believe in progress
            }
        });
    }

    private void fetchFeed() {

        VKParameters parameters = VKParameters.from(
                VKApiConst.EXTENDED, 1,
                VKApiConst.COUNT, 30,
                VKApiConst.FIELDS, "photo_100");

        VKRequest request = new VKRequest("newsfeed.getDiscoverForContestant",
                parameters);

        executeFetchRequest(request);

    }

    private void fetchNextFeed() {

        VKParameters parameters = VKParameters.from(
                VKApiConst.EXTENDED, 1,
                VKApiConst.COUNT, 30,
                "start_from", nextPage,
                VKApiConst.FIELDS, "photo_100");

        VKRequest request = new VKRequest("newsfeed.getDiscoverForContestant",
                parameters);

        executeFetchRequest(request);

    }

    private void executeFetchRequest(VKRequest request) {
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                List<VkFeedPost> feedPosts = FeedPostsParser.parseData(response.json);
                feedPostAdapter.updateData(feedPosts);
                nextPage = FeedPostsParser.parseNextPage(response.json);

            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

//Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_SHORT).show();

//I don't really believe in progress
            }
        });
    }
}

