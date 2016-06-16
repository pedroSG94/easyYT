package com.pedro.easyyt.ffmpeg;

import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import com.pedro.easyyt.model.RecordDataConfig;
import org.bytedeco.javacv.FFmpegFrameRecorder;

/**
 * Created by pedro on 4/05/16.
 */
public class RecordManager {

  private final String TAG = RecordManager.class.toString();
  private FFmpegFrameRecorder recorder;
  private RecordAudio recordAudio;
  private Thread audioThread;

  private String rtmpUrl;
  private EasyYTView easyYTView;
  private RecordDataConfig dataConfig;
  private Camera camera;

  private int bufferSize;
  private AudioRecord audioRecord;

  public RecordManager(String rtmpUrl, EasyYTView easyYTView, RecordDataConfig dataConfig, Camera camera){
    this.rtmpUrl = rtmpUrl;
    this.easyYTView = easyYTView;
    this.dataConfig = dataConfig;
    this.camera = camera;

    setBufferSizeAndAudioRecorder();
    setFFmepgFrameRecorder();
  }

  private void setBufferSizeAndAudioRecorder(){
    bufferSize = AudioRecord.getMinBufferSize(dataConfig.getAudioRateInHz(),
        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, dataConfig.getAudioRateInHz(),
        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
  }

  private void setFFmepgFrameRecorder(){
    recorder = new FFmpegFrameRecorder(rtmpUrl, dataConfig.getResolutionWidth(), dataConfig.getResolutionHeight(), 1);
    recorder.setVideoQuality(0); // maximum quality
    recorder.setVideoOption("preset", "veryfast"); // or ultrafast or fast, etc.
    recorder.setFormat("flv");
    recorder.setSampleRate(dataConfig.getAudioRateInHz());
    // Set in the surface changed method
    recorder.setFrameRate(dataConfig.getFrameRate());
  }
  public void startRecording() {
    initRecordCamera();
    initRecorder();
    try {
      recorder.start();
      audioThread.start();
    } catch (FFmpegFrameRecorder.Exception e) {
      Log.e(TAG, e.getMessage());
    }
  }

  private void initRecordCamera(){
    try {
      easyYTView.setDependencies(camera, recorder, dataConfig, audioRecord);
      easyYTView.initRecordVideo();
    }
    catch(Exception e){
      Log.e(TAG, e.getMessage());
    }

  }

  private void initRecorder() {
    recordAudio = new RecordAudio(recorder, dataConfig, bufferSize, audioRecord);
    audioThread = new Thread(recordAudio);
  }

  public void stopRecording() {
    recordAudio.setRunAudioThread(false);
    try {
      audioThread.join();
    } catch (InterruptedException e) {
      // reset interrupt to be nice
      Thread.currentThread().interrupt();
      return;
    }
    audioThread = null;
    if (recorder != null && recordAudio.isRecording()) {

      recordAudio.setRecording(false);
      Log.d(TAG, "Finishing recording, calling stop and release on recorder");
      try {
        recorder.stop();
        recorder.release();
      } catch (FFmpegFrameRecorder.Exception e) {
        e.printStackTrace();
      }
      recorder = null;
    }
  }
}
