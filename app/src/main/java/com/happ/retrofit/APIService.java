package com.happ.retrofit;

import android.app.IntentService;
import android.content.Intent;
import android.os.Process;

import com.happ.App;

/**
 * Created by dante on 8/2/16.
 */
public class APIService extends IntentService {
    private static final String ACTION_GET_EVENTS = "com.happ.action.ACTION_GET_EVENTS";
    private static final String ACTION_GET_EVENTS_FOR_PAGE = "com.happ.action.ACTION_GET_EVENTS_FOR_PAGE";
    private static final String ACTION_GET_INTERESTS = "com.happ.action.ACTION_GET_INTERESTS";
    private static final String ACTION_POST_LOGIN = "com.happ.action.ACTION_POST_LOGIN";
    private static final String ACTION_POST_SIGNUP = "com.happ.action.ACTION_POST_SIGNUP";
    private static final String ACTION_GET_USER = "com.happ.action.ACTION_GET_USER";
    private static final String ACTION_GET_CURRENT_USER = "com.happ.action.ACTION_GET_CURRENT_USER";
    private static final String ACTION_GET_CURRENT_CITY = "com.happ.action.ACTION_GET_CURRENT_CITY";

    private static final String EXTRA_PAGE = "com.happ.extra.EXTRA_PAGE";
    private static final String EXTRA_USERNAME = "com.happ.extra.EXTRA_USERNAME";
    private static final String EXTRA_PASSWORD = "com.happ.extra.EXTRA_PASSWORD";

    public static void getEvents() {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_EVENTS);
        App.getContext().startService(intent);
    }

    public static void getEvents(int page) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_EVENTS_FOR_PAGE);
        intent.putExtra(EXTRA_PAGE, page);
        App.getContext().startService(intent);
    }

    public static void getInterests() {
        Intent interests = new Intent(App.getContext(), APIService.class);
        interests.setAction(ACTION_GET_INTERESTS);
        App.getContext().startService(interests);
    }

    public static void getUser(String username) {
        Intent user = new Intent(App.getContext(), APIService.class);
        user.setAction(ACTION_GET_USER);
        user.putExtra(EXTRA_USERNAME, username);
        App.getContext().startService(user);
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
                HappRestClient.getInstance().getEvents();
            } else if (action.equals(ACTION_GET_EVENTS_FOR_PAGE)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                HappRestClient.getInstance().getEvents(page);
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
            }
        }
    }

}
