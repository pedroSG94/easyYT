package com.pedro.easyyt.presenter;

import android.hardware.Camera;
import android.view.SurfaceView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.pedro.easyyt.domain.interactor.ffmpeg.RecordManager;
import com.pedro.easyyt.domain.interactor.yasea.InitEncoderAndSend;
import com.pedro.easyyt.domain.interactor.youtube.createevent.CreateEventInteractor;
import com.pedro.easyyt.domain.interactor.youtube.endevent.EndEventInteractor;
import com.pedro.easyyt.domain.interactor.youtube.startevent.StartEventInteractor;
import com.pedro.easyyt.domain.model.RecordDataConfig;
import com.pedro.easyyt.domain.model.StreamDataInfo;

/**
 * Created by pedro on 18/07/16.
 */
public class YouTubePresenterImp extends YouTubePresenter{

    private RecordManager recordManager;

    private CreateEventInteractor createEventInteractor;
    private StartEventInteractor startEventInteractor;
    private EndEventInteractor endEventInteractor;

    public YouTubePresenterImp(CreateEventInteractor createEventInteractor,
                               StartEventInteractor startEventInteractor,
                               EndEventInteractor endEventInteractor) {
        this.createEventInteractor = createEventInteractor;
        this.startEventInteractor = startEventInteractor;
        this.endEventInteractor = endEventInteractor;
    }

    @Override
    public void createEvent(GoogleAccountCredential credential, String name, String description,
                            String resolution, String state) {
        createEventInteractor.createEvent(credential, name, description, resolution, state,
                new CreateEventInteractor.Callback() {
            @Override
            public void onSuccess(StreamDataInfo streamDataInfo, String endPoint) {
                view.createEventSuccess(streamDataInfo, endPoint);
            }

            @Override
            public void onErrorStartActivityForResult(UserRecoverableAuthIOException e) {
                view.onErrorStartActivityForResult(e);
            }

            @Override
            public void onErrorStartActivityForResult2(IllegalArgumentException e) {
                view.onErrorStartActivityForResult2(e);
            }

            @Override
            public void onError(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void startEvent(GoogleAccountCredential credential, String id) {
        startEventInteractor.startEvent(credential, id, new StartEventInteractor.Callback() {
            @Override
            public void onSuccess() {
                view.startEventSuccess();
            }

            @Override
            public void onError(String error) {
                view.onError(error);
            }
        });

    }

    @Override
    public void endEvent(GoogleAccountCredential credential, String id) {
        endEventInteractor.endEvent(credential, id, new EndEventInteractor.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void startStream(final GoogleAccountCredential credential, String name, String description,
                            String resolution, String state, final SurfaceView surfaceView,
                            final RecordDataConfig dataConfig, final Camera camera) {
        createEventInteractor.createEvent(credential, name, description, resolution, state,
                new CreateEventInteractor.Callback() {
                    @Override
                    public void onSuccess(StreamDataInfo streamDataInfo, String endPoint) {
                        view.streamData(streamDataInfo);
                        /**start send and encoding data*/
                        InitEncoderAndSend initEncoderAndSend = new InitEncoderAndSend(surfaceView);
                        initEncoderAndSend.initAll(endPoint);
                        startEventInteractor.startEvent(credential,
                                streamDataInfo.getLiveBroadcast().getId(),
                                new StartEventInteractor.Callback() {
                            @Override
                            public void onSuccess() {
                                view.streamingStarted();
                            }

                            @Override
                            public void onError(String error) {
                                view.onError(error);
                            }
                        });
                    }

                    @Override
                    public void onErrorStartActivityForResult(UserRecoverableAuthIOException e) {
                        view.onErrorStartActivityForResult(e);
                    }

                    @Override
                    public void onErrorStartActivityForResult2(IllegalArgumentException e) {
                        view.onErrorStartActivityForResult2(e);
                    }

                    @Override
                    public void onError(String error) {
                        view.onError(error);
                    }
                });
    }

    @Override
    public void stopStream(GoogleAccountCredential credential, String id) {
        endEventInteractor.endEvent(credential, id, new EndEventInteractor.Callback() {
            @Override
            public void onSuccess() {
                recordManager.stopRecording();
                view.streamingStopped();
            }

            @Override
            public void onError(String error) {
                view.onError(error);
            }
        });
    }
}