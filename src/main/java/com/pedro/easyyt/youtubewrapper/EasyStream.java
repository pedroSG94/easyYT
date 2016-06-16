package com.pedro.easyyt.youtubewrapper;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.pedro.easyyt.ffmpeg.RecordManager;
import com.pedro.easyyt.ffmpeg.EasyYTView;
import com.pedro.easyyt.model.RecordDataConfig;

/**
 * Created by pedro on 6/05/16.
 */
public class EasyStream implements SendData {

  private final String TAG = EasyStream.class.toString();

  private RecordManager recordManager;
  private EasyYTView easyYTView;
  private Activity activity;
  private GoogleAccountCredential credential;
  private Camera camera;
  private RecordDataConfig dataConfig;
  private String resolution;
  private String name;
  private String description;
  private String state;
  private String id;
  private boolean streaming;

  public EasyStream(){
    streaming = false;
  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }

  public void setEasyYTView(EasyYTView easyYTView) {
    this.easyYTView = easyYTView;
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
      new CreateEvent(activity, this, credential, name, description, resolution, state).execute();
    }
    else{
      Log.e(TAG, "you are streaming, stop it if you want start other stream");
    }
  }

  public void stopStream(){
    Log.d(TAG, "stopping stream...");
    if(id != null) {
      new EndEvent(credential, id).execute();
      recordManager.stopRecording();
      id = null;
      streaming = false;
    }
    else{
      Log.e(TAG, "no event created, you cant stop anything");
    }
  }

  public boolean isStreaming(){
   return streaming;
  }

  @Override
  public void youtubeUrl(String url) {
    Log.d(TAG, "sending data to youtube...");
    recordManager = new RecordManager(url, easyYTView, dataConfig, camera);
    recordManager.startRecording();
    streaming = true;
  }

  @Override
  public void youtubeId(String id) {
    this.id = id;
  }
}
