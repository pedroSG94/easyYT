package com.pedro.easyyt.app.base;

import com.pedro.easyyt.app.executor.Interactor;
import com.pedro.easyyt.app.executor.InteractorExecutor;
import com.pedro.easyyt.app.executor.MainThreadExecutor;

/**
 * Created by pedro on 18/07/16.
 */
public abstract class BaseInteractor implements Interactor {

    private InteractorExecutor interactorExecutor;
    private MainThreadExecutor mainThreadExecutor;

    public BaseInteractor(InteractorExecutor interactorExecutor, MainThreadExecutor mainThreadExecutor){
        this.interactorExecutor = interactorExecutor;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    public void executeCurrentInteractor(){
        interactorExecutor.execute(this);
    }

    public void executeOnMainThread(Runnable runnable){
        mainThreadExecutor.execute(runnable);
    }
}
