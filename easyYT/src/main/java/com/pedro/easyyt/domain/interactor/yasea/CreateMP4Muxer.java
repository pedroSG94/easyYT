package com.pedro.easyyt.domain.interactor.yasea;

import android.util.Log;

import net.ossrs.yasea.SrsMp4Muxer;

/**
 * Created by pedro on 27/07/16.
 */
public class CreateMP4Muxer {

    private final String TAG = "CreateMP4Muxer";

    public CreateMP4Muxer(){}

    private SrsMp4Muxer mp4Muxer = new SrsMp4Muxer(new SrsMp4Muxer.EventHandler() {
        @Override
        public void onRecordPause(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void onRecordResume(String msg) {
            Log.i(TAG, msg);
        }

        @Override
        public void onRecordStarted(String msg) {
            Log.e(TAG, msg);
        }

        @Override
        public void onRecordFinished(String msg) {
            Log.i(TAG, msg);
        }
    });

    public SrsMp4Muxer getMp4Muxer() {
        return mp4Muxer;
    }
}
