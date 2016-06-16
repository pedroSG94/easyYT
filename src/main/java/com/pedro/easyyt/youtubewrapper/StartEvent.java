package com.pedro.easyyt.youtubewrapper;

import android.os.AsyncTask;
import android.util.Log;
import com.google.api.services.youtube.YouTube;
import java.io.IOException;

/**
 * Created by pedro on 6/05/16.
 */
public class StartEvent extends AsyncTask<Void, Void, Void>{

  private final String TAG = StartEvent.class.toString();
  private YouTube youTube;
  private String id;

  public StartEvent(String id, YouTube youTube){
    this.id = id;
    this.youTube = youTube;
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      Thread.sleep(10000);
      EasyYTManager.startEvent(youTube, id);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    Log.d(TAG, "event started");
  }
}
