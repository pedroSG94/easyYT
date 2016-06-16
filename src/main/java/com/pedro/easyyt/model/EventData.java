package com.pedro.easyyt.model;

import com.google.api.services.youtube.model.LiveBroadcast;

/**
 * Created by pedro on 28/04/16.
 */
public class EventData {
  private LiveBroadcast mEvent;
  private String mIngestionAddress;

  public LiveBroadcast getEvent() {
    return mEvent;
  }

  public void setEvent(LiveBroadcast event) {
    mEvent = event;
  }

  public String getId() {
    return mEvent.getId();
  }

  public String getTitle() {
    return mEvent.getSnippet().getTitle();
  }

  public String getThumbUri() {
    String url = mEvent.getSnippet().getThumbnails().getDefault().getUrl();
    // if protocol is not defined, pick https
    if (url.startsWith("//")) {
      url = "https:" + url;
    }
    return url;
  }

  public String getIngestionAddress() {
    return mIngestionAddress;
  }

  public void setIngestionAddress(String ingestionAddress) {
    mIngestionAddress = ingestionAddress;
  }

  public String getWatchUri() {
    return "http://www.youtube.com/watch?v=" + getId();
  }
}
