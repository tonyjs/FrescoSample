package com.tonyjs.frescosample;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by tonyjs on 15. 12. 3..
 */
public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Fresco.initialize(this);
    }
}
