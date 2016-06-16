package com.pedro.easyyt.youtubewrapper;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.pedro.easyyt.constants.Resolution;
import com.pedro.easyyt.constants.StreamState;
import com.pedro.easyyt.exceptions.CameraException;
import com.pedro.easyyt.ffmpeg.EasyYTView;
import com.pedro.easyyt.model.RecordDataConfig;

/**
 * Created by pedro on 6/05/16.
 */
public class StreamBuilder {

  private final String TAG = StreamBuilder.class.toString();

  private static volatile StreamBuilder myInstance = null;

  private EasyYTView easyYTView;
  private Activity activity;
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

  public StreamBuilder setSurfaceView(EasyYTView easyYTView){
    this.easyYTView = easyYTView;
    return this;
  }

  public StreamBuilder setActivity(Activity activity){
    this.activity = activity;
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
      Camera camera = Camera.open();
      camera.setDisplayOrientation(90);

      RecordDataConfig dataConfig = new RecordDataConfig();
      dataConfig.setFrameRate(frameRate);
      dataConfig.setAudioRateInHz(audioRateInHz);
      dataConfig.setResolution(resolution);

      EasyStream easyStream = new EasyStream();
      easyStream.setActivity(activity);
      easyStream.setEasyYTView(easyYTView);
      easyStream.setCredential(credential);
      easyStream.setCamera(camera);
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
