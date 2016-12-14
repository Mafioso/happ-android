package com.happ.retrofit;

import android.app.IntentService;
import android.content.Intent;
import android.os.Process;

import com.happ.App;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by dante on 8/2/16.
 */
public class APIService extends IntentService {
    private static final String ACTION_GET_EVENTS = "com.happ.action.ACTION_GET_EVENTS";
    private static final String ACTION_GET_EVENTS_FOR_PAGE = "com.happ.action.ACTION_GET_EVENTS_FOR_PAGE";
    private static final String ACTION_GET_FILTERED_EVENTS = "com.happ.action.ACTION_GET_FILTERED_EVENTS";
    private static final String ACTION_GET_CITIES = "com.happ.action.ACTION_GET_CITIES";
    private static final String ACTION_GET_INTERESTS = "com.happ.action.ACTION_GET_INTERESTS";
    private static final String ACTION_GET_INTERESTS_FOR_PAGE = "com.happ.action.ACTION_GET_INTERESTS_FOR_PAGE";
    private static final String ACTION_POST_LOGIN = "com.happ.action.ACTION_POST_LOGIN";
    private static final String ACTION_POST_SIGNUP = "com.happ.action.ACTION_POST_SIGNUP";
    private static final String ACTION_POST_CHANGE_PW = "com.happ.action.ACTION_POST_CHANGE_PW";

    private static final String ACTION_GET_USER = "com.happ.action.ACTION_GET_USER";
    private static final String ACTION_GET_CURRENT_USER = "com.happ.action.ACTION_GET_CURRENT_USER";
    private static final String ACTION_GET_CURRENT_CITY = "com.happ.action.ACTION_GET_CURRENT_CITY";
    private static final String ACTION_GET_CURRENCIES = "com.happ.action.ACTION_GET_CURRENCIES";
    private static final String ACTION_SET_INTERESTS = "com.happ.action.ACTION_SET_INTERESTS";
    private static final String ACTION_SET_ALL_INTERESTS = "com.happ.action.ACTION_SET_ALL_INTERESTS";
    private static final String ACTION_SET_CITIES = "com.happ.action.ACTION_SET_CITIES";
    private static final String ACTION_SET_CURRENCY = "com.happ.action.ACTION_SET_CURRENCY";

    private static final String ACTION_POST_USEREDIT = "com.happ.action.ACTION_POST_USEREDIT";
    private static final String ACTION_PATCH_EVENTEDIT = "com.happ.action.ACTION_PATCH_EVENTEDIT";

    private static final String ACTION_DELETE_EVENT = "com.happ.extra.ACTION_DELETE_EVENT";
    private static final String ACTION_UPVOTE_EVENT = "com.happ.extra.ACTION_UPVOTE_EVENT";
    private static final String ACTION_DOWNVOTE_EVENT = "com.happ.extra.ACTION_DOWNVOTE_EVENT";
    private static final String ACTION_UNFAV_EVENT = "com.happ.extra.ACTION_UNFAV_EVENT";
    private static final String ACTION_FAV_EVENT = "com.happ.extra.ACTION_FAV_EVENT";
    private static final String ACTION_PATCH_EVENTCREATE = "com.happ.action.ACTION_PATCH_EVENTCREATE";


    private static final String EXTRA_PAGE = "com.happ.extra.EXTRA_PAGE";
    private static final String EXTRA_GET_FAVS = "com.happ.extra.EXTRA_GET_FAVS";
    private static final String EXTRA_USERNAME = "com.happ.extra.EXTRA_USERNAME";
    private static final String EXTRA_PASSWORD = "com.happ.extra.EXTRA_PASSWORD";

    private static final String EXTRA_OLD_PASSWORD = "com.happ.extra.EXTRA_OLD_PASSWORD";
    private static final String EXTRA_NEW_PASSWORD = "com.happ.extra.EXTRA_NEW_PASSWORD";

