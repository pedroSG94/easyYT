package com.pedro.easyyt.app.executor;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by pedro on 18/07/16.
 */
public class MainThreadExecutorImp implements MainThreadExecutor {

    private Handler handler;

    public MainThreadExecutorImp(){
        handler = new Handler(Looper.getMainLooper());
    }
    @Override
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
