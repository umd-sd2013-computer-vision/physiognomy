package com.timetravellingtreasurechest.camera;


import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements Callback {

	private SurfaceHolder holder;
    private Camera camera;
    private Integer cameraId;
    private Parameters workingParams;
	
	public Preview(Context context, int cameraId) {
		super(context);
		
		this.holder = this.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.camera = Camera.open(cameraId);
        this.cameraId = cameraId;
        this.workingParams = camera.getParameters();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // Now that the size is known, set up the camera parameters and begin
        // the preview.
		camera.stopPreview();
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
        camera.setParameters(parameters);
        camera.startPreview();
	}
	
	public void startPreview() {
		if(camera == null) {
			setupCameraView();
		}
		camera.startPreview(); 
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		setupCameraView();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
        stop();
	}
	
	public void setupCameraView() {
		try
        {
            //Open the Camera in preview mode
			stop();
            this.camera = Camera.open(this.cameraId);
            camera.setParameters(this.workingParams);
            this.camera.setPreviewDisplay(this.holder);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace(System.out);
        }
	}
	
	public void stop() {
		if(camera != null) {
			camera.stopPreview();
	        camera.release();
	        camera = null;
		}
	}
	

	public Camera getCamera() {
            return this.camera;
    }

	public Parameters getParameters() {
		return this.workingParams;
	}
	
	public void setParameters(Parameters newParams) {
		this.workingParams = newParams;
		if(camera != null)
			camera.setParameters(this.workingParams);
	}
	
	public Camera setCamera(int cameraId) {
		if(cameraId != this.cameraId) {
			stop();
			this.cameraId = cameraId;
			setupCameraView();
		}
		return camera;
	}
	public Integer getCameraId() {
		return cameraId;
	}

}
