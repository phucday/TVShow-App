package com.example.navigationcomponent.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.navigationcomponent.activities.TVShowDetailsActivity;
import com.example.navigationcomponent.adapters.WatchListAdapter;
import com.example.navigationcomponent.databinding.FragmentFavoriteBinding;
import com.example.navigationcomponent.listeners.WatchListListener;
import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.models.UserTVShow;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.example.navigationcomponent.viewmodels.UserTVShowViewModel;
import com.example.navigationcomponent.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentFavorite extends Fragment implements WatchListListener {

    private FragmentFavoriteBinding binding;

    private WatchListViewModel watchListViewModel;
    private List<TVShow> watchList;
    private WatchListAdapter watchListAdapter;

    private UserTVShowViewModel userTVShowViewModel;
    private PreferenceManager preferenceManager;
    // id of user
    int userId ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        watchListViewModel = new ViewModelProvider(this).get(WatchListViewModel.class);

        // test lây id để lấy data của user đó
        userTVShowViewModel = new ViewModelProvider(requireActivity()).get(UserTVShowViewModel.class);
        preferenceManager = new PreferenceManager(requireActivity().getApplicationContext());

        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // lay id user
        userId = Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID));
        Log.d("userIdFragmentFavorite","ID: "+userId);

        doInitialization();
    }

    private void doInitialization(){
//        watchListViewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        binding.imageBack.setOnClickListener(view -> requireActivity().onBackPressed());
        watchList = new ArrayList<>();
    }

    private void loadWatchList() {
        binding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(userTVShowViewModel.getTVShowForUser(userId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    binding.setIsLoading(false);
                    Log.d("sizeWatchList",tvShows.size()+"");
                    if(watchList.size() > 0){
                        watchList.clear();
                    }
                    getTVShow();
                    watchList.addAll(tvShows);
                    watchListAdapter = new WatchListAdapter(watchList,this);
                    binding.watchListRecyclerView.setAdapter(watchListAdapter);
                    binding.watchListRecyclerView.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();
                })
        );
    }

    private void getTVShow(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(watchListViewModel.loadWatchList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tvShows -> {
                            binding.setIsLoading(false);
                            Log.d("SIzegetTVShow",tvShows.size()+"");
                            compositeDisposable.dispose();
                        })
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        loadWatchList();
    }

    @Override
    public void onResume() {
        super.onResume();
//        loadWatchList();
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(requireActivity().getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchList(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        // UserTVShow data
        UserTVShow userTVShow = new UserTVShow(userId,tvShow.getId());
        compositeDisposable.add(userTVShowViewModel.deleteUserTVShow(userTVShow)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
//                    deleteFilmFireBase(tvShow);
                    watchList.remove(position);
                    watchListAdapter.notifyItemRemoved(position);
                    watchListAdapter.notifyItemRangeChanged(position,watchListAdapter.getItemCount());
                    compositeDisposable.dispose();
                }));
    }
}