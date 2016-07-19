package com.pedro.easyyt.domain.model;

import android.util.Log;
import com.pedro.easyyt.constants.Resolution;

/**
 * Created by pedro on 4/05/16.
 */
public class RecordDataConfig {

  private final String TAG = RecordDataConfig.class.toString();

  private int resolutionWidth;
  private int resolutionHeight;
  private int frameRate;
  private int audioRateInHz;


  public void setResolution(String resolution){
    if(resolution == Resolution.R_240P){
      resolutionWidth = 320;
      resolutionHeight = 240;
    }
    else if (resolution == Resolution.R_480P){
      resolutionWidth = 640;
      resolutionHeight = 480;
    }
    else if (resolution == Resolution.R_720P){
      resolutionWidth = 1280;
      resolutionHeight = 720;
    }
    else{
      Log.e(TAG, "resolution error change to default(240p)");
      resolutionWidth = 320;
      resolutionHeight = 240;
    }
  }
  public int getResolutionWidth() {
    return resolutionWidth;
  }

  public void setResolutionWidth(int resolutionWidth) {
    this.resolutionWidth = resolutionWidth;
  }

  public int getResolutionHeight() {
    return resolutionHeight;
  }

  public void setResolutionHeight(int resolutionHeight) {
    this.resolutionHeight = resolutionHeight;
  }

  public int getFrameRate() {
    return frameRate;
  }

  public void setFrameRate(int frameRate) {
    this.frameRate = frameRate;
  }

  public int getAudioRateInHz() {
    return audioRateInHz;
  }

  public void setAudioRateInHz(int audioRateInHz) {
    this.audioRateInHz = audioRateInHz;
  }
}
