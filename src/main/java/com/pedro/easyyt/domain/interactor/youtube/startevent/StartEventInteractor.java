package com.pedro.easyyt.domain.interactor.youtube.startevent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by pedro on 18/07/16.
 */
public interface StartEventInteractor {

    void startEvent(GoogleAccountCredential credential, String id, Callback callback);

    interface Callback{
        void onSuccess();
        void onError(String error);
    }
}
