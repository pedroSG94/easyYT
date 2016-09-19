package com.pedro.easyyt.domain.interactor.yasea;

import android.hardware.Camera;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import net.ossrs.yasea.SrsEncoder;
import net.ossrs.yasea.SrsFlvMuxer;
import net.ossrs.yasea.SrsMp4Muxer;

/**
 * Created by pedro on 27/07/16.
 */
public class InitEncoderAndSend implements SurfaceHolder.Callback, Camera.PreviewCallback{


  private static final String TAG = "Yasea";

  private AudioRecord mic = null;
  private boolean aloop = false;
  private Thread aworker = null;

  private SurfaceView surfaceView = null;
  private Camera mCamera = null;

  private int videoFrameCount;
  private long lastTimeMillis;
  private int mPreviewRotation = 90;
  private int mCamId = Camera.getNumberOfCameras() - 1; // default camera
  private byte[] mYuvFrameBuffer = new byte[SrsEncoder.VPREV_WIDTH * SrsEncoder.VPREV_HEIGHT * 3 / 2];

  private SrsFlvMuxer flvMuxer = new CreateFLVMuxer().getFlvMuxer();
  private SrsMp4Muxer mp4Muxer = new CreateMP4Muxer().getMp4Muxer();
  private SrsEncoder mEncoder = new SrsEncoder(flvMuxer, mp4Muxer);

  //
  /**need implements orientation ans swap camera*/
  //

  public InitEncoderAndSend(SurfaceView surfaceView){
    this.surfaceView = surfaceView;
  }

  public void initAll(String endPoint){
    try {
      flvMuxer.start(endPoint);
    } catch (IOException e) {
      Log.e(TAG, "start FLV muxer failed.");
      e.printStackTrace();
      return;
    }
    flvMuxer.setVideoResolution(480, 640);
    startEncoder();
  }

  public void stopAll(){
    stopEncoder();
    flvMuxer.stop();
    mp4Muxer.stop();
  }

  public void pause(){
    mp4Muxer.pause();
  }

  public void resume(){
    mp4Muxer.resume();
  }

