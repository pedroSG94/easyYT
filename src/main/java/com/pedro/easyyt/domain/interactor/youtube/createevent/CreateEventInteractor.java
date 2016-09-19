package com.pedro.easyyt.domain.interactor.youtube.createevent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.pedro.easyyt.domain.model.StreamDataInfo;

/**
 * Created by pedro on 18/07/16.
 */
public interface CreateEventInteractor {

    void createEvent(GoogleAccountCredential credential,
                     String name, String description, String resolution, String state,
                     Callback callback);

    interface Callback{
        void onSuccess(StreamDataInfo streamDataInfo, String endPoint);
        void onErrorStartActivityForResult(UserRecoverableAuthIOException e);
        void onErrorStartActivityForResult2(IllegalArgumentException e);
        void onError(String error);
    }
}
