package com.pedro.easyyt.youtubewrapper;

import android.util.Log;
import android.view.SurfaceView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.pedro.easyyt.constants.Resolution;
import com.pedro.easyyt.constants.StreamState;
import com.pedro.easyyt.domain.model.RecordDataConfig;
import com.pedro.easyyt.exceptions.CameraException;

/**
 * Created by pedro on 6/05/16.
 */
public class StreamBuilder {

  private final String TAG = StreamBuilder.class.toString();

  private static volatile StreamBuilder myInstance = null;

  private SurfaceView surfaceView;
  private EasyYTCallback easyYTCallback;
  private GoogleAccountCredential credential;
  private String resolution = Resolution.R_240P;
  private String name = "easyyt_name";
  private String description = "easyyt_description";
  private int frameRate = 15;
  private int audioRateInHz = 44100;
  private String state = StreamState.PUBLIC;

  private StreamBuilder(){}

  public final static StreamBuilder getInstance() {
    if (myInstance == null) {
      synchronized (StreamBuilder.class) {
        if (myInstance == null) {
          StreamBuilder.myInstance = new StreamBuilder();
        }
      }
    }
    return myInstance;
  }

  public StreamBuilder setFrameRate(int frameRate){
    this.frameRate = frameRate;
    return this;
  }

  public StreamBuilder setAudioRate(int audioRate){
    audioRateInHz = audioRate;
    return this;
  }

  public StreamBuilder setSurfaceView(SurfaceView surfaceView){
    this.surfaceView = surfaceView;
    return this;
  }

  public StreamBuilder setEastYTCallback(EasyYTCallback easyYTCallback){
    this.easyYTCallback = easyYTCallback;
    return this;
  }

  public StreamBuilder setCredential(GoogleAccountCredential credential){
    this.credential = credential;
    return this;
  }

  public StreamBuilder setState(String state){
    if (state == StreamState.PUBLIC || state == StreamState.PRIVATE) {
      this.state = state;
    }
    else{
      Log.e(TAG, "state unsupported change to default(public)");
    }
    return this;
  }

  public StreamBuilder setResolution(String resolution){
    if(resolution == Resolution.R_240P || resolution == Resolution.R_480P || resolution == Resolution.R_720P) {
      this.resolution = resolution;
    }
    else{
      Log.e(TAG, "resolution unsupported change to default(240p)");
      this.resolution = Resolution.R_240P;
    }
    return this;
  }

  public StreamBuilder setName(String name){
    this.name = name;
    return this;
  }

  public StreamBuilder setDescription(String description){
    this.description = description;
    return this;
  }

  public EasyStream build(){
    try {
      RecordDataConfig dataConfig = new RecordDataConfig();
      dataConfig.setFrameRate(frameRate);
      dataConfig.setAudioRateInHz(audioRateInHz);
      dataConfig.setResolution(resolution);

      EasyStream easyStream = new EasyStream();
      easyStream.setEasyYTCallback(easyYTCallback);
      easyStream.setSurfaceView(surfaceView);
      easyStream.setCredential(credential);
      easyStream.setDataConfig(dataConfig);
      easyStream.setResolution(resolution);
      easyStream.setName(name);
      easyStream.setDescription(description);
      easyStream.setState(state);
      return easyStream;
    }
    catch(CameraException e){
      Log.e(TAG, e.getMessage());
      Log.e(TAG, "lock orientation portrait to fix");
      return null;
    }
  }

}