  private void startCamera() {
    if (mCamera != null) {
      Log.d(TAG, "start camera, already started. return");
      return;
    }
    if (mCamId > (Camera.getNumberOfCameras() - 1) || mCamId < 0) {
      Log.e(TAG, "####### start camera failed, inviald params, camera No.="+ mCamId);
      return;
    }

    mCamera = Camera.open(mCamId);
    //mCamera.setDisplayOrientation(-90);
    Camera.Parameters params = mCamera.getParameters();
  		/* preview size  */
    Camera.Size size = mCamera.new Size(SrsEncoder.VPREV_WIDTH, SrsEncoder.VPREV_HEIGHT);
    if (!params.getSupportedPreviewSizes().contains(size)) {
      Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),
          new IllegalArgumentException(String.format("Unsupported preview size %dx%d", size.width, size.height)));
    }

          /* picture size  */
    if (!params.getSupportedPictureSizes().contains(size)) {
      Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),
          new IllegalArgumentException(String.format("Unsupported picture size %dx%d", size.width, size.height)));
    }

    /***** set parameters *****/
    //params.set("orientation", "portrait");
    //params.set("orientation", "landscape");
    //params.setRotation(90);
    params.setPictureSize(SrsEncoder.VPREV_WIDTH, SrsEncoder.VPREV_HEIGHT);
    params.setPreviewSize(SrsEncoder.VPREV_WIDTH, SrsEncoder.VPREV_HEIGHT);
    int[] range = findClosestFpsRange(SrsEncoder.VFPS, params.getSupportedPreviewFpsRange());
    params.setPreviewFpsRange(range[0], range[1]);
    params.setPreviewFormat(SrsEncoder.VFORMAT);
    params.setRotation(90);
    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
    params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
    if (!params.getSupportedFocusModes().isEmpty()) {
      params.setFocusMode(params.getSupportedFocusModes().get(0));
    }
    mCamera.setParameters(params);

    mCamera.setDisplayOrientation(mPreviewRotation);

    mCamera.addCallbackBuffer(mYuvFrameBuffer);
    //mCamera.setDisplayOrientation(90);
    mCamera.setPreviewCallbackWithBuffer(this);
    try {
      mCamera.setPreviewDisplay(surfaceView.getHolder());
    } catch (IOException e) {
      e.printStackTrace();
    }
    mCamera.startPreview();
  }

  private void stopCamera() {
    if (mCamera != null) {
      // need to SET NULL CB before stop preview!!!
      mCamera.setPreviewCallback(null);
      mCamera.stopPreview();
      mCamera.release();
      mCamera = null;
    }
  }

  private void onGetYuvFrame(byte[] data) {
    // Calculate YUV sampling FPS
    if (videoFrameCount == 0) {
      lastTimeMillis = System.nanoTime() / 1000000;
      videoFrameCount++;
    } else {
      if (++videoFrameCount >= 48) {
        long diffTimeMillis = System.nanoTime() / 1000000 - lastTimeMillis;
        Log.i(TAG, String.format("Sampling fps: %f", (double) videoFrameCount * 1000 / diffTimeMillis));
        videoFrameCount = 0;
      }
    }
    mEncoder.onGetYuvFrame(data);
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera c) {
    byte[] out = new byte[data.length];
    rotateNV21(data, out, 480, 640, 180);
    onGetYuvFrame(out);
    c.addCallbackBuffer(mYuvFrameBuffer);
  }

  private void onGetPcmFrame(byte[] pcmBuffer, int size) {
    mEncoder.onGetPcmFrame(pcmBuffer, size);
  }

  public static void rotateNV21(byte[] input, byte[] output, int width, int height, int rotation) {
    boolean swap = (rotation == 90 || rotation == 270);
    boolean yflip = (rotation == 90 || rotation == 180);
    boolean xflip = (rotation == 270 || rotation == 180);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int xo = x, yo = y;
        int w = width, h = height;
        int xi = xo, yi = yo;
        if (swap) {
          xi = w * yo / h;
          yi = h * xo / w;
        }
        if (yflip) {
          yi = h - yi - 1;
        }
        if (xflip) {
          xi = w - xi - 1;
        }
        output[w * yo + xo] = input[w * yi + xi];
        int fs = w * h;
        int qs = (fs >> 2);
        xi = (xi >> 1);
        yi = (yi >> 1);
        xo = (xo >> 1);
        yo = (yo >> 1);
        w = (w >> 1);
        h = (h >> 1);
        // adjust for interleave here
        int ui = fs + (w * yi + xi) * 2;
        int uo = fs + (w * yo + xo) * 2;
        // and here
        int vi = ui + 1;
        int vo = uo + 1;
        output[uo] = input[ui];
        output[vo] = input[vi];
      }
    }
  }

  private void startAudio() {
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

  private void stopAudio() {
    aloop = false;
    if (aworker != null) {
      Log.i(TAG, "stop audio worker thread");
      aworker.interrupt();
      try {
        aworker.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
        aworker.interrupt();
      }
      aworker = null;
    }

    if (mic != null) {
      mic.setRecordPositionUpdateListener(null);
      mic.stop();
      mic.release();
      mic = null;
    }
  }

  private void startEncoder() {
    if (!mEncoder.start()) {
      return;
    }

    startCamera();

    aworker = new Thread(new Runnable() {
      @Override
      public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        startAudio();
      }
    });
    aloop = true;
    aworker.start();
  }

  private void stopEncoder() {
    stopAudio();
    stopCamera();
    mEncoder.stop();
  }

  private static int[] findClosestFpsRange(int expectedFps, List<int[]> fpsRanges) {
    expectedFps *= 1000;
    int[] closestRange = fpsRanges.get(0);
    int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
    for (int[] range : fpsRanges) {
      if (range[0] <= expectedFps && range[1] >= expectedFps) {
        int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
        if (curMeasure < measure) {
          closestRange = range;
          measure = curMeasure;
        }
      }
    }
    return closestRange;
  }

  private static String getRandomAlphaString(int length) {
    String base = "abcdefghijklmnopqrstuvwxyz";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  private static String getRandomAlphaDigitString(int length) {
    String base = "abcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.d(TAG, "surfaceChanged");
  }

  @Override
  public void surfaceCreated(SurfaceHolder arg0) {
    Log.d(TAG, "surfaceCreated");
    if (mCamera != null) {
      try {
        mCamera.setPreviewDisplay(surfaceView.getHolder());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder arg0) {
    Log.d(TAG, "surfaceDestroyed");
  }
}