    private static final String EXTRA_SET_INTERESTS = "com.happ.extra.EXTRA_SET_INTERESTS";
    private static final String EXTRA_SET_ALL_INTERESTS = "com.happ.extra.EXTRA_SET_ALL_INTERESTS";
    private static final String EXTRA_SET_CITIES = "com.happ.extra.EXTRA_SET_CITIES";
    private static final String EXTRA_SET_CURRENCY = "com.happ.extra.EXTRA_SET_CURRENCY";
    private static final String EXTRA_SEARCH_TEXT = "com.happ.extra.EXTRA_SEARCH_TEXT";
    private static final String EXTRA_EVENT_SEARCH_TEXT = "com.happ.extra.EXTRA_EVENT_SEARCH_TEXT";


    private static final String EXTRA_FULLNAME = "com.happ.extra.EXTRA_FULLNAME";
    private static final String EXTRA_EMAIL = "com.happ.extra.EXTRA_EMAIL";
    private static final String EXTRA_PHONE = "com.happ.extra.EXTRA_PHONE";
    private static final String EXTRA_DATEOFBIRTH = "com.happ.extra.EXTRA_DATEOFBIRTH";
    private static final String EXTRA_GENDER = "com.happ.extra.EXTRA_GENDER";

    private static final String EXTRA_EE_ID = "com.happ.extra.EXTRA_EE_ID";

    private static final String EXTRA_DELETE_EVENT = "com.happ.extra.EXTRA_DELETE_EVENT";

    private static final String EXTRA_UPVOTE_EVENT = "com.happ.extra.EXTRA_UPVOTE_EVENT";
    private static final String EXTRA_DOWNVOTE_EVENT = "com.happ.extra.EXTRA_DOWNVOTE_EVENT";
    private static final String EXTRA_UNFAV_EVENT = "com.happ.extra.EXTRA_UNFAV_EVENT";
    private static final String EXTRA_FAV_EVENT = "com.happ.extra.EXTRA_FAV_EVENT";

    private static final String EXTRA_STARTDATE = "com.happ.extra.EXTRA_STARTDATE";
    private static final String EXTRA_ENDDATE = "com.happ.extra.EXTRA_ENDDATE";
    private static final String EXTRA_FREEEVENTS = "com.happ.extra.EXTRA_FREEEVENTS";
    private static final String EXTRA_POPULARITY_EVENTS = "com.happ.extra.EXTRA_POPULARITY_EVENTS";




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

    public static void getFilteredEvents(int page,
                                         String feedSearchText,
                                         String startDate,
                                         String endDate,
                                         String isFree,
                                         boolean popularity,
                                         boolean favs) {
        Intent i = new Intent(App.getContext(), APIService.class);
        i.setAction(ACTION_GET_FILTERED_EVENTS);
        i.putExtra(EXTRA_PAGE, page);
        i.putExtra(EXTRA_EVENT_SEARCH_TEXT, feedSearchText);
        i.putExtra(EXTRA_STARTDATE, startDate);
        i.putExtra(EXTRA_ENDDATE, endDate);
        i.putExtra(EXTRA_FREEEVENTS, isFree);
        i.putExtra(EXTRA_POPULARITY_EVENTS, popularity);
        i.putExtra(EXTRA_GET_FAVS, favs);
        App.getContext().startService(i);
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

    public static void getEventsBySearch(String searchText) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_EVENTS);
        intent.putExtra(EXTRA_EVENT_SEARCH_TEXT, searchText);
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

