package com.xenoplasm.cameraexample;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;


public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener {

    private Camera camera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.setSurfaceTextureListener(this);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size cs = sizes.get(0);
            parameters.setPreviewSize(cs.width, cs.height);

            camera.setParameters(parameters);
            camera.setPreviewTexture(surface);
            camera.startPreview();

        } catch (IOException ioe) {
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }
}