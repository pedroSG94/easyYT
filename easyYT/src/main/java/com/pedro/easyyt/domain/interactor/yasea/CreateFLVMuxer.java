package com.pedro.easyyt.domain.interactor.yasea;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import net.ossrs.yasea.SrsFlvMuxer;
import net.ossrs.yasea.SrsMp4Muxer;
import net.ossrs.yasea.rtmp.RtmpPublisher;

/**
 * Created by pedro on 27/07/16.
 */
public class CreateFLVMuxer {

    private final String TAG = "CreateFLVMuxer";

    public CreateFLVMuxer(){}

    private SrsFlvMuxer flvMuxer = new SrsFlvMuxer(new RtmpPublisher.EventHandler() {
        @Override
        public void onRtmpConnecting(String msg) {
            Log.e(TAG, msg);
        }

        @Override
        public void onRtmpConnected(String msg) {
            Log.e(TAG, msg);
        }

        @Override
        public void onRtmpVideoStreaming(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void onRtmpAudioStreaming(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void onRtmpStopped(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void onRtmpDisconnected(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void onRtmpOutputFps(final double fps) {
            Log.i(TAG, String.format("Output Fps: %f", fps));
        }
    });

    public SrsFlvMuxer getFlvMuxer() {
        return flvMuxer;
    }
}
