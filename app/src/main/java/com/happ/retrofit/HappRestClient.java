package com.happ.retrofit;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happ.App;
import com.happ.BroadcastIntents;
import com.happ.R;
import com.happ.models.ChangePwData;
import com.happ.models.CitiesResponse;
import com.happ.models.City;
import com.happ.models.Currency;
import com.happ.models.CurrencyResponse;
import com.happ.models.Event;
import com.happ.models.EventPhones;
import com.happ.models.EventsResponse;
import com.happ.models.Geopoints;
import com.happ.models.HappToken;
import com.happ.models.Interest;
import com.happ.models.InterestResponse;
import com.happ.models.LoginData;
import com.happ.models.SignUpData;
import com.happ.models.User;
import com.happ.models.UserEditData;
import com.happ.retrofit.serializers.DateDeserializer;
import com.happ.retrofit.serializers.GeopointDeserializer;
import com.happ.retrofit.serializers.InterestDeserializer;
import com.happ.retrofit.serializers.PhoneDeserializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .registerTypeAdapter(EventPhones.class, new PhoneDeserializer())
                    .registerTypeAdapter(Geopoints.class, new GeopointDeserializer())
                    .create();
            gsonConverterFactory = GsonConverterFactory.create(gson);
        }
        if (httpLoggingInterceptor == null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
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

    public void getEvents(boolean favs) {
        this.getEvents(1, favs);
    }

    public void getEvents(int page, boolean favs) {
        Call<EventsResponse> getEventsResponse;
        Date date = new Date();
        DateTimeFormatter dtFormatter = DateTimeFormat.forPattern("yyyyMMdd");
        DateTime eventDate = new DateTime(date);
        String today = eventDate.toString(dtFormatter);
        if (favs) {
            getEventsResponse = this.happApi.getFavourites(page, today);
        } else {
            getEventsResponse = this.happApi.getEvents(page, today);
        }
        getEventsResponse.enqueue(new Callback<EventsResponse>() {
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
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENTS_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<CitiesResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.CITY_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<HappToken> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.LOGIN_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doChangePassword(String oldPassword, String newPassword) {

        ChangePwData changePwData = new ChangePwData();
        changePwData.setOldPassword(oldPassword);
        changePwData.setNewPssword(newPassword);

        happApi.doChangePassword(changePwData).enqueue(new Callback<HappToken>() {
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

                    Intent intent = new Intent(BroadcastIntents.CHANGE_PW_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
                else {
                    Intent intent = new Intent(BroadcastIntents.CHANGE_PW_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<HappToken> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.CHANGE_PW_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doUserEdit(String edit_fullname, String edit_email, String edit_phone, Date edit_dateofbirth, int gender) {

        UserEditData userEditData = new UserEditData();
        userEditData.setFullname(edit_fullname);
        userEditData.setEmail(edit_email);
        userEditData.setPhone(edit_phone);
        userEditData.setDate_of_birth(edit_dateofbirth);
        userEditData.setGender(gender);

        happApi.doUserEdit(userEditData).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Log.d("HAPP_API", String.valueOf(response.code()));
                Log.d("HAPP_API", response.message());

                if(response.isSuccessful()) {

                    User user = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.USEREDIT_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
                else {
                    Intent intent = new Intent(BroadcastIntents.USEREDIT_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.USEREDIT_REQUEST_FAIL);

                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doEventEdit(final String eventId) {
        Realm realm = Realm.getDefaultInstance();
        Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

        if (event != null) {
            event.setId(event.getLocalId());
            ArrayList<String> interest_ids = new ArrayList<>();
            for (Interest interest: event.getInterests()
                    ) {
                interest_ids.add(interest.getId());
            }
            event.setInterestIds(interest_ids);
            happApi.doEventEdit(event.getId(), event).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {

                    Log.d("HAPP_API", String.valueOf(response.code()));
                    Log.d("HAPP_API", response.message());

                    if (response.isSuccessful()) {

                        Event event = response.body();

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        realm.copyToRealmOrUpdate(event);

                        realm.commitTransaction();
                        realm.close();

                        Intent intent = new Intent(BroadcastIntents.EVENTEDIT_REQUEST_OK);
                        intent.putExtra("event_id", event.getId());

                        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(BroadcastIntents.EVENTEDIT_REQUEST_FAIL);
                        intent.putExtra("CODE", response.code());
                        showRequestError(response);
                        intent.putExtra("MESSAGE", response.message());
                        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                    }

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Event oldEvent = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    oldEvent.deleteFromRealm();
                    realm.commitTransaction();
                    realm.close();
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Event oldEvent = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    oldEvent.deleteFromRealm();
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.EVENTEDIT_REQUEST_FAIL);
                    Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    intent.putExtra("MESSAGE", t.getLocalizedMessage());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            });

        }
    }

    public void doUpVote(final String eventId) {
        EmptyBody body = new EmptyBody();
        body.setEmpty("empty");
        happApi.doUpVote(eventId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    if (event != null) {
                        realm.beginTransaction();
                        event.setDidVote(true);
                        event.setVotesCount(event.getVotesCount()+1);
                        realm.copyToRealmOrUpdate(event);
                        realm.commitTransaction();
                    }

                    Intent intent = new Intent(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK);
                    intent.putExtra(BroadcastIntents.EXTRA_DID_UPVOTE, 1);
                    intent.putExtra(BroadcastIntents.EXTRA_EVENT_ID, eventId);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BroadcastIntents.EVENT_UPVOTE_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENT_UPVOTE_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doDownVote(final String eventId) {

        EmptyBody body = new EmptyBody();
        body.setEmpty("empty");
        happApi.doDownVote(eventId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    if (event != null) {
                        realm.beginTransaction();
                        event.setDidVote(false);
                        event.setVotesCount(event.getVotesCount() - 1);
                        realm.copyToRealmOrUpdate(event);
                        realm.commitTransaction();
                    }

                    Intent intent = new Intent(BroadcastIntents.EVENT_UPVOTE_REQUEST_OK);
                    intent.putExtra(BroadcastIntents.EXTRA_DID_UPVOTE, 0);
                    intent.putExtra(BroadcastIntents.EXTRA_EVENT_ID, eventId);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BroadcastIntents.EVENT_UPVOTE_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENT_UPVOTE_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }


    public void doUnFav(final String eventId) {

        EmptyBody body = new EmptyBody();
        body.setEmpty("empty");
        happApi.doUnFav(eventId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    if (event != null) {
                        realm.beginTransaction();
                        event.setInFavorites(false);
                        realm.copyToRealmOrUpdate(event);
                        realm.commitTransaction();
                    }

                    Intent intent = new Intent(BroadcastIntents.EVENT_UNFAV_REQUEST_OK);
                    intent.putExtra(BroadcastIntents.EXTRA_DID_FAV, 0);
                    intent.putExtra(BroadcastIntents.EXTRA_EVENT_ID, eventId);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BroadcastIntents.EVENT_UNFAV_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("BODY", "AHAHAHHAAHHHAHAHHAHAHAHAA");
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENT_UNFAV_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void doFav(final String eventId) {

        EmptyBody body = new EmptyBody();
        body.setEmpty("empty");
        happApi.doFav(eventId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    if (event != null) {
                        realm.beginTransaction();
                        event.setInFavorites(true);
                        realm.copyToRealmOrUpdate(event);
                        realm.commitTransaction();
                    }

                    Intent intent = new Intent(BroadcastIntents.EVENT_UNFAV_REQUEST_OK);
                    intent.putExtra(BroadcastIntents.EXTRA_DID_FAV, 1);
                    intent.putExtra(BroadcastIntents.EXTRA_EVENT_ID, eventId);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BroadcastIntents.EVENT_UNFAV_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENT_UNFAV_REQUEST_FAIL);
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void createEvent(final String eventId) {
        Realm realm = Realm.getDefaultInstance();
        Event event = realm.where(Event.class).equalTo("id", eventId).findFirst();
        event = realm.copyFromRealm(event);
        realm.close();

        if (event != null) {
            ArrayList<String> interest_ids = new ArrayList<>();
            for (Interest interest: event.getInterests()
                    ) {
                interest_ids.add(interest.getId());
            }
            event.setInterestIds(interest_ids);
            happApi.createEvent(event).enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {

                    Log.d("HAPP_API", String.valueOf(response.code()));
                    Log.d("HAPP_API", response.message());

                    if (response.isSuccessful()) {

                        Event event = response.body();

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        realm.copyToRealmOrUpdate(event);

                        realm.commitTransaction();
                        realm.close();

                        Intent intent = new Intent(BroadcastIntents.EVENTCREATE_REQUEST_OK);
                        String eventId = event.getId();
                        intent.putExtra("event_id", eventId);
                        intent.putExtra("in_event_activity", true);
                        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent(BroadcastIntents.EVENTCREATE_REQUEST_FAIL);
                        intent.putExtra("CODE", response.code());
                        showRequestError(response);
                        intent.putExtra("MESSAGE", response.message());
                        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                    }

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Event oldEvent = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    oldEvent.deleteFromRealm();
                    realm.commitTransaction();
                    realm.close();
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Event oldEvent = realm.where(Event.class).equalTo("id", eventId).findFirst();
                    oldEvent.deleteFromRealm();
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.EVENTCREATE_REQUEST_FAIL);
                    Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    intent.putExtra("MESSAGE", t.getLocalizedMessage());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            });

        }
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

                    setAuthHeader();

                    Intent intent = new Intent(BroadcastIntents.SIGNUP_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
                else {
                    Intent intent = new Intent(BroadcastIntents.SIGNUP_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<HappToken> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SIGNUP_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getFilteredEvents(int page, String feedSearchText, String startDate, String endDate, String maxPrice, boolean favs) {

        if (favs) {
            getFilteredFavs(page, feedSearchText, startDate, endDate, maxPrice);
            return;
        }

        happApi.getFilteredEvents(page, feedSearchText,  startDate, endDate, maxPrice).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if (response.isSuccessful()){
                    List<Event> fEvents = response.body().getEvents();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(fEvents);
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.FILTERED_EVENTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.FILTERED_EVENTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.FILTERED_EVENTS_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void getFilteredFavs(int page, String feedSearchText, String startDate, String endDate, String maxPrice) {
        happApi.getFilteredFavourites(page, feedSearchText, startDate, endDate, maxPrice).enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if (response.isSuccessful()){
                    List<Event> fEvents = response.body().getEvents();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(fEvents);
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.FILTERED_EVENTS_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.FILTERED_EVENTS_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.FILTERED_EVENTS_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<InterestResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.INTERESTS_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void setAllInterests(int all) {
        happApi.setAllInterests(all).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SET_INTERESTS_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    public void setCity(final String cityId) {

        happApi.setCity(cityId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()){
                    Realm realm = Realm.getDefaultInstance();

                    User user = App.getCurrentUser();
                    user.getSettings().setCity(cityId);

                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(user);

                    realm.commitTransaction();

                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.SET_CITIES_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.SET_CITIES_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.SET_CITIES_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
//                    User prevUser = App.getCurrentUser();
//                    ArrayList<String> previousInterestsIds = prevUser.getInterestIds();
//                    String previousCityId = prevUser.getSettings().getCity();

                    User user = response.body();
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();

                    boolean eventsChanged = false;

//                    if (!previousCityId.equals(user.getSettings().getCity())) {
//                        RealmResults<Event> events = realm.where(Event.class).equalTo("cityId",previousCityId).findAll();
//                        realm.beginTransaction();
//                        events.deleteAllFromRealm();
//                        realm.commitTransaction();
//                        eventsChanged = true;
//                    }
//
//                    ArrayList<String> unsubscriberEvents = new ArrayList<String>();
//                    ArrayList<String> newInterestsList = user.getInterestIds();
//
//                    for (int i = 0; i<previousInterestsIds.size(); i++) {
//                        String interestId = previousInterestsIds.get(i);
//                        if (!newInterestsList.contains(interestId)) {
//                            unsubscriberEvents.add(interestId);
//                        }
//                    }
//
//                    if (unsubscriberEvents.size() > 0) {
//                        RealmResults<Event> events = realm.where(Event.class).findAll();
//                        realm.beginTransaction();
//                        events.deleteAllFromRealm();
//                        realm.commitTransaction();
//                        eventsChanged = true;
//                    }

                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.GET_CURRENT_USER_REQUEST_OK);
                    intent.putExtra("EVENTS_CHANGED", eventsChanged);
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
                    showRequestError(response);
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.USER_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    public void getCurrencies(int page) {
        happApi.getCurrency(page).enqueue(new Callback<CurrencyResponse>() {
            @Override
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                if (response.isSuccessful()){
                    List<Currency> currencies = response.body().getCurrencies();

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();

                    realm.copyToRealmOrUpdate(currencies);

                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.CURRENCY_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.CURRENCY_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.CURRENCY_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }




    public void doEventDelete(final String eventID) {

        happApi.doEventDelete(eventID).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {

                if (response.isSuccessful()){
                    Realm realm = Realm.getDefaultInstance();
                    Event event = realm.where(Event.class).equalTo("id", eventID).findFirst();
                    realm.beginTransaction();
                    event.deleteFromRealm();
                    realm.commitTransaction();
                    realm.close();

                    Intent intent = new Intent(BroadcastIntents.EVENTDELETE_REQUEST_OK);
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);

                }
                else {
                    Intent intent = new Intent(BroadcastIntents.EVENTDELETE_REQUEST_FAIL);
                    intent.putExtra("CODE", response.code());
                    showRequestError(response);
                    intent.putExtra("BODY", response.body().toString());
                    intent.putExtra("MESSAGE", response.message());
                    LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Intent intent = new Intent(BroadcastIntents.EVENTDELETE_REQUEST_FAIL);
                Toast.makeText(App.getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                intent.putExtra("MESSAGE", t.getLocalizedMessage());
                LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
            }
        });
    }

    private void showRequestError(Response response) {
        if (response.code() == 500) {
            Toast.makeText(App.getContext(), R.string.error_500, Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject errObj = new JSONObject(response.errorBody().string());
//                            HashMap<String, String> errors = new HashMap<String, String>();

//                String err_text = App.getContext().getResources().getString(R.string.error)+":";
                String err_text = ";";
                int idx = 0;
                for (Iterator<String> iter = errObj.keys(); iter.hasNext();) {
                    String key = iter.next();
                    JSONArray valArr = errObj.getJSONArray(key);
                    if (valArr.length() > 0) {
                        String value = valArr.getString(0);
                        //                                errors.put(key, value);
                        if (idx == 0) {
                            err_text += value;
                        } else {
                            err_text += " and " + value + " ";
                        }
                        idx++;
                    }
                }

                Toast.makeText(App.getContext(), err_text, Toast.LENGTH_LONG).show();


            } catch (IOException ex) {

            } catch (JSONException e) {
//                        e.printStackTrace();
            }
        }
    }

}
