package com.example.navigationcomponent.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.RecyclerView;


import com.example.navigationcomponent.activities.SearchActivity;
import com.example.navigationcomponent.activities.SignInActivity;
import com.example.navigationcomponent.activities.TVShowDetailsActivity;
import com.example.navigationcomponent.adapters.TVShowAdapter;
import com.example.navigationcomponent.database.TVShowDatabase;
import com.example.navigationcomponent.databinding.FragmentHomeBinding;
import com.example.navigationcomponent.listeners.TVShowListener;
import com.example.navigationcomponent.models.TVShow;
import com.example.navigationcomponent.models.User;
import com.example.navigationcomponent.models.UserTVShow;
import com.example.navigationcomponent.ultilities.Constants;
import com.example.navigationcomponent.ultilities.PreferenceManager;
import com.example.navigationcomponent.viewmodels.MostPopularTVShowsViewModel;
import com.example.navigationcomponent.viewmodels.UserTVShowViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class FragmentHome extends Fragment implements TVShowListener{

    private FragmentHomeBinding binding;
    private MostPopularTVShowsViewModel viewModel;
    private UserTVShowViewModel userTVShowViewModel;
    private List<TVShow> tvShows = new ArrayList<>();

    private TVShowAdapter tvShowAdapter;

    private  int currentPage = 1;
    private int totalAvailablePage = 1;
    private PreferenceManager preferenceManager;
    private TVShowDatabase db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        userTVShowViewModel = new ViewModelProvider(requireActivity()).get(UserTVShowViewModel.class);
        db = TVShowDatabase.getTvShowDatabase(requireActivity().getApplicationContext());
//        addUserTVShow();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        preferenceManager = new PreferenceManager(requireActivity().getApplicationContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        doInitialization();
        Log.d("FragmentHome_SEND_DATA",preferenceManager.getString("testSendData"));

//        addUserTVShow();
//        deleteAllUser();
        showUser();
        binding.logOut.setOnClickListener(v -> signOut());
    }

    private void showUser() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(db.userDao().getAllUser()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    Log.d("showUser:",users.size()+"");
                    for(User user: users){
                        Log.d("firebaseIdFragmentHome",user.getFireBaseId());
                        Log.d("userPhone:",""+user.getPhoneNumber());
                    }
//                    Log.d("UserIDFragmentHome",preferenceManager.getString(Constants.KEY_USER_ID));
                    compositeDisposable.dispose();
                })
        );
    }

    private void addUserTVShow(){
        User user1 = new User("a","a@gmail.com","1",123123,"firebaseId : 1");
        User user2 = new User("b","b@gmail.com","2",456456,"firebaseId : 2");
        User user3 = new User("c","c@gmail.com","3",789789,"firebaseId : 3");
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(db.userDao().insertUser(user1)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("user1:","ok");
                })
        );

        compositeDisposable.add(db.userDao().insertUser(user2)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("user2:","ok");
                })
        );

        compositeDisposable.add(db.userDao().insertUser(user3)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("user3:","ok");
                })
        );
    }
    private void deleteAllUser(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(db.userDao().deleteAllUser()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d("deleteAllUser:","ok");
                })
        );
    }

    // adapter problem
    private void doInitialization(){
        Log.d("doInitialization","ok");
        binding.tvShowRecyclerView.setHasFixedSize(true);
//        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowAdapter = new TVShowAdapter(tvShows, this);
        binding.tvShowRecyclerView.setAdapter(tvShowAdapter);
        binding.tvShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.tvShowRecyclerView.canScrollVertically(1)){
                    if(currentPage <= totalAvailablePage){
                        currentPage += 1;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        binding.imageSearch.setOnClickListener(view ->
                startActivity(new Intent(requireActivity().getApplicationContext(), SearchActivity.class)));
//        getMostPopularTVShows();

    }


    private void getMostPopularTVShows(){
        Log.d("FragmentHome", "getMostPopularTVShows called for page: " + currentPage);
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(getViewLifecycleOwner(), mostPopularTVShowsResponse -> {
            toggleLoading();
            if(mostPopularTVShowsResponse != null){
                totalAvailablePage = mostPopularTVShowsResponse.getTotalPages();
                Log.d("FragmentHome", "Total pages: " + totalAvailablePage);
                if(mostPopularTVShowsResponse.getTvShows() != null){
                    int oldCount = tvShows.size();
                    tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                    tvShowAdapter.notifyItemRangeInserted(oldCount,tvShows.size());
                }else {
                    Log.d("FragmentHome", "No TV shows received");
                }
            }else {
                Log.d("FragmentHome", "Response is null");
            }
        });
    }
    private void signOut(){
        ShowToast("Signing Out ...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(Constants.KEY_USER_FIREBASE_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(requireActivity(), SignInActivity.class));
                })
                .addOnFailureListener(e -> ShowToast("Unable to sign out"));
    }
    private void ShowToast(String message){
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        getMostPopularTVShows();
    }


    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }

    private void toggleLoading(){
        if(currentPage == 1){
            if(binding.getIsLoading() != null && binding.getIsLoading()){
                binding.setIsLoading(false);
            }else {
                binding.setIsLoading(true);
            }
        }else {
            if(binding.getIsLoadingMore() != null && binding.getIsLoadingMore()){
                binding.setIsLoadingMore(false);
            }else {
                binding.setIsLoadingMore(true);
            }
        }
    }
}