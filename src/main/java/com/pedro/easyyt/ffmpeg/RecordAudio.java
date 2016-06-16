package com.pedro.easyyt.ffmpeg;

import android.media.AudioRecord;
import android.util.Log;
import com.pedro.easyyt.model.RecordDataConfig;
import java.nio.ShortBuffer;
import org.bytedeco.javacv.FFmpegFrameRecorder;

/**
 * Created by pedro on 4/05/16.
 */
public class RecordAudio implements Runnable{

  private final String TAG = AudioRecord.class.toString();

  private AudioRecord audioRecord;
  private boolean runAudioThread = true;
  private boolean recording = true;
  private FFmpegFrameRecorder recorder;
  private RecordDataConfig dataConfig;
  private int bufferSize;

  public RecordAudio(FFmpegFrameRecorder recorder, RecordDataConfig dataConfig, int bufferSize, AudioRecord audioRecord){
    this.recorder = recorder;
    this.dataConfig = dataConfig;
    this.bufferSize = bufferSize;
    this.audioRecord = audioRecord;
  }
  @Override
  public void run() {
    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    // Audio
    ShortBuffer audioData;
    int bufferReadResult;

    //int bufferSize;
    //bufferSize = AudioRecord.getMinBufferSize(dataConfig.getAudioRateInHz(), AudioFormat.CHANNEL_IN_MONO,
    //    AudioFormat.ENCODING_PCM_16BIT);
    //audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, dataConfig.getAudioRateInHz(),
    //    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

    audioData = ShortBuffer.allocate(bufferSize);

    Log.d(TAG, "audioRecord.startRecording()");
    audioRecord.startRecording();

/* ffmpeg_audio encoding loop */
    while (runAudioThread) {

      bufferReadResult = audioRecord.read(audioData.array(), 0, audioData.capacity());
      audioData.limit(bufferReadResult);
      if (bufferReadResult > 0) {
        Log.v(TAG,"bufferReadResult: " + bufferReadResult);
        // If "recording" isn't true when start this thread, it never get's set according to this if statement...!!!
        // Why? Good question...
        if (recording) {
          //if (RECORD_LENGTH <= 0)
          try {
            recorder.recordSamples(audioData);
          }
          catch (FFmpegFrameRecorder.Exception e) {
            Log.v(TAG,e.getMessage());
            e.printStackTrace();
          }
        }
      }
    }

    Log.v(TAG,"AudioThread Finished, release audioRecord");
/* encoding finish, release recorder */
    if (audioRecord != null) {
      audioRecord.stop();
      audioRecord.release();
      audioRecord = null;
      Log.v(TAG,"audioRecord released");
    }
  }

  public boolean isRunAudioThread() {
    return runAudioThread;
  }

  public void setRunAudioThread(boolean runAudioThread) {
    this.runAudioThread = runAudioThread;
  }

  public boolean isRecording() {
    return recording;
  }

  public void setRecording(boolean recording) {
    this.recording = recording;
  }
}
