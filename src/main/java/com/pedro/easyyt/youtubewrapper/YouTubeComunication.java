package com.pedro.easyyt.youtubewrapper;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.pedro.easyyt.app.View;
import com.pedro.easyyt.domain.model.StreamDataInfo;

/**
 * Created by pedro on 18/07/16.
 */
public interface YouTubeComunication extends View {
    void streamData(StreamDataInfo streamDataInfo);
    void streamingStarted();
    void streamingStopped();
    void createEventSuccess(StreamDataInfo streamDataInfo, String endPoint);
    void startEventSuccess();
    void endEventSuccess();
    void onError(String error);
    void onErrorStartActivityForResult(UserRecoverableAuthIOException e);
    void onErrorStartActivityForResult2(IllegalArgumentException e);
}
