package com.pedro.easyyt.youtubewrapper;

import android.hardware.Camera;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.pedro.easyyt.app.executor.InteractorExecutor;
import com.pedro.easyyt.app.executor.InteractorExecutorImp;
import com.pedro.easyyt.app.executor.MainThreadExecutor;
import com.pedro.easyyt.app.executor.MainThreadExecutorImp;
import com.pedro.easyyt.domain.interactor.yasea.YaseaWrapper;
import com.pedro.easyyt.domain.interactor.youtube.createevent.CreateEventInteractorImp;
import com.pedro.easyyt.domain.interactor.youtube.endevent.EndEventInteractorImp;
import com.pedro.easyyt.domain.interactor.youtube.startevent.StartEventInteractorImp;
import com.pedro.easyyt.domain.model.RecordDataConfig;
import com.pedro.easyyt.domain.model.StreamDataInfo;
import com.pedro.easyyt.presenter.YouTubePresenter;
import com.pedro.easyyt.presenter.YouTubePresenterImp;
import net.ossrs.yasea.SrsCameraView;

/**
 * Created by pedro on 6/05/16.
 */
public class EasyStream implements YouTubeComunication {

  private final String TAG = EasyStream.class.toString();

  private SrsCameraView surfaceView;
  private GoogleAccountCredential credential;
  private Camera camera;
  private RecordDataConfig dataConfig;
  private String resolution;
  private String name;
  private String description;
  private String state;
  private String id;
  private boolean streaming;

  private YouTubePresenter youTubePresenter;
  private StreamDataInfo streamDataInfo;
  private EasyYTCallback easyYTCallback;

  public YaseaWrapper getYaseaWrapper() {
    return yaseaWrapper;
  }

  public void setYaseaWrapper(YaseaWrapper yaseaWrapper) {
    this.yaseaWrapper = yaseaWrapper;
  }

  private YaseaWrapper yaseaWrapper;

  public EasyStream(){
    MainThreadExecutor mainThreadExecutor = new MainThreadExecutorImp();
    InteractorExecutor interactorExecutor = new InteractorExecutorImp();
    youTubePresenter = new YouTubePresenterImp(
            new CreateEventInteractorImp(interactorExecutor, mainThreadExecutor),
            new StartEventInteractorImp(interactorExecutor, mainThreadExecutor),
            new EndEventInteractorImp(interactorExecutor, mainThreadExecutor));
    youTubePresenter.setView(this);
  }

  public void setEasyYTCallback(EasyYTCallback easyYTCallback) {
    this.easyYTCallback = easyYTCallback;
  }

  public void setSurfaceView(SrsCameraView surfaceView) {
    this.surfaceView = surfaceView;
  }

  public void setCredential(GoogleAccountCredential credential) {
    this.credential = credential;
  }

  public void setCamera(Camera camera){
    this.camera = camera;
  }

  public void setDataConfig(RecordDataConfig dataConfig){
    this.dataConfig = dataConfig;
  }

  public void setResolution(String resolution){
    this.resolution = resolution;
  }

  public void setName(String name){
    this.name = name;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public void setState(String state){
    this.state = state;
  }

  public void startStream(){
    Log.d(TAG, "starting...");
    if(id == null) {
      youTubePresenter.startStream(credential, name, description, resolution, state, yaseaWrapper, dataConfig, camera);
    }
    else{
      Log.e(TAG, "you are streaming, stop it if you want start other stream");
    }
  }

  public void stopStream(){
    Log.d(TAG, "stopping stream...");
    if(id != null) {
      youTubePresenter.stopStream(credential, streamDataInfo.getLiveBroadcast().getId(), yaseaWrapper);
      id = null;
    }
    else{
      Log.e(TAG, "no event created, you cant stop anything");
    }
  }

  public void createEvent(){
    youTubePresenter.createEvent(credential, name, description, resolution, state);
  }

  public void startEvent(String id){
    youTubePresenter.startEvent(credential, id);
  }

  public boolean isStreaming(){
   return streaming;
  }

  @Override
  public void streamData(StreamDataInfo streamDataInfo) {
    this.streamDataInfo = streamDataInfo;
    id = streamDataInfo.getLiveBroadcast().getId();
  }

  @Override
  public void streamingStarted() {
    easyYTCallback.streamingStarted();
    streaming = true;
  }

  @Override
  public void streamingStopped() {
    easyYTCallback.streamingStopped();
    streaming = false;
  }

  @Override
  public void createEventSuccess(StreamDataInfo streamDataInfo, String endPoint) {
    easyYTCallback.createEventSuccess(streamDataInfo, endPoint);
  }

  @Override
  public void startEventSuccess() {
    easyYTCallback.startEventSuccess();
  }

  @Override
  public void endEventSuccess() {
    easyYTCallback.endEventSuccess();
  }

  @Override
  public void onError(String error) {
    easyYTCallback.onError(error);
  }

  @Override
  public void onErrorStartActivityForResult(UserRecoverableAuthIOException e) {
    easyYTCallback.onErrorStartActivityForResult(e);
  }

  @Override
  public void onErrorStartActivityForResult2(IllegalArgumentException e) {
    easyYTCallback.onErrorStartActivityForResult2(e);
  }
}
