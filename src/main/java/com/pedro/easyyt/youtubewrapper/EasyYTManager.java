package com.pedro.easyyt.youtubewrapper;

import android.util.Log;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.IngestionInfo;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastContentDetails;
import com.google.api.services.youtube.model.LiveBroadcastListResponse;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveBroadcastStatus;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamListResponse;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.google.api.services.youtube.model.MonitorStreamInfo;
import com.pedro.easyyt.constants.Constants;
import com.pedro.easyyt.constants.StreamState;
import com.pedro.easyyt.domain.model.EventData;
import com.pedro.easyyt.domain.model.StreamDataInfo;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by pedro on 19/04/16.
 */
public class EasyYTManager {

  private static final String TAG = EasyYTManager.class.toString();

  public static List<EventData> getLiveEvents(
      YouTube youtube) throws IOException {
    Log.i(TAG, "Requesting live events.");

    YouTube.LiveBroadcasts.List liveBroadcastRequest = youtube
        .liveBroadcasts().list("id,snippet,contentDetails");
    // liveBroadcastRequest.setMine(true);
    liveBroadcastRequest.setBroadcastStatus("upcoming");

    // List request is executed and list of broadcasts are returned
    LiveBroadcastListResponse returnedListResponse = liveBroadcastRequest.execute();

    // Get the list of broadcasts associated with the user.
    List<LiveBroadcast> returnedList = returnedListResponse.getItems();

    List<EventData> resultList = new ArrayList<>(returnedList.size());
    EventData event;

    for (LiveBroadcast broadcast : returnedList) {
      event = new EventData();
      event.setEvent(broadcast);
      String streamId = broadcast.getContentDetails().getBoundStreamId();
      if (streamId != null) {
        String ingestionAddress = getIngestionAddress(youtube, streamId);
        event.setIngestionAddress(ingestionAddress);
      }
      resultList.add(event);
    }
    return resultList;
  }

  public static String getIngestionAddress(YouTube youtube, String streamId)
      throws IOException {
    YouTube.LiveStreams.List liveStreamRequest = youtube.liveStreams()
        .list("cdn");
    liveStreamRequest.setId(streamId);
    LiveStreamListResponse returnedStream = liveStreamRequest.execute();

    List<LiveStream> streamList = returnedStream.getItems();
    if (streamList.isEmpty()) {
      return "";
    }
    IngestionInfo ingestionInfo = streamList.get(0).getCdn().getIngestionInfo();
    return ingestionInfo.getIngestionAddress() + "/"
        + ingestionInfo.getStreamName();
  }
}
