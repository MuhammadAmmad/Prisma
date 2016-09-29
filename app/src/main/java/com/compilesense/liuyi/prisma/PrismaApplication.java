package com.compilesense.liuyi.prisma;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by shenjingyuan002 on 16/9/27.
 */

public class PrismaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
