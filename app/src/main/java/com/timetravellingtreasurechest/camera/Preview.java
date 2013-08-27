package com.timetravellingtreasurechest.camera;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.timetravellingtreasurechest.services.ServiceServer;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

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
//        parameters.setPreviewSize(width, height);
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
        
        ServiceServer.getAndroidContext();
		Display display = ((WindowManager)ServiceServer.getAndroidContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation() == Surface.ROTATION_0)
            camera.setDisplayOrientation(90);
        else if(display.getRotation() == Surface.ROTATION_270)
            camera.setDisplayOrientation(180);

        // basically we are sizing the cvmat to 1200 cause of memory issues, if we take a picture of the
        // smallest size nearst 1200 x 1200 we can not toss around a really really large byte[] that gets
        // scaled down anyways
        List<Size> sizes = parameters.getSupportedPictureSizes(); // android orders this largest to smallest
        Camera.Size size = sizes.get(sizes.size()-1); // smallest picture size
        for (int i = sizes.size() - 2; i >= 0 && size.height < 1200; i--)
        	size = sizes.get(i);
        
        parameters.setPictureSize(size.width, size.height);
        
        System.out.println("camera size - width: " + size.width + "   height: " + size.height);
        
        camera.setParameters(parameters);        
        camera.startPreview();
	}
	
	public void startPreview() { camera.startPreview(); }

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try
        {
                //Open the Camera in preview mode
                this.camera = Camera.open();
                this.camera.setPreviewDisplay(this.holder);
        }
        catch(IOException ioe)
        {
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

	public Camera getCamera()
    {
            return this.camera;
    }

	public void stopPreview() {
		if (camera != null)
			camera.stopPreview();
	}

}
