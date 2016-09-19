package com.pedro.easyyt.constants;

import com.google.android.gms.common.Scopes;
import com.google.api.services.youtube.YouTubeScopes;

/**
 * Created by pedro on 19/04/16.
 */
public class Constants {
  public static final String BROADCAST = "youtube#liveBroadcast";
  public static final String STREAM = "youtube#liveStream";
  public static final String[] SCOPES = new String[]{Scopes.PROFILE, YouTubeScopes.YOUTUBE};
  public static final int FUTURE_DATE_OFFSET_MILLIS = 5 * 1000;
  public static final int REQUEST_ACCOUNT_PICKER = 2;
  public static final int REQUEST_AUTHORIZATION = 3;
  public static final int REQUEST_STREAMER = 4;
}
