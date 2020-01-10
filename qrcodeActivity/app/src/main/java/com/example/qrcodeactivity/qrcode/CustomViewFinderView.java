package com.example.qrcodeactivity.qrcode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.example.qrcodeactivity.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:重写扫描框类
 * author：ccw
 * date:2019-12-27
 * time:22:45
 */
public class CustomViewFinderView extends ViewfinderView {
    private int screenWidth = 0;
    private int screenHeight = 0;
    private RectF buttonRect;
    private String text_btn = "个性化需求";
    /**
     * 重绘时间间隔
     */
    private static long CUSTOME_ANIMATION_DELAY = 16;
    /**
     * 边角线颜色
     */

    private int mLineColor = getContext().getResources().getColor(R.color.c_a_green);
    /**
     * 线性梯度各个位置对应的颜色值
     */
    private Rect nowScanRect;
    /**
     * 边角线厚度 (建议使用dp)
     */

    private float mLineDepth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics());
    /**
     * "边角线长度/扫描边框长度"的占比 (比例越大，线越长)
     */

    private float mLineRate = 0.05f;
    /**
     * 扫描线起始位置
     */

    private int mScanLinePosition = 0;
    /**
     * 扫描线每次重绘的移动距离
     */

    private float mScanLineDy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics());

    /**
     * 线性梯度
     */
    private LinearGradient mLinearGradient;
    /**
     * 线性梯度位置
     */

    private float[] mPositions = {0f, 0.5f, 1f};

    /**
     * 扫描线厚度
     */

    private float mScanLineDepth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics());
    //扫描线渐变色
    private int laserColor_center = getContext().getResources().getColor(R.color.c_a_green);

    private int laserColor_light = getContext().getResources().getColor(R.color.c_a_green);

    private int[] mScanLineColor = {laserColor_light, laserColor_center, laserColor_light};


    private float mDist = 0;

    public CustomViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        WindowManager windowManager = ((Activity) getContext()).getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        int w = screenWidth * 3 / 5;
        int marginL = screenWidth / 5;
        int marginT = screenHeight / 4;
        nowScanRect = new Rect(marginL, marginT, w + marginL, w + marginT);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        refreshSizes();
        if (framingRect == null || previewFramingRect == null) {
            return;
        }
        Rect frame = nowScanRect;
      //  Rect frame =  cameraPreview.getFramingRect();
        Rect previewFrame = previewFramingRect;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (resultBitmap != null) {
            paint.setColor(resultColor);
        } else {
            paint.setColor(maskColor);
        }

        canvas.drawRect(0f, 0f, width, frame.top, paint);
        canvas.drawRect(0f, frame.top, frame.left, (frame.bottom + 1), paint);
        canvas.drawRect((frame.right + 1), frame.top, width, (frame.bottom + 1), paint);
        canvas.drawRect(0f, (frame.bottom + 1), width, height, paint);

        //  drawText(canvas, frame)

      //  drawButton(canvas, frame);
        ////绘制4个角
        paint.setColor(mLineColor);
