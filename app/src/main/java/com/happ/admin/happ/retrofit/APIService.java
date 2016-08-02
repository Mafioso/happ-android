package com.happ.admin.happ.retrofit;

import android.app.IntentService;
import android.content.Intent;

import com.happ.admin.happ.App;

/**
 * Created by dante on 8/2/16.
 */
public class APIService extends IntentService{
    private static final String ACTION_GET_EVENTS = "happ.action.ACTION_GET_EVENTS";

    public static void getEvents() {
        Intent intent = new Intent(App.getContext(), APIService.class);
        intent.setAction(ACTION_GET_EVENTS);
        App.getContext().startService(intent);
    }

    public APIService() {
        super("APIService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_GET_EVENTS)) {

                HappRestClient.getInstance().getEvents();
            }
        }
    }

}
