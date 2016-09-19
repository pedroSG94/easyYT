package com.pedro.easyyt.domain.interactor.yasea;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.ossrs.yasea.SrsEncoder;

import java.io.IOException;

/**
 * Created by pedro on 27/07/16.
 */
public class EasyYTView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private final String TAG = "EasyYTView";

    private int videoFrameCount;
    private long lastTimeMillis;
    private Camera camera;
    private byte[] mYuvFrameBuffer = new byte[SrsEncoder.VPREV_WIDTH * SrsEncoder.VPREV_HEIGHT * 3 / 2];
    private SrsEncoder encoder;

    public EasyYTView(Context context) {
        super(context);
    }

    public EasyYTView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyYTView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEncoder(SrsEncoder encoder) {
        this.encoder = encoder;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        try {
            this.camera.setPreviewDisplay(getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Camera.PreviewCallback getPreviewCallback(){
        return this;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if(camera != null && encoder != null) {
            onGetYuvFrame(bytes);
            camera.addCallbackBuffer(mYuvFrameBuffer);
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
        encoder.onGetYuvFrame(data);
    }
}
