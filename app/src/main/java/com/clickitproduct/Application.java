package com.clickitproduct;

import android.content.Context;
import android.support.multidex.MultiDex;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import ly.img.android.PESDK;

public class Application extends android.app.Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        PESDK.init(this);
        //MultiDex.install(this);
    }

    public Application() {}

    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
