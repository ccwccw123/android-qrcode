package com.example.qrcodeactivity.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.qrcodeactivity.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by ccw on 19/12/25.
 */
public class QrcodeActivity extends AppCompatActivity {

    public static void startActivity(Activity context) {
        new RxPermissions(context)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = new Intent(context, QrcodeActivity.class);
                        context.startActivity(intent);
                    }
                });
    }

    public static void startActivity(Activity context, int requestCode) {
        new RxPermissions(context)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        Intent intent = new Intent(context, QrcodeActivity.class);
                        intent.putExtra("type", 1);
                        context.startActivityForResult(intent, requestCode);
                    }
                });
    }

    //扩展闪光灯，边框，扫码线，去掉红线
    private CompoundBarcodeView barcodeView;
    private ImageView flashlight;
    private int mType;
    private boolean isLightOn = false;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
            if (result != null) {
                //这里写业务逻辑
                Log.d("wewq", result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);
        new RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {

                    }
                });
        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        flashlight = (ImageView) findViewById(R.id.flashlight);
        barcodeView.decodeContinuous(callback);
        Intent intent = new Intent();
        barcodeView.initializeFromIntent(intent);
        mType = getIntent().getIntExtra("type", 0);
        flashlight.setOnClickListener(v -> {
            if (isLightOn) {
                barcodeView.setTorchOff();
                flashlight.setImageResource(R.drawable.light_close);
            } else {
                barcodeView.setTorchOn();
                flashlight.setImageResource(R.drawable.light_open);
            }
            isLightOn = !isLightOn;
        });
        // 如果没有闪光灯功能，就去掉相关按钮
        if (!hasFlash()) {
            flashlight.setVisibility(View.GONE);
        }

    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


}
