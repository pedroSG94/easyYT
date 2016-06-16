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
import com.pedro.easyyt.model.EventData;
import com.pedro.easyyt.model.StreamDataInfo;
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

  public static StreamDataInfo createLiveEvent(YouTube youtube, String name,
      String description, String resolution, String state) throws IOException{
    SimpleDateFormat dateFormat = new SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss'Z'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    long futureDateMillis = System.currentTimeMillis()
        + Constants.FUTURE_DATE_OFFSET_MILLIS;
    Date futureDate = new Date();
    futureDate.setTime(futureDateMillis);
    String date = dateFormat.format(futureDate);

    Log.i("pedroTest", String.format(
        "Creating event: name='%s', description='%s', date='%s'.",
        name, description, date));

      LiveBroadcastSnippet broadcastSnippet = new LiveBroadcastSnippet();
      broadcastSnippet.setTitle(name);
      broadcastSnippet.setScheduledStartTime(new DateTime(futureDate));

      LiveBroadcastContentDetails contentDetails = new LiveBroadcastContentDetails();
      MonitorStreamInfo monitorStream = new MonitorStreamInfo();
      monitorStream.setEnableMonitorStream(false);
      contentDetails.setMonitorStream(monitorStream);

      // Create LiveBroadcastStatus with privacy status.
      LiveBroadcastStatus status = new LiveBroadcastStatus();
      status.setPrivacyStatus(state);

      LiveBroadcast broadcast = new LiveBroadcast();
      broadcast.setKind(Constants.BROADCAST);
      broadcast.setSnippet(broadcastSnippet);
      broadcast.setStatus(status);
      broadcast.setContentDetails(contentDetails);

      // Create the insert request
      YouTube.LiveBroadcasts.Insert liveBroadcastInsert = youtube
          .liveBroadcasts().insert("snippet,status,contentDetails",
              broadcast);

      // Request is executed and inserted broadcast is returned
      LiveBroadcast returnedBroadcast = liveBroadcastInsert.execute();

      // Create a snippet with title.
      LiveStreamSnippet streamSnippet = new LiveStreamSnippet();
      streamSnippet.setTitle(name);

      // Create content distribution network with format and ingestion
      // type.
      CdnSettings cdn = new CdnSettings();
      cdn.setFormat(resolution);
      cdn.setIngestionType("rtmp");

      LiveStream stream = new LiveStream();
      stream.setKind(Constants.STREAM);
      stream.setSnippet(streamSnippet);
      stream.setCdn(cdn);

      // Create the insert request
      YouTube.LiveStreams.Insert liveStreamInsert = youtube.liveStreams()
          .insert("snippet,cdn", stream);

      // Request is executed and inserted stream is returned
      LiveStream returnedStream = liveStreamInsert.execute();

      // Create the bind request
      YouTube.LiveBroadcasts.Bind liveBroadcastBind = youtube
          .liveBroadcasts().bind(returnedBroadcast.getId(),
              "id,contentDetails");

      // Set stream id to bind
      liveBroadcastBind.setStreamId(returnedStream.getId());

      // Request is executed and bound broadcast is returned
      return new StreamDataInfo(liveBroadcastBind.execute(), returnedStream);
  }

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

  public static void startEvent(YouTube youtube, String broadcastId)
      throws IOException {

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      Log.e(TAG, "", e);
    }

    YouTube.LiveBroadcasts.Transition
        transitionRequest = youtube.liveBroadcasts().transition("live", broadcastId, "status");
    transitionRequest.execute();
  }

  public static void endEvent(YouTube youtube, String broadcastId)
      throws IOException {
    YouTube.LiveBroadcasts.Transition transitionRequest = youtube.liveBroadcasts().transition(
        StreamState.COMPLETE, broadcastId, "status");
    transitionRequest.execute();
  }
}
