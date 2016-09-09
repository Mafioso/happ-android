package com.happ.retrofit;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.CitiesResponse;
import com.happ.models.City;
import com.happ.models.Event;
import com.happ.models.EventsResponse;
import com.happ.models.HappToken;
import com.happ.models.Interest;
import com.happ.models.InterestResponse;
import com.happ.models.LoginData;
import com.happ.models.SignUpData;
import com.happ.models.User;
import com.happ.retrofit.serializers.InterestDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.realm.Realm;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dante on 8/1/16.
 */
public class HappRestClient {

    private Gson gson;
    private Retrofit retrofit;
    private HAPPapi happApi;
    private OkHttpClient httpClient;
    private Interceptor authHeaderInterceptor;

    private HttpLoggingInterceptor httpLoggingInterceptor;
    private GsonConverterFactory gsonConverterFactory;
    private String host = App.getContext().getString(R.string.HOST);
    private String api = App.getContext().getString(R.string.API_URL);

    private static HappRestClient instance;

    public static synchronized HappRestClient getInstance() {
        if (instance == null) {
            instance = new HappRestClient();
        }
        return instance;
    }

    private HappRestClient() {
        buildClient(null);
    }

    private void buildClient(List<Interceptor> httpInterceptors) {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Interest.class, new InterestDeserializer())
                    .create();
            gsonConverterFactory = GsonConverterFactory.create(gson);
        }
        if (httpLoggingInterceptor == null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
//                .addInterceptor(new LocalResponseInterceptor(App.getContext()))
                .addInterceptor(httpLoggingInterceptor);

        if (httpInterceptors != null) {
            for (int i = 0; i < httpInterceptors.size(); i++) {
                builder.addInterceptor(httpInterceptors.get(i));
            }
        }
        httpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(host+api)
                .addConverterFactory(gsonConverterFactory)
                .client(httpClient)
                .build();

        happApi = retrofit.create(HAPPapi.class);
//        setAuthHeader();
    }

    public boolean setAuthHeader() {
        boolean successfullyAddedHeader = false;
        Realm realm = Realm.getDefaultInstance();
        try {
            HappToken token = realm.where(HappToken.class).findFirst();

            int i = token.getToken().lastIndexOf('.');
            String unsignedToken = token.getToken().substring(0,i+1);
            Jwt<Header,Claims> tokenData = Jwts.parser().parseClaimsJwt(unsignedToken);
            Date now = new Date();
            Date expiration = tokenData.getBody().getExpiration();
            if (expiration.before(now)) {
                realm.beginTransaction();
                token.deleteFromRealm();
                realm.commitTransaction();
                removeAuthHeader();
                throw new Exception();
            }

            final String authString = "JWT " + token.getToken();
            removeAuthHeader();

            authHeaderInterceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();

                    Request.Builder requestBuilder = originalRequest.newBuilder()
                            .header("Authorization", authString);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            };
            List<Interceptor> interceptors = new ArrayList<Interceptor>();
            interceptors.add(authHeaderInterceptor);
            buildClient(interceptors);
            successfullyAddedHeader = true;
        } catch (Exception ex) {
            Log.e("HAPP_API", ex.getLocalizedMessage());
        } finally {
            realm.close();
            return successfullyAddedHeader;
        }
    }

    public boolean removeAuthHeader() {
        if (authHeaderInterceptor != null && httpClient.interceptors().contains(authHeaderInterceptor)) {
            authHeaderInterceptor = null;
            buildClient(null);
            return true;
        }
        return false;
    }

    public void refreshToken() {
        final Realm realm = Realm.getDefaultInstance();
        try {
            HappToken token = realm.where(HappToken.class).findFirst();

            int i = token.getToken().lastIndexOf('.');
            String unsignedToken = token.getToken().substring(0,i+1);
            Jwt<Header,Claims> tokenData = Jwts.parser().parseClaimsJwt(unsignedToken);

            Date now = new Date();
            Date exp = tokenData.getBody().getExpiration();
            if (exp.before(now)) {
                realm.beginTransaction();
                token.deleteFromRealm();
                realm.commitTransaction();
                removeAuthHeader();
                throw new Exception();
            }

            final long millisInWeek = 7*24*60*60*1000l;
            if (now.getTime() + millisInWeek > exp.getTime()) {
                happApi.refreshToken(token).enqueue(new Callback<HappToken>() {
                    @Override
                    public void onResponse(Call<HappToken> call, Response<HappToken> response) {
                        if (response.isSuccessful()) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(response.body());
                            realm.commitTransaction();
                            setAuthHeader();
                        }
                    }

                    @Override
                    public void onFailure(Call<HappToken> call, Throwable t) {

                    }
                });
            } else {
                setAuthHeader();
            }


        } catch (Exception ex) {

        } finally {
            realm.close();
        }
    }

    public void getEvents() {
        this.getEvents(1);
    }

    public void getEvents(int page) {
        happApi.getEvents(page).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {

                if (response.isSuccessful()){
                    List<Event> events = response.body().getEvents();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(events);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getCities(int page, String searchText) {
        happApi.getCities(page, searchText).enqueue(new Callback<CitiesResponse>() {
            @Override
            public void onResponse(Call<CitiesResponse> call, Response<CitiesResponse> response) {

                if (response.isSuccessful()){
                    List<City> citys = response.body().getCities();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(citys);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.CITY_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.CITY_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<CitiesResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.CITY_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doLogin(String username, String password) {

        LoginData loginData = new LoginData();
        loginData.setUsername(username);
        loginData.setPassword(password);

        happApi.doLogin(loginData).enqueue(new Callback<HappToken>() {
            @Override
            public void onResponse(Call<HappToken> call, Response<HappToken> response) {

                Log.d("HAPP_API", String.valueOf(response.code()));
                Log.d("HAPP_API", response.message());

                if(response.isSuccessful()) {

                    HappToken happToken = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(happToken);

                    realm.commitTransaction();
                    realm.close();

                    setAuthHeader();

                    Intent intent = new Intent(BroadcastIntents.LOGIN_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
                else {
                    Intent intent = new Intent(BroadcastIntents.LOGIN_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<HappToken> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.LOGIN_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doSignUp(String username, String password) {
        SignUpData signUpData = new SignUpData();
        signUpData.setUsername(username);
        signUpData.setPassword(password);

        happApi.doSignUp(signUpData).enqueue(new Callback<HappToken>() {
            @Override
            public void onResponse(Call<HappToken> call, Response<HappToken> response) {

                if(response.isSuccessful()) {

                    HappToken happToken = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(happToken);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.SIGNUP_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
                else {
                    Intent intent = new Intent(BroadcastIntents.SIGNUP_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<HappToken> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SIGNUP_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getInterests() {
        getInterests(1);
    }

    public void getInterests(int page) {
        happApi.getInterests(page).enqueue(new Callback<InterestResponse>() {
            @Override
            public void onResponse(Call<InterestResponse> call, Response<InterestResponse> response) {
                if (response.isSuccessful()){
                    List<Interest> interests = response.body().getInterests();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(interests);
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<InterestResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void setInterests(ArrayList<String> ids) {
        happApi.setInterests(ids).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void setCity(String cityId) {

        happApi.setCity(cityId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()){
                    Intent intent = new Intent(BroadcastIntents.SET_CITIES_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.SET_CITIES_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SET_CITIES_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getCurrentUser() {
        happApi.getCurrentUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("HAPP_API", String.valueOf(response.code()));
                Log.d("HAPP_API", response.message());

                if (response.isSuccessful()) {
                    User user = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }


    public void getUser(String username) {


        happApi.getUser(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()){
                    User user = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(user);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.USER_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.USER_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.USER_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getCurrentCity() {
        User user = App.getCurrentUser();
        happApi.getCity(user.getSettings().getCity()).enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                if (response.isSuccessful()) {
                    City city = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(city);
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.CITY_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {

            }
        });
    }


}
