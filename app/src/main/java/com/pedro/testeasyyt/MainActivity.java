package com.pedro.testeasyyt;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.pedro.easyyt.app.base.EasyYTActivity;
import com.pedro.easyyt.constants.Resolution;
import com.pedro.easyyt.constants.StreamState;
import com.pedro.easyyt.domain.model.StreamDataInfo;
import com.pedro.easyyt.youtubewrapper.EasyStream;
import com.pedro.easyyt.youtubewrapper.EasyYTCallback;
import com.pedro.easyyt.youtubewrapper.StreamBuilder;
import net.ossrs.yasea.SrsCameraView;

public class MainActivity extends EasyYTActivity implements EasyYTCallback, Button.OnClickListener {

    private Button button;
    private EasyStream easyStream;
    private SrsCameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        chooseAccount();

        mCameraView = (SrsCameraView) findViewById(R.id.surface);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        easyStream = StreamBuilder.getInstance()
            .setState(StreamState.PRIVATE)
            .setSurfaceView(mCameraView)
            .setCredential(getCredential())
            .setResolution(Resolution.R_720P)
            .setEastYTCallback(this)
            .build();
    }

    @Override
    public void onClick(View v) {
        if(!easyStream.isStreaming()) easyStream.startStream();
        else  easyStream.stopStream();
    }

    @Override
    public void streamingStarted() {

    }

    @Override
    public void streamingStopped() {

    }

    @Override
    public void createEventSuccess(StreamDataInfo streamDataInfo, String endPoint) {
    }

    @Override
    public void startEventSuccess() {
        Log.e("LIVE", "you are in live");
    }

    @Override
    public void endEventSuccess() {

    }

    @Override
    public void onError(String error) {
        Log.e("ERROR", error);
    }

    @Override
    public void onErrorStartActivityForResult(UserRecoverableAuthIOException e) {
        startActivityForResult(e);
    }

    @Override
    public void onErrorStartActivityForResult2(IllegalArgumentException e) {
        startActivityForResult2(e);
    }
}