//
        // 定义画笔的颜色
        //左上-横线
        canvas.drawRect(frame.left - mLineDepth,
                frame.top - mLineDepth,
                frame.left + frame.width() * mLineRate,
                frame.top, paint);
        //左上-纵线
        canvas.drawRect(frame.left - mLineDepth, frame.top, frame.left, frame.top + frame.height() * mLineRate, paint);
        //右上-横线
        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.top - mLineDepth, frame.right + mLineDepth, frame.top, paint);
        //右上-纵线
        canvas.drawRect(frame.right, frame.top - mLineDepth, frame.right + mLineDepth, frame.top + frame.height() * mLineRate, paint);
        //左下-横线
        canvas.drawRect(frame.left - mLineDepth, frame.bottom, frame.left + frame.width() * mLineRate, frame.bottom + mLineDepth, paint);
        //左下-纵线
        canvas.drawRect(frame.left - mLineDepth, frame.bottom - frame.height() * mLineRate, frame.left, frame.bottom, paint);
        //右下-横线
        canvas.drawRect(frame.right - frame.width() * mLineRate, frame.bottom, frame.right + mLineDepth, frame.bottom + mLineDepth, paint);
        //右下-纵线
        canvas.drawRect(frame.right, frame.bottom - frame.height() * mLineRate, frame.right + mLineDepth, frame.bottom + mLineDepth, paint);
        if (resultBitmap != null) {
            paint.setAlpha(ViewfinderView.CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            //  drawLaserLine(canvas,frame)
            // 绘制扫描线
            mScanLinePosition += mScanLineDy;
            if (mScanLinePosition > frame.height()) {
                mScanLinePosition = 0;
            }

            mLinearGradient = new LinearGradient(frame.left, (frame.top + mScanLinePosition), frame.right, (frame.top + mScanLinePosition), mScanLineColor, mPositions, Shader.TileMode.CLAMP);

            paint.setShader(mLinearGradient);

            canvas.drawRect(frame.left, (frame.top + mScanLinePosition), frame.right, frame.top + mScanLinePosition + mScanLineDepth, paint);

            paint.setShader(null);

            int scaleX = frame.width() / previewFrame.width();
            int scaleY = frame.height() / previewFrame.height();
            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints=null;
            } else {
                possibleResultPoints = new ArrayList(5);

                lastPossibleResultPoints = currentPossible;

                paint.setAlpha(ViewfinderView.CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle((frameLeft + (point.getX() * scaleX)), (frameTop + (point.getY() * scaleY)), ViewfinderView.POINT_SIZE, paint);
                }

            }

            if (currentLast != null) {
                paint.setAlpha(ViewfinderView.CURRENT_POINT_OPACITY / 2);

                paint.setColor(resultPointColor);

                float radius = ViewfinderView.POINT_SIZE / 2.0f;

                for (ResultPoint point : currentLast) {
                    canvas.drawCircle((frameLeft + (point.getX() * scaleX)), (frameTop + (point.getY() * scaleY)), radius, paint);
                }

            }

        }// Request another update at the animation interval, but only repaint the laser line, // not the entire viewfinde

// rmask.
        postInvalidateDelayed(CUSTOME_ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);

    }

    private void drawButton(Canvas canvas, Rect mScanRect) {
        Paint buttonPaint = new Paint();
        buttonPaint.setAntiAlias(true);
        buttonPaint.setColor(getContext().getResources().getColor(R.color.c_white));
        buttonPaint.setStrokeWidth(1f);
        buttonPaint.setStyle(Paint.Style.STROKE);

        int height = CommonUtils.dp2px(getContext(), 40);

        int left = mScanRect.left + (mScanRect.right - mScanRect.left) / 6;

        int top = mScanRect.bottom + CommonUtils.dp2px(getContext(), 48);

        int right = mScanRect.right - (mScanRect.right - mScanRect.left) / 6;

        int bottom = mScanRect.bottom + CommonUtils.dp2px(getContext(), 48) + height;

        buttonRect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(buttonRect, CommonUtils.dp2px(getContext(), 20), CommonUtils.dp2px(getContext(), 20), buttonPaint);


        buttonPaint.setColor(getContext().getResources().getColor(R.color.c_white));
        buttonPaint.setTextSize(CommonUtils.dp2px(getContext(), 14));
        buttonPaint.setStyle(Paint.Style.FILL);
        buttonPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetricsInt fontMetrics = buttonPaint.getFontMetricsInt();

        // var baseLine = buttonRect.centerY() + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
        int baseLine = (int) ((buttonRect.top + buttonRect.bottom - fontMetrics.top - fontMetrics.bottom) / 2);
        canvas.drawText(text_btn, buttonRect.centerX(), baseLine, buttonPaint);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

//        override fun onTouchEvent(event: MotionEvent?): Boolean {
//
//        when (event?.action) {
//
//            MotionEvent.ACTION_DOWN -> {
//
//                val x = event?.x
//
//                                val y = event?.y
//
//                                buttonRect?.let {
//
//                                        if (it.contains(x, y)) {
//
//                        inputView?.inputClick()
//
//                    }
//
//                }
//            }
//
//        }
//        return super.onTouchEvent(event)
//    }
}
