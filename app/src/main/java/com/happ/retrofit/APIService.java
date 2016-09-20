package com.happ.retrofit;

import android.app.IntentService;
import android.content.Intent;
import android.os.Process;

import com.happ.App;

import java.util.ArrayList;

/**
 * Created by dante on 8/2/16.
 */
public class APIService extends IntentService {
    private static final String ACTION_GET_EVENTS = "com.happ.action.ACTION_GET_EVENTS";
    private static final String ACTION_GET_EVENTS_FOR_PAGE = "com.happ.action.ACTION_GET_EVENTS_FOR_PAGE";
    private static final String ACTION_GET_CITIES = "com.happ.action.ACTION_GET_CITIES";
    private static final String ACTION_GET_INTERESTS = "com.happ.action.ACTION_GET_INTERESTS";
    private static final String ACTION_GET_INTERESTS_FOR_PAGE = "com.happ.action.ACTION_GET_INTERESTS_FOR_PAGE";
    private static final String ACTION_POST_LOGIN = "com.happ.action.ACTION_POST_LOGIN";
    private static final String ACTION_POST_SIGNUP = "com.happ.action.ACTION_POST_SIGNUP";
    private static final String ACTION_GET_USER = "com.happ.action.ACTION_GET_USER";
    private static final String ACTION_GET_CURRENT_USER = "com.happ.action.ACTION_GET_CURRENT_USER";
    private static final String ACTION_GET_CURRENT_CITY = "com.happ.action.ACTION_GET_CURRENT_CITY";
    private static final String ACTION_SET_INTERESTS = "com.happ.action.ACTION_SET_INTERESTS";
    private static final String ACTION_SET_CITIES = "com.happ.action.ACTION_SET_CITIES";



    private static final String EXTRA_PAGE = "com.happ.extra.EXTRA_PAGE";
    private static final String EXTRA_GET_FAVS = "com.happ.extra.EXTRA_GET_FAVS";
    private static final String EXTRA_USERNAME = "com.happ.extra.EXTRA_USERNAME";
    private static final String EXTRA_PASSWORD = "com.happ.extra.EXTRA_PASSWORD";
    private static final String EXTRA_SET_INTERESTS = "com.happ.extra.EXTRA_SET_INTERESTS";
    private static final String EXTRA_SET_CITIES = "com.happ.extra.EXTRA_SET_CITIES";
    private static final String EXTRA_SEARCH_TEXT = "com.happ.extra.EXTRA_SEARCH_TEXT";

