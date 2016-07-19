package com.pedro.easyyt.domain.interactor.youtube.createevent;

import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CdnSettings;
import com.google.api.services.youtube.model.LiveBroadcast;
import com.google.api.services.youtube.model.LiveBroadcastContentDetails;
import com.google.api.services.youtube.model.LiveBroadcastSnippet;
import com.google.api.services.youtube.model.LiveBroadcastStatus;
import com.google.api.services.youtube.model.LiveStream;
import com.google.api.services.youtube.model.LiveStreamSnippet;
import com.google.api.services.youtube.model.MonitorStreamInfo;
import com.pedro.easyyt.app.base.BaseInteractor;
import com.pedro.easyyt.app.executor.InteractorExecutor;
import com.pedro.easyyt.app.executor.MainThreadExecutor;
import com.pedro.easyyt.constants.Constants;
import com.pedro.easyyt.domain.model.StreamDataInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pedro on 18/07/16.
 */
public class CreateEventInteractorImp extends BaseInteractor implements CreateEventInteractor {

    private String TAG = "CreateEventInteractorImp";

    private GoogleAccountCredential credential;
    private String name;
    private String description;
    private String resolution;
    private String state;
    private Callback callback;
    private StreamDataInfo streamDataInfo;

    public CreateEventInteractorImp(InteractorExecutor interactorExecutor,
                                    MainThreadExecutor mainThreadExecutor) {
        super(interactorExecutor, mainThreadExecutor);
    }

    @Override
    public void createEvent(GoogleAccountCredential credential, String name, String description,
                            String resolution, String state, Callback callback) {
        this.credential = credential;
        this.name = name;
        this.description = description;
        this.resolution = resolution;
        this.state = state;
        this.callback = callback;
        executeCurrentInteractor();
    }

    @Override
    public void run() {
        try {
            streamDataInfo = createLiveEvent(credential, name, description, resolution, state);
            executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(streamDataInfo, getEndpoint());
                }
            });
        }
        catch (final UserRecoverableAuthIOException e) {
            executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onErrorStartActivityForResult(e);
                }
            });
        }
        catch(final IllegalArgumentException e){
            executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onErrorStartActivityForResult2(e);
                }
            });
        } catch (final IOException e) {
            executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e.getMessage());
                }
            });
        }
    }

    private StreamDataInfo createLiveEvent(GoogleAccountCredential credential, String name,
                                           String description, String resolution, String state) throws IOException{
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), credential).build();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long futureDateMillis = System.currentTimeMillis()
                + Constants.FUTURE_DATE_OFFSET_MILLIS;
        Date futureDate = new Date();
        futureDate.setTime(futureDateMillis);
        String date = dateFormat.format(futureDate);

        Log.d("pedroTest", String.format("Creating event: name='%s', description='%s', date='%s'.",
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
        YouTube.LiveBroadcasts.Insert liveBroadcastInsert = youTube
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
        YouTube.LiveStreams.Insert liveStreamInsert = youTube.liveStreams()
                .insert("snippet,cdn", stream);

        // Request is executed and inserted stream is returned
        LiveStream returnedStream = liveStreamInsert.execute();

        // Create the bind request
        YouTube.LiveBroadcasts.Bind liveBroadcastBind = youTube.liveBroadcasts()
                .bind(returnedBroadcast.getId(), "id,contentDetails");

        // Set stream id to bind
        liveBroadcastBind.setStreamId(returnedStream.getId());

        // Request is executed and bound broadcast is returned
        return new StreamDataInfo(liveBroadcastBind.execute(), returnedStream);
    }


    private String getEndpoint(){
        String address = streamDataInfo.getLiveStream().getCdn().getIngestionInfo().getIngestionAddress();
        String name = streamDataInfo.getLiveStream().getCdn().getIngestionInfo().getStreamName();

        int pos = address.lastIndexOf("/");
        String urlIP = address.substring(0, pos);
        String urlDetail = address.substring(pos);

        String endpoint = urlIP + ":1935" + urlDetail + "/" + name;
        Log.d(TAG, "endpoint: " + endpoint);
        return endpoint;
    }
}
