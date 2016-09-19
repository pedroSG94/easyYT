package com.pedro.easyyt.domain.interactor.yasea;

import android.hardware.Camera;
import android.util.Log;

import net.ossrs.yasea.SrsEncoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by pedro on 27/07/16.
 */
public class CameraControls {

    private final String TAG = "CameraControls";
    private int camId = Camera.getNumberOfCameras() - 1; // default camera
    private Camera camera;
    private Camera.PreviewCallback previewCallback;
    private int rotation = 90;
    private byte[] mYuvFrameBuffer = new byte[SrsEncoder.VPREV_WIDTH * SrsEncoder.VPREV_HEIGHT * 3 / 2];
    private EasyYTView easyYTView;

    public CameraControls(EasyYTView easyYTView){
        this.easyYTView = easyYTView;
        previewCallback = easyYTView.getPreviewCallback();
    }

    public void startCamera() {
        if (camera != null) {
            Log.d(TAG, "start camera, already started. return");
            return;
        }
        if (camId > (Camera.getNumberOfCameras() - 1) || camId < 0) {
            Log.e(TAG, "####### start camera failed, inviald params, camera No.="+ camId);
            return;
        }

        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
		/* preview size  */
        Camera.Size size = camera.new Size(SrsEncoder.VPREV_WIDTH, SrsEncoder.VPREV_HEIGHT);
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
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        if (!params.getSupportedFocusModes().isEmpty()) {
            params.setFocusMode(params.getSupportedFocusModes().get(0));
        }
        camera.setParameters(params);

        camera.setDisplayOrientation(rotation);

        camera.addCallbackBuffer(mYuvFrameBuffer);
        camera.setPreviewCallbackWithBuffer(previewCallback);
        try {
            camera.setPreviewDisplay(easyYTView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    public void stopCamera() {
        if (camera != null) {
            // need to SET NULL CB before stop preview!!!
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public Camera getCamera() {
        return camera;
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
}