    public static void getEvents(boolean favs) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_EVENTS);
        intent.putExtra(EXTRA_GET_FAVS, favs);
        App.getContext().startService(intent);
    }

    public static void getEvents(int page, boolean favs) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_EVENTS_FOR_PAGE);
        intent.putExtra(EXTRA_PAGE, page);
        intent.putExtra(EXTRA_GET_FAVS, favs);
        App.getContext().startService(intent);
    }

    public static void getCities() {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CITIES);
        App.getContext().startService(intent);
    }

    public static void getCities(String searchText) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CITIES);
        intent.putExtra(EXTRA_SEARCH_TEXT, searchText);
        App.getContext().startService(intent);
    }

    public static void getCities(int page, String searchText) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CITIES);
        intent.putExtra(EXTRA_SEARCH_TEXT, searchText);
        intent.putExtra(EXTRA_PAGE, page);
        App.getContext().startService(intent);
    }

    public static void getCities(int page) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CITIES);
        intent.putExtra(EXTRA_PAGE, page);
        App.getContext().startService(intent);
    }

    public static void getInterests() {
        Intent interests = new Intent(App.getContext(), APIService.class);
        interests.setAction(ACTION_GET_INTERESTS);
        App.getContext().startService(interests);
    }

    public static void getInterests(int page) {
        Intent interests = new Intent(App.getContext(), APIService.class);
        interests.setAction(ACTION_GET_INTERESTS_FOR_PAGE);
        interests.putExtra(EXTRA_PAGE, page);
        App.getContext().startService(interests);
    }

    public static void getUser(String username) {
        Intent user = new Intent(App.getContext(), APIService.class);
        user.setAction(ACTION_GET_USER);
        user.putExtra(EXTRA_USERNAME, username);
        App.getContext().startService(user);
    }

    public static void setInterests(ArrayList<String> data) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_SET_INTERESTS);
        intent.putStringArrayListExtra(EXTRA_SET_INTERESTS, data);
        App.getContext().startService(intent);
    }

    public static void setCity(String id) {
        Intent city = new Intent(App.getContext(), APIService.class);
        city.setAction(ACTION_SET_CITIES);
        city.putExtra(EXTRA_SET_CITIES, id);
        App.getContext().startService(city);
    }

    public static void doLogin(String username, String password) {
        Intent login = new Intent(App.getContext(), APIService.class);
        login.setAction(ACTION_POST_LOGIN);
        login.putExtra(EXTRA_USERNAME, username);
        login.putExtra(EXTRA_PASSWORD, password);
        App.getContext().startService(login);
    }

    public static void doSignUp (String username, String password) {
        Intent signUp = new Intent(App.getContext(), APIService.class);
        signUp.setAction(ACTION_POST_SIGNUP);
        signUp.putExtra(EXTRA_USERNAME, username);
        signUp.putExtra(EXTRA_PASSWORD, password);
        App.getContext().startService(signUp);
    }

    public static void getCurrentUser() {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CURRENT_USER);
        App.getContext().startService(intent);
    }

    public static void getCurrentCity() {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CURRENT_CITY);
        App.getContext().startService(intent);
    }

    public APIService() {
        super("APIService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_GET_EVENTS)) {
                boolean favs = intent.getBooleanExtra(EXTRA_GET_FAVS, false);
                HappRestClient.getInstance().getEvents(favs);
            } else if (action.equals(ACTION_GET_EVENTS_FOR_PAGE)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                boolean favs = intent.getBooleanExtra(EXTRA_GET_FAVS, false);
                HappRestClient.getInstance().getEvents(page, favs);
            } else if (action.equals(ACTION_GET_CITIES)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                String searchText = intent.getStringExtra(EXTRA_SEARCH_TEXT);
                HappRestClient.getInstance().getCities(page, searchText);
            } else if (action.equals(ACTION_GET_INTERESTS)) {
                HappRestClient.getInstance().getInterests();
            } else if (action.equals(ACTION_POST_LOGIN)) {
                String username = intent.getStringExtra(EXTRA_USERNAME);
                String password = intent.getStringExtra(EXTRA_PASSWORD);
                HappRestClient.getInstance().doLogin(username, password);
            } else if (action.equals(ACTION_POST_SIGNUP)) {
                String username = intent.getStringExtra(EXTRA_USERNAME);
                String password = intent.getStringExtra(EXTRA_PASSWORD);
                HappRestClient.getInstance().doSignUp(username, password);
            }else if (action.equals(ACTION_GET_USER)) {
                String username = intent.getStringExtra(EXTRA_USERNAME);
                HappRestClient.getInstance().getUser(username);
            } else if (action.equals(ACTION_GET_CURRENT_USER)) {
                HappRestClient.getInstance().getCurrentUser();
            } else if (action.equals(ACTION_GET_CURRENT_CITY)) {
                HappRestClient.getInstance().getCurrentCity();
            } else if (action.equals(ACTION_SET_INTERESTS)) {
                ArrayList<String> data = intent.getStringArrayListExtra(EXTRA_SET_INTERESTS);
                HappRestClient.getInstance().setInterests(data);
            } else if (action.equals(ACTION_SET_CITIES)) {
                String cityId = intent.getStringExtra(EXTRA_SET_CITIES);
                HappRestClient.getInstance().setCity(cityId);
            } else if (action.equals(ACTION_GET_INTERESTS_FOR_PAGE)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                HappRestClient.getInstance().getInterests(page);
            }
        }
    }

}
