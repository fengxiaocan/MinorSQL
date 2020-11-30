package com.app.thread;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.SoftReference;

public abstract class Executor {
    private static SoftReference<Handler> reference;
    protected Runnable runTask;

    public static void handler(Runnable runnable) {
        if (reference == null || reference.get() == null) {
            reference = new SoftReference<>(new Handler(Looper.getMainLooper()));
        }
        reference.get().post(runnable);
    }

    public void execute() {
        new Thread(runTask).start();
    }
}
