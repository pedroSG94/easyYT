package com.pedro.easyyt.app.base;

import com.pedro.easyyt.app.View;

/**
 * Created by pedro on 18/07/16.
 */
public abstract class BasePresenter<T extends View> {
    protected T view;

    public void setView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }
}
