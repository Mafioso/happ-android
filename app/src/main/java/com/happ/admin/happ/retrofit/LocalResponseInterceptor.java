package com.happ.admin.happ.retrofit;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;


public class LocalResponseInterceptor implements Interceptor {

    private Context context;

    private String scenario = null;

    public LocalResponseInterceptor(Context ctx) {
        this.context = ctx;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        URL requestedUrl = request.url().url();
        String requestedMethod = request.method();

        String prefix = "";
        if (this.scenario != null) {
            prefix = scenario + "_";
        }

        String fileName = (prefix + requestedMethod + requestedUrl.getPath()).replace("/", "_");
        fileName = fileName.toLowerCase();

        int resourceId = context.getResources().getIdentifier(fileName, "raw",
                context.getPackageName());

        if (resourceId == 0) {
            Log.wtf("YourTag", "Could not find res/raw/" + fileName + ".json");
            throw new IOException("Could not find res/raw/" + fileName + ".json");
        }

        InputStream inputStream = context.getResources().openRawResource(resourceId);

        String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        if (mimeType == null) {
            mimeType = "application/json";
        }

        Buffer input = new Buffer().readFrom(inputStream);

        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(ResponseBody.create(MediaType.parse(mimeType), input.size(), input))
                .build();
    }
}
