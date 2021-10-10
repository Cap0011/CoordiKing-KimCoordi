package com.team6.coordiking_kimcoordi.activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.logging.Logger;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

@GlideModule
public class MyGlideApp extends AppGlideModule {
    @Override public void registerComponents(Context context, Glide glide, Registry registry) {
//        Logger.d("");
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
    }



}
