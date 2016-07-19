package com.pedro.easyyt.app.executor;

/**
 * Created by pedro on 18/07/16.
 */
public interface MainThreadExecutor {
    void execute(Runnable runnable);
}