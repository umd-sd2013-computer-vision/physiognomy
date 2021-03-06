package com.timetravellingtreasurechest;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements Callback {

	private SurfaceHolder holder;
    private Camera camera;
	
	@SuppressWarnings("deprecation")
	public Preview(Context context) {
		super(context);
		
		this.holder = this.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        
        parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
        camera.setParameters(parameters);
        camera.startPreview();
	}
	
	public void startPreview() { camera.startPreview(); }

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
            //Open the Camera in preview mode			
            camera = Camera.open();
            camera.setDisplayOrientation(90);            
            camera.setPreviewDisplay(this.holder);
        } catch(IOException ioe) {
        	ioe.printStackTrace(System.out);
        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// Surface will be destroyed when replaced with a new screen
        //Always make sure to release the Camera instance
        camera.stopPreview();
        camera.release();
        camera = null;

	}

	public Camera getCamera() {
		return this.camera;
    }

}
