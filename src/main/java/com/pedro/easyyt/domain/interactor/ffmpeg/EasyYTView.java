package com.pedro.easyyt.domain.interactor.ffmpeg;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pedro.easyyt.domain.model.RecordDataConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 * Created by pedro on 4/05/16.
 */
public class EasyYTView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback{

  private final String TAG = EasyYTView.class.toString();

  private SurfaceHolder holder;
  private Camera camera;
  private Frame yuvImage;
  private FFmpegFrameRecorder recorder;
  private boolean recording = true;
  private long startTime = 0;
  private boolean isPreviewOn = false;
  private RecordDataConfig dataConfig;

  private boolean dependenciesReady = false;
  private boolean initReady = false;

  private AudioRecord audioRecord;

  public EasyYTView(Context context) {
    super(context);
  }

  public EasyYTView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public EasyYTView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    Log.e(TAG, "created");
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.e(TAG, "changed");
    if (dependenciesReady && initReady) {
      stopPreview();
      Camera.Parameters camParams = camera.getParameters();
      List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
      // Sort the list in ascending order
      Collections.sort(sizes, new Comparator<Camera.Size>() {
        public int compare(final Camera.Size a, final Camera.Size b) {
          return a.width * a.height - b.width * b.height;
        }
      });
      // Pick the first preview size that is equal or bigger, or pick the last (biggest) option if we cannot
      // reach the initial settings of imageWidth/imageHeight.
      for (int i = 0; i < sizes.size(); i++) {
        if ((sizes.get(i).width >= dataConfig.getResolutionWidth() && sizes.get(i).height >= dataConfig.getResolutionHeight()) || i == sizes.size() - 1) {
          dataConfig.setResolutionWidth(sizes.get(i).width);
          dataConfig.setResolutionHeight(sizes.get(i).height);
          Log.d(TAG, "Changed to supported resolution: " + dataConfig.getResolutionWidth() + "x" + dataConfig.getResolutionHeight());
          break;
        }
      }
      camParams.setPreviewSize(dataConfig.getResolutionWidth(), dataConfig.getResolutionHeight());
      Log.d(TAG, "Setting resolutionWidth: "
              + dataConfig.getResolutionWidth()
              + " resolutionHeight: "
              + dataConfig.getResolutionHeight()
              + " frameRate: "
              + dataConfig.getFrameRate());
      camParams.setPreviewFrameRate(dataConfig.getFrameRate());
      Log.d(TAG, "Preview Framerate: " + camParams.getPreviewFrameRate());
      camera.setParameters(camParams);
      // Set the holder (which might have changed) again
      try {
        camera.setPreviewDisplay(holder);
        camera.setPreviewCallback(this);
        startPreview();
      } catch (Exception e) {
        Log.d(TAG, "Could not set preview display in surfaceChanged");
      }
    }
  }
  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    try {
      holder.addCallback(null);
      camera.setPreviewCallback(null);
    } catch (RuntimeException e) {
      // The camera has probably just been released, ignore.
    }
  }
  public void startPreview() {
    if (!isPreviewOn && camera != null) {
      isPreviewOn = true;
      camera.startPreview();
    }
  }
  public void stopPreview() {
    if (isPreviewOn && camera != null) {
      isPreviewOn = false;
      camera.stopPreview();
    }
  }
  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    if(dependenciesReady && initReady) {
      if (audioRecord == null || audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
        startTime = System.currentTimeMillis();
        return;
      }

      /* get video data */
      if(yuvImage == null)
        Log.d(TAG, "image null");
      if (yuvImage != null && recording) {
        ((ByteBuffer) yuvImage.image[0].position(0)).put(data);
        try {
          Log.d(TAG, "Writing Frame");
          long t = 1000 * (System.currentTimeMillis() - startTime);
          if (t > recorder.getTimestamp()) {
            recorder.setTimestamp(t);
          }
          recorder.record(yuvImage);
        } catch (FFmpegFrameRecorder.Exception e) {
          Log.d(TAG, e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }

  public void setDependencies(Camera camera, FFmpegFrameRecorder recorder, RecordDataConfig dataConfig, AudioRecord audioRecord){
    this.camera = camera;
    this.dataConfig = dataConfig;
    this.recorder = recorder;
    this.audioRecord = audioRecord;

    dependenciesReady = true;
    Log.e(TAG, "dependencies ok");
  }

  public void initRecordVideo(){
    holder = getHolder();
    holder.addCallback(EasyYTView.this);
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    camera.setPreviewCallback(EasyYTView.this);
    yuvImage = new Frame(dataConfig.getResolutionWidth(), dataConfig.getResolutionHeight(), Frame.DEPTH_UBYTE, 2);

    try {
      stopPreview();
      camera.setPreviewDisplay(holder);
      Log.e(TAG, "init record video...");
    } catch (IOException exception) {
      Log.e(TAG, "error in camera");
      camera.release();
      camera = null;
    }

    initReady = true;
    configureResolution();
  }

  public void configureResolution(){
    if (dependenciesReady && initReady) {
      stopPreview();
      Camera.Parameters camParams = camera.getParameters();
      List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
      // Sort the list in ascending order
      Collections.sort(sizes, new Comparator<Camera.Size>() {
        public int compare(final Camera.Size a, final Camera.Size b) {
          return a.width * a.height - b.width * b.height;
        }
      });
      // Pick the first preview size that is equal or bigger, or pick the last (biggest) option if we cannot
      // reach the initial settings of imageWidth/imageHeight.
      for (int i = 0; i < sizes.size(); i++) {
        if ((sizes.get(i).width >= dataConfig.getResolutionWidth() && sizes.get(i).height >= dataConfig.getResolutionHeight()) || i == sizes.size() - 1) {
          dataConfig.setResolutionWidth(sizes.get(i).width);
          dataConfig.setResolutionHeight(sizes.get(i).height);
          Log.d(TAG, "Changed to supported resolution: " + dataConfig.getResolutionWidth() + "x" + dataConfig.getResolutionHeight());
          break;
        }
      }
      camParams.setPreviewSize(dataConfig.getResolutionWidth(), dataConfig.getResolutionHeight());
      Log.d(TAG, "Setting resolutionWidth: "
              + dataConfig.getResolutionWidth()
              + " resolutionHeight: "
              + dataConfig.getResolutionHeight()
              + " frameRate: "
              + dataConfig.getFrameRate());
      camParams.setPreviewFrameRate(dataConfig.getFrameRate());
      Log.d(TAG, "Preview Framerate: " + camParams.getPreviewFrameRate());
      camera.setParameters(camParams);
      // Set the holder (which might have changed) again
      try {
        camera.setPreviewDisplay(holder);
        camera.setPreviewCallback(this);
        startPreview();
      } catch (Exception e) {
        Log.d(TAG, "Could not set preview display in surfaceChanged");
      }
    }
  }
}
