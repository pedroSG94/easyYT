package com.pedro.easyyt.youtubewrapper;

import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import java.io.IOException;

/**
 * Created by pedro on 9/05/16.
 */
public class EndEvent extends AsyncTask<Void, Void, Void> {

  private final String TAG = EndEvent.class.toString();

  private String id;
  private GoogleAccountCredential credential;
  private boolean error;

  public EndEvent(GoogleAccountCredential credential, String id){
    this.credential = credential;
    this.id = id;

    error = false;
  }

  @Override
  protected Void doInBackground(Void... params) {
    YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
        credential).build();
    try {
      EasyYTManager.endEvent(youTube, id);
    } catch (IOException e) {
      e.printStackTrace();
      error = true;
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if(!error) {
      Log.d(TAG, "event finished");
    }
    else{
      Log.e(TAG, "error stopping event");
    }
  }
}
