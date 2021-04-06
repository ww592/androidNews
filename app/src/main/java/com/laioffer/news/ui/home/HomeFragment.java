package com.laioffer.news.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.news.R;
import com.laioffer.news.databinding.FragmentHomeBinding;
import com.laioffer.news.model.Article;
import com.laioffer.news.repository.NewsRepository;
import com.laioffer.news.repository.NewsViewModelFactory;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;

public class HomeFragment extends Fragment implements CardStackListener {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    private List<Article> articles;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // CardStackView
        CardSwipeAdapter cardSwipeAdapter = new CardSwipeAdapter();
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        layoutManager.setStackFrom(StackFrom.Bottom);
        binding.homeCardStackView.setLayoutManager(layoutManager);
        binding.homeCardStackView.setAdapter(cardSwipeAdapter);

        // Repository
        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this,new NewsViewModelFactory(repository))
                .get(HomeViewModel.class);
        viewModel.setCountryInput("us");
        viewModel.getTopHeadlines()
                .observe(getViewLifecycleOwner(), newsResponse -> {
                    if (newsResponse != null) {
                        articles = newsResponse.articles;
                        cardSwipeAdapter.setArticles(articles);
                        // Log.d("HomeFragment", newsResponse.toString());
                    }
                });

        // Like / Unlike
        binding.homeLikeButton.setOnClickListener(v -> swipeCard(Direction.Right));
        binding.homeUnlinkButton.setOnClickListener(v -> swipeCard(Direction.Left));
    }

    // Swipe
    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction)
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.swipe();
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());
        } else if (direction == Direction.Right) {
            Article article = articles.get(layoutManager.getTopPosition() - 1);
            viewModel.setFavoriteArticle(article);
            Log.d("CardStackView", "Liked " + layoutManager.getTopPosition());
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}