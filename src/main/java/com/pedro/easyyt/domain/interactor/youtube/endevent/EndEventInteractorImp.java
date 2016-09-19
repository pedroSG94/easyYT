package com.pedro.easyyt.domain.interactor.youtube.endevent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.pedro.easyyt.app.base.BaseInteractor;
import com.pedro.easyyt.app.executor.InteractorExecutor;
import com.pedro.easyyt.app.executor.MainThreadExecutor;
import com.pedro.easyyt.constants.StreamState;

import java.io.IOException;

/**
 * Created by pedro on 18/07/16.
 */
public class EndEventInteractorImp extends BaseInteractor implements EndEventInteractor {

    private GoogleAccountCredential credential;
    private String id;
    private Callback callback;

    public EndEventInteractorImp(InteractorExecutor interactorExecutor, MainThreadExecutor mainThreadExecutor) {
        super(interactorExecutor, mainThreadExecutor);
    }

    @Override
    public void run() {
        try {
            endEvent(credential, id);
            callback.onSuccess();
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }

    @Override
    public void endEvent(GoogleAccountCredential credential, String id, Callback callback) {
        this.credential = credential;
        this.id = id;
        this.callback = callback;
        executeCurrentInteractor();
    }

    private void endEvent(GoogleAccountCredential credential, String id) throws IOException {
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                credential).build();
        YouTube.LiveBroadcasts.Transition transitionRequest = youTube.liveBroadcasts().transition(
                StreamState.COMPLETE, id, "status");
        transitionRequest.execute();
    }
}
