package com.happ;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by iztiev on 10/20/16.
 */

public class ImageLoadGlideModule  implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.connectTimeout(20, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
        glide.register(GlideUrl.class, InputStream.class, factory);
    }
}