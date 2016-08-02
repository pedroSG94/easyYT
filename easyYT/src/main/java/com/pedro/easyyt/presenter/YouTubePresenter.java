package com.pedro.easyyt.presenter;

import android.hardware.Camera;
import android.view.SurfaceView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.pedro.easyyt.app.base.BasePresenter;
import com.pedro.easyyt.domain.model.RecordDataConfig;
import com.pedro.easyyt.youtubewrapper.YouTubeComunication;

/**
 * Created by pedro on 18/07/16.
 */
public abstract class YouTubePresenter extends BasePresenter<YouTubeComunication> {
    public abstract void createEvent(GoogleAccountCredential credential, String name,
                                     String description, String resolution, String state);
    public abstract void startEvent(GoogleAccountCredential credential, String id);
    public abstract void endEvent(GoogleAccountCredential credential, String id);
    public abstract void startStream(GoogleAccountCredential credential, String name,
                                     String description, String resolution, String state,
                                     SurfaceView surfaceView, RecordDataConfig dataConfig,
                                     Camera camera);
    public abstract void stopStream(GoogleAccountCredential credential, String id);
}