    public static void setAllInterests(int all) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_SET_ALL_INTERESTS);
        intent.putExtra(EXTRA_SET_ALL_INTERESTS, all);
        App.getContext().startService(intent);
    }

    public static void setCity(String id) {
        Intent city = new Intent(App.getContext(), APIService.class);
        city.setAction(ACTION_SET_CITIES);
        city.putExtra(EXTRA_SET_CITIES, id);
        App.getContext().startService(city);
    }

    public static void setCurrency(String id) {
        Intent currency = new Intent(App.getContext(), APIService.class);
        currency.setAction(ACTION_SET_CURRENCY);
        currency.putExtra(EXTRA_SET_CURRENCY, id);
        App.getContext().startService(currency);
    }

    public static void doLogin(String username, String password) {
        Intent login = new Intent(App.getContext(), APIService.class);
        login.setAction(ACTION_POST_LOGIN);
        login.putExtra(EXTRA_USERNAME, username);
        login.putExtra(EXTRA_PASSWORD, password);
        App.getContext().startService(login);
    }

    public static void doChangePassword(String oldPassword, String newPassword) {
        Intent changePw = new Intent(App.getContext(), APIService.class);
        changePw.setAction(ACTION_POST_CHANGE_PW);
        changePw.putExtra(EXTRA_OLD_PASSWORD, oldPassword);
        changePw.putExtra(EXTRA_NEW_PASSWORD, newPassword);
        App.getContext().startService(changePw);
    }


    public static void doUserEdit(String edit_fullname, String edit_email, String edit_phone, Date edit_dateofbirth, int gender) {
        Intent userEdit = new Intent(App.getContext(), APIService.class);
        userEdit.setAction(ACTION_POST_USEREDIT);
        userEdit.putExtra(EXTRA_FULLNAME, edit_fullname);
        userEdit.putExtra(EXTRA_EMAIL, edit_email);
        userEdit.putExtra(EXTRA_PHONE, edit_phone);
        userEdit.putExtra(EXTRA_DATEOFBIRTH, edit_dateofbirth.getTime());
        userEdit.putExtra(EXTRA_GENDER, gender);
        App.getContext().startService(userEdit);
    }

    public static void doUpVote(String eventId) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_UPVOTE_EVENT);
        intent.putExtra(EXTRA_UPVOTE_EVENT, eventId);
        App.getContext().startService(intent);
    }

    public static void doDownVote(String eventId) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_DOWNVOTE_EVENT);
        intent.putExtra(EXTRA_DOWNVOTE_EVENT, eventId);
        App.getContext().startService(intent);
    }

    public static void doUnFav(String eventId) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_UNFAV_EVENT);
        intent.putExtra(EXTRA_UNFAV_EVENT, eventId);
        App.getContext().startService(intent);
    }

    public static void doFav(String eventId) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_FAV_EVENT);
        intent.putExtra(EXTRA_FAV_EVENT, eventId);
        App.getContext().startService(intent);
    }

    public static void doEventEdit(String eventId) {
        Intent eventEdit = new Intent(App.getContext(), APIService.class);
        eventEdit.setAction(ACTION_PATCH_EVENTEDIT);
        eventEdit.putExtra(EXTRA_EE_ID, eventId);
        App.getContext().startService(eventEdit);
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

    public static void getCurrencies(int page) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_CURRENCIES);
        intent.putExtra(EXTRA_PAGE, page);
        App.getContext().startService(intent);
    }



    public static void doEventDelete(String eventID) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_DELETE_EVENT);
        intent.putExtra(EXTRA_DELETE_EVENT, eventID);
        App.getContext().startService(intent);
    }

    public static void createEvent(String eventId) {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_PATCH_EVENTCREATE);
        intent.putExtra(EXTRA_EE_ID, eventId);
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
            } else if (action.equals(ACTION_GET_FILTERED_EVENTS)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                String feedSearchText = intent.getStringExtra(EXTRA_EVENT_SEARCH_TEXT);
                String startDate = intent.getStringExtra(EXTRA_STARTDATE);
                String endDate = intent.getStringExtra(EXTRA_ENDDATE);
                String isFree = intent.getStringExtra(EXTRA_FREEEVENTS);
                boolean popularity = intent.getBooleanExtra(EXTRA_POPULARITY_EVENTS, false);
                boolean favs = intent.getBooleanExtra(EXTRA_GET_FAVS, false);
                HappRestClient
                        .getInstance()
                        .getFilteredEvents(page, feedSearchText, startDate, endDate, isFree, popularity, favs);
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
            } else if (action.equals(ACTION_POST_CHANGE_PW)) {
                String oldPassword = intent.getStringExtra(EXTRA_OLD_PASSWORD);
                String newPassword = intent.getStringExtra(EXTRA_NEW_PASSWORD);
                HappRestClient.getInstance().doChangePassword(oldPassword, newPassword);
            } else if (action.equals(ACTION_POST_USEREDIT)) {
                String edit_fullname = intent.getStringExtra(EXTRA_FULLNAME);
                String edit_email = intent.getStringExtra(EXTRA_EMAIL);
                String edit_phone = intent.getStringExtra(EXTRA_PHONE);
                long birthDateLong = intent.getLongExtra(EXTRA_DATEOFBIRTH, 0);
                int gender = intent.getIntExtra(EXTRA_GENDER, 0);
                Date edit_dateofbirth = new Date(birthDateLong);
                HappRestClient.getInstance().doUserEdit(edit_fullname, edit_email, edit_phone, edit_dateofbirth, gender);
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
            } else if (action.equals(ACTION_GET_CURRENCIES)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                HappRestClient.getInstance().getCurrencies(page);
            } else if (action.equals(ACTION_SET_INTERESTS)) {
                ArrayList<String> data = intent.getStringArrayListExtra(EXTRA_SET_INTERESTS);
                HappRestClient.getInstance().setInterests(data);
            } else if (action.equals(ACTION_SET_ALL_INTERESTS)) {
                int all = intent.getIntExtra(EXTRA_SET_ALL_INTERESTS, 1);
                HappRestClient.getInstance().setAllInterests(all);
            } else if (action.equals(ACTION_SET_CITIES)) {
                String cityId = intent.getStringExtra(EXTRA_SET_CITIES);
                HappRestClient.getInstance().setCity(cityId);
            }else if (action.equals(ACTION_SET_CURRENCY)) {
                String currencyId = intent.getStringExtra(EXTRA_SET_CURRENCY);
                HappRestClient.getInstance().setCurrency(currencyId);
            } else if (action.equals(ACTION_GET_INTERESTS_FOR_PAGE)) {
                int page = intent.getIntExtra(EXTRA_PAGE, 1);
                HappRestClient.getInstance().getInterests(page);
            } else if (action.equals(ACTION_PATCH_EVENTEDIT)) {
                String eventId = intent.getStringExtra(EXTRA_EE_ID);
                HappRestClient.getInstance().doEventEdit(eventId);
            } else if (action.equals(ACTION_DELETE_EVENT)) {
                String eventId = intent.getStringExtra(EXTRA_DELETE_EVENT);
                HappRestClient.getInstance().doEventDelete(eventId);
            } else if (action.equals(ACTION_UPVOTE_EVENT)) {
                String eventId = intent.getStringExtra(EXTRA_UPVOTE_EVENT);
                HappRestClient.getInstance().doUpVote(eventId);
            } else if (action.equals(ACTION_DOWNVOTE_EVENT)) {
                String eventId = intent.getStringExtra(EXTRA_DOWNVOTE_EVENT);
                HappRestClient.getInstance().doDownVote(eventId);
            } else if (action.equals(ACTION_PATCH_EVENTCREATE)) {
                String eventId = intent.getStringExtra(EXTRA_EE_ID);
                HappRestClient.getInstance().createEvent(eventId);
            }else if (action.equals(ACTION_FAV_EVENT)) {
                String eventId = intent.getStringExtra(EXTRA_FAV_EVENT);
                HappRestClient.getInstance().doFav(eventId);
            }else if (action.equals(ACTION_UNFAV_EVENT)) {
                String eventId = intent.getStringExtra(EXTRA_UNFAV_EVENT);
                HappRestClient.getInstance().doUnFav(eventId);
            }

        }
    }

}
