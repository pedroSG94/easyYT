package com.pedro.easyyt.domain.interactor.youtube.startevent;

import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.pedro.easyyt.app.base.BaseInteractor;
import com.pedro.easyyt.app.executor.InteractorExecutor;
import com.pedro.easyyt.app.executor.MainThreadExecutor;

import java.io.IOException;

/**
 * Created by pedro on 18/07/16.
 */
public class StartEventInteractorImp extends BaseInteractor implements StartEventInteractor {

    private GoogleAccountCredential credential;
    private String id;
    private Callback callback;

    public StartEventInteractorImp(InteractorExecutor interactorExecutor, MainThreadExecutor mainThreadExecutor) {
        super(interactorExecutor, mainThreadExecutor);
    }

    @Override
    public void run() {
        try {
            startEvent(credential, id);
            executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess();
                }
            });
        } catch (IOException | InterruptedException e) {
            executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e.getMessage());
                }
            });
        }
    }

    @Override
    public void startEvent(GoogleAccountCredential credential, String id, Callback callback) {
        this.credential = credential;
        this.id = id;
        this.callback = callback;
        executeCurrentInteractor();
    }

    private void startEvent(GoogleAccountCredential credential, String id)
            throws IOException, InterruptedException {
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), credential).build();
        Thread.sleep(10000);
        YouTube.LiveBroadcasts.Transition
                transitionRequest = youTube.liveBroadcasts().transition("live", id, "status");
        transitionRequest.execute();
    }
}
