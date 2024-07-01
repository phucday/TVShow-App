package com.example.navigationcomponent.respositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.navigationcomponent.network.APiService;
import com.example.navigationcomponent.network.ApiClient;
import com.example.navigationcomponent.responses.TVShowsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTVShowsRepository {

    private APiService apiService;

    public MostPopularTVShowsRepository(){
        apiService = ApiClient.getRetrofit().create(APiService.class);
    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page){
        MutableLiveData<TVShowsResponse> data = new MutableLiveData<>();
        apiService.getMostPopularTVShows(page).enqueue(new Callback<TVShowsResponse>() {
            @Override
            public void onResponse(Call<TVShowsResponse> call, Response<TVShowsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TVShowsResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
