package com.go4lunch2.ui.detail_restaurant;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.data.PlaceDetailsAPI;
import com.go4lunch2.data.PlacesAPI;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Rating;
import com.go4lunch2.data.model.Restaurant;
import com.go4lunch2.data.model.model_gmap.Place;
import com.go4lunch2.data.model.model_gmap.restaurant_details.RestaurantDetailsJson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailRestaurantViewModel extends ViewModel {

    Repository repository;
    MutableLiveData<DetailRestaurantViewState> restaurantSelectedLiveData = new MutableLiveData<>();
    Context ctx = MyApplication.getInstance();

    public DetailRestaurantViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<DetailRestaurantViewState> getDetailRestaurantLiveData(String idRestaurant) {

        DetailRestaurantViewState detail = null;
        Restaurant r = repository.getRestaurantById(idRestaurant);

        if (r != null) {

            detail = new DetailRestaurantViewState(
                    r.getId(),
                    r.getName(),
                    r.getType(),
                    r.getAdress(),
                    r.getOpeningTime(),
                    "100 m",
                    Utils.ratingToStars(r.getRcf().getAverageRate()),
                    repository.getListWorkmatesByIds(r.getRcf().getWorkmatesInterestedIds()),
                    r.getImage(),
                    r.getRestaurantDetails().getPhone(),
                    r.getRestaurantDetails().getWebsite()
            );

            if (r.getRestaurantDetails().getPhone()== null) getDetailsFromAPI(detail);
        }
        restaurantSelectedLiveData.setValue(detail);
        return restaurantSelectedLiveData;
    }

    public void getDetailsFromAPI (DetailRestaurantViewState r) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        PlaceDetailsAPI service = retrofit.create(PlaceDetailsAPI.class);
        Call<RestaurantDetailsJson> callAsync = service.getResults(r.getId(),
                                                                   ctx.getString(R.string.google_maps_key22));


        callAsync.enqueue(new Callback<RestaurantDetailsJson>() {
            @Override
            public void onResponse(Call<RestaurantDetailsJson> call, Response<RestaurantDetailsJson> response) {
                String phone = response.body().getResult().getInternationalPhoneNumber();
                String website = response.body().getResult().getWebsite();

                DetailRestaurantViewState restaurantViewStateUpdated = new DetailRestaurantViewState(
                        r.getId(),
                        r.getName(),
                        r.getType(),
                        r.getAdress(),
                        r.getOpeningHours(),
                        r.getDistance(),
                        r.starsCount,
                        r.getWorkmatesInterested(),
                        r.getImage(),
                        phone,
                        website
                );
                restaurantSelectedLiveData.setValue(restaurantViewStateUpdated);

            }

            @Override
            public void onFailure(Call<RestaurantDetailsJson> call, Throwable t) {
                System.out.println(t);
            }
        });
    }




    public void addRate (String idWorkmate, String idRestaurant, int givenRate) {
        repository.addGrade(new Rating(idRestaurant, idWorkmate, givenRate));

    }
}