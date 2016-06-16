package com.pedro.easyyt.youtubewrapper;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.pedro.easyyt.constants.Constants;
import com.pedro.easyyt.model.StreamDataInfo;
import java.io.IOException;
import java.util.List;

/**
 * Created by pedro on 4/05/16.
 */
public class CreateEvent extends AsyncTask<List<String>, GoogleAccountCredential, Void> {

  private final String TAG = CreateEvent.class.toString();

  private GoogleAccountCredential credential;
  private String description;
  private String name;
  private String state;
  private String resolution;
  private Activity activity;

  private StreamDataInfo streamDataInfo;
  private YouTube youTube;
  private SendData sendData;

  private boolean error;

  public CreateEvent(Activity activity, SendData sendData, GoogleAccountCredential credential, String name, String description, String resolution, String state){
    this.credential = credential;
    this.name = name;
    this.description = description;
    this.state = state;
    this.resolution = resolution;
    this.activity = activity;
    this.sendData = sendData;

    error = false;
  }

  @Override
  protected Void doInBackground(List<String>... params) {
    youTube = new YouTube.Builder(new NetHttpTransport(),
        new JacksonFactory(), credential).build();

    try {
      streamDataInfo = EasyYTManager.createLiveEvent(youTube, name, description, resolution, state);
    }
    catch (UserRecoverableAuthIOException e) {
      activity.startActivityForResult(e.getIntent(), Constants.REQUEST_AUTHORIZATION);
      error = true;
    }
    catch(IllegalArgumentException e){
      activity.startActivityForResult(credential.newChooseAccountIntent(),
          Constants.REQUEST_ACCOUNT_PICKER);
      error = true;
    }
    catch (IOException e) {
      e.printStackTrace();
      error = true;
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if (!error) {
      Log.d(TAG, "event created");
      sendData.youtubeUrl(getEndpoint());
      sendData.youtubeId(streamDataInfo.getLiveBroadcast().getId());
      new StartEvent(streamDataInfo.getLiveBroadcast().getId(), youTube).execute();
    }
    else{
      Log.e(TAG, "error to acquire permission of youtube...");
    }
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
