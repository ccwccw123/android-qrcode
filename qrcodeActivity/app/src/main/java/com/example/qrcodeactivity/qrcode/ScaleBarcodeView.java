package com.example.qrcodeactivity.qrcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.camera.CameraInstance;

public class ScaleBarcodeView extends BarcodeView {

    private float mDist = 0;
    //扩展增加双击放大缩小操作 ccw
    private static int timeout = 300;//双击间三百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private Handler handler;

    public ScaleBarcodeView(Context context) {
        super(context);
    }

    public ScaleBarcodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
    }

    public ScaleBarcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE) {
                CameraInstance cameraInstance = getCameraInstance();
                if (cameraInstance != null) {
                    cameraInstance.changeCameraParameters(parameters -> {
                        handleZoom(event, parameters);
                        return parameters;
                    });
                }
            }
        } else if (event.getPointerCount() == 1) {
            if (action == MotionEvent.ACTION_DOWN) {
                clickCount++;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickCount == 2) {
                            CameraInstance cameraInstance = getCameraInstance();
                            if (cameraInstance != null) {
                                cameraInstance.changeCameraParameters(parameters -> {
                                    handleScaleZoom(parameters);
                                    return parameters;
                                });
                            }
                        }
                        handler.removeCallbacksAndMessages(null);
                        //清空handler延时，并防内存泄漏
                        clickCount = 0;//计数清零
                    }
                }, timeout);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
    }

    private void handleScaleZoom(Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        if (zoom < maxZoom / 2) {
            params.setZoom(maxZoom);
        } else {
            params.setZoom(1);
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


}
