package com.grievancesystem.speakout.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.databinding.ActivityMessBinding;
import com.grievancesystem.speakout.databinding.ActivityQRCodeBinding;
import com.grievancesystem.speakout.utility.Helper;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRCodeActivity extends AppCompatActivity {
ActivityQRCodeBinding qrCodeBinding;
CodeScanner codeScanner;
boolean save;
private static final int MY_CAMERA_REQUEST_CODE = 100;


    @Override
    protected void onResume() {
        super.onResume();
        if (Prefs.getUser(this).getUserType()==Helper.USER_ADMINISTRATOR){
            requestForCamera();

        }
    }
    public void requestForCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
            else {
                codeScanner.startPreview();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrCodeBinding= ActivityQRCodeBinding.inflate(getLayoutInflater());
        setContentView(qrCodeBinding.getRoot());

        getSupportActionBar().setTitle("QR CODE");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        if (Prefs.getUser(this).getUserType()==Helper.USER_ADMINISTRATOR){
            codeScanner=new CodeScanner(this,qrCodeBinding.scannerView);
            qrCodeBinding.qrCode.setVisibility(View.GONE);
            qrCodeBinding.textqr.setVisibility(View.GONE);
            qrCodeBinding.scannerView.setVisibility(View.VISIBLE);

            codeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeBinding.resultsOfQr.setVisibility(View.VISIBLE);
                            qrCodeBinding.resultsOfQr.setText(result.getText());
                        }
                    });
                }
            });
            qrCodeBinding.scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    codeScanner.startPreview();
                }
            });
        }
        else{
            QRGEncoder encoder=new QRGEncoder(Prefs.getUser(this).getUid(),null, QRGContents.Type.TEXT,500);
            try {
                Bitmap bitmap=encoder.encodeAsBitmap();
                qrCodeBinding.qrCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }




    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Helper.toast(this, "Camera Permission Granted");
                codeScanner.startPreview();
            } else {
                Helper.toast(this, "Camera Permission Denied");
            }
        }
    }

}