package com.pedro.easyyt.domain.interactor.yasea;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import net.ossrs.yasea.SrsEncoder;

/**
 * Created by pedro on 27/07/16.
 */
public class AudioThread extends Thread {

    private final String TAG = "AudioThread";
    private boolean aloop;
    private AudioRecord mic = null;
    private SrsEncoder encoder;

    public AudioThread(){}

    public AudioThread(SrsEncoder encoder) {
        this.encoder = encoder;
    }

    public void setEncoder(SrsEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void run() {
        super.run();
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        if (mic != null) {
            return;
        }

        int bufferSize = 2 * AudioRecord.getMinBufferSize(SrsEncoder.ASAMPLERATE, SrsEncoder.ACHANNEL, SrsEncoder.AFORMAT);
        mic = new AudioRecord(MediaRecorder.AudioSource.MIC, SrsEncoder.ASAMPLERATE, SrsEncoder.ACHANNEL, SrsEncoder.AFORMAT, bufferSize);
        mic.startRecording();

        byte pcmBuffer[] = new byte[4096];
        while (aloop && !Thread.interrupted()) {
            int size = mic.read(pcmBuffer, 0, pcmBuffer.length);
            if (size <= 0) {
                Log.e(TAG, "***** audio ignored, no data to read.");
                break;
            }
            onGetPcmFrame(pcmBuffer, size);
        }
    }

    public void stopAudio() {
        aloop = false;
        Log.i(TAG, "stop audio worker thread");
        interrupt();
        try {
            join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            interrupt();
        }
        if (mic != null) {
            mic.setRecordPositionUpdateListener(null);
            mic.stop();
            mic.release();
            mic = null;
        }
    }

    private void onGetPcmFrame(byte[] pcmBuffer, int size) {
        encoder.onGetPcmFrame(pcmBuffer, size);
    }
}
