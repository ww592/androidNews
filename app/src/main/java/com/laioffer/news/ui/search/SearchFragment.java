package com.laioffer.news.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.news.R;
import com.laioffer.news.databinding.FragmentSearchBinding;
import com.laioffer.news.repository.NewsRepository;
import com.laioffer.news.repository.NewsViewModelFactory;

public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private FragmentSearchBinding binding;

    public SearchFragment() {
        //  constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchNewsAdapter newsAdapter = new SearchNewsAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
        });

        binding.newsResultsRecyclerView.setLayoutManager(gridLayoutManager);
        binding.newsResultsRecyclerView.setAdapter(newsAdapter);
        newsAdapter.setItemCallback(article -> {
            SearchFragmentDirections.ActionNavigationSearchToNavigationDetails
                    direction = SearchFragmentDirections.actionNavigationSearchToNavigationDetails(article);
            NavHostFragment.findNavController(SearchFragment.this).navigate(direction);
        });

        binding.newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.setSearchInput(query);
                }
                binding.newsSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        NewsRepository repository = new NewsRepository(getContext());
        // Provide cache
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(SearchViewModel.class);
        viewModel
                .searchNews()
                .observe(
                        getViewLifecycleOwner(),
                        newsResponse -> {
                            if (newsResponse != null) {
                                Log.d("SearchFragment", newsResponse.toString());
                                newsAdapter.setArticles(newsResponse.articles);
                            }
                        });
    }
}