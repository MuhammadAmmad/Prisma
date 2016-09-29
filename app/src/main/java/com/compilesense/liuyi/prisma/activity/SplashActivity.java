package com.compilesense.liuyi.prisma.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.compilesense.liuyi.prisma.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    public static final int REQUEST_PERMISSION = 11;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG,"onCreate");
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (!checkPermission()){
                Log.d(TAG,"checkPermission:no pass!");
                requestPermission();
            }else {
                Log.d(TAG,"checkPermission:pass!");
                startNext();
            }
        }else {
            startNext();
        }
    }

    //我用的华为手机出现重复调用onResume()的情况;
    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean checkPermission(){
        Log.d(TAG,"checkPermission");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"CAMERA");
            return false;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            Log.d(TAG,"WRITE_EXTERNAL_STORAGE");
            return false;
        }

        return true;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);

        ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startNext();
            }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                Log.e(TAG,"权限申请被拒绝");
            }
        }
    }

    private void startNext(){
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                Log.d("SplashActivity","finish");
                finish();
            }
        },1000);
    }
}
