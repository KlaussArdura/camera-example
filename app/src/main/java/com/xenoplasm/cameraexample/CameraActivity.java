package com.xenoplasm.cameraexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {
    private final int PERMISSION_REQUEST_CODE = 1234;

    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        checkPermissions();
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE);
            } else {
                startup();
            }
        } else {
            startup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length < 2
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Deben aceptarse los permisos para usar la aplicación", Toast.LENGTH_LONG)
                            .show();
                    finish();
                } else {
                    startup();
                }
            }
        }
    }

    private void startup() {
        camera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        final CameraPreview cameraPreview = new CameraPreview(this, camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);
    }

    public void takePhoto(View v) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                // Photo file where is going to be saved
                File photoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera", new SimpleDateFormat("ddMMyyHHmmss").format(new Date()) + ".jpeg");

                try {
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    fos.write(data);
                    fos.close();
                    Toast.makeText(CameraActivity.this, "Foto guardada: " + photoFile,
                            Toast.LENGTH_SHORT).show();
                } catch (Exception error) {
                    Toast.makeText(CameraActivity.this, "Error:" + photoFile,
                            Toast.LENGTH_LONG).show();
                    Log.e(getClass().getSimpleName(), "Archivo " + photoFile.getName() + " no guardado: "
                            + error.getMessage());
                } finally {
                    // Restart preview to avoid framelayout freeze
                    camera.stopPreview();
                    camera.startPreview();
                }
            }
        });
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(findFrontFacingCamera());
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private int findFrontFacingCamera() {
        int cameraId = 0;
        // Search for the back facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();       // release the camera for other applications
            camera = null;
        }
    }

    @Override
    public void finish() {
        releaseCamera();
        super.finish();
    }

}
