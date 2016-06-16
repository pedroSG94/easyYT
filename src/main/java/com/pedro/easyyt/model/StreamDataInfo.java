package com.pedro.easyyt.model;

import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveStream;

/**
 * Created by pedro on 4/05/16.
 */
public class StreamDataInfo {
  private LiveBroadcast liveBroadcast;
  private LiveStream liveStream;

  public StreamDataInfo(){}

  public StreamDataInfo(LiveBroadcast liveBroadcast, LiveStream liveStream){
    this.liveBroadcast = liveBroadcast;
    this.liveStream = liveStream;
  }

  public void setLiveBroadcast(LiveBroadcast liveBroadcast) {
    this.liveBroadcast = liveBroadcast;
  }

  public void setLiveStream(LiveStream liveStream) {
    this.liveStream = liveStream;
  }

  public LiveBroadcast getLiveBroadcast() {
    return liveBroadcast;
  }

  public LiveStream getLiveStream() {
    return liveStream;
  }
}
