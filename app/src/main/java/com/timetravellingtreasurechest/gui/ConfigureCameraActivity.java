package com.timetravellingtreasurechest.gui;

import java.util.List;

import com.timetravellingtreasurechest.app.MainActivity;
import com.timetravellingtreasurechest.app.R;
import com.timetravellingtreasurechest.camera.Preview;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.ExpandableListView;

public class ConfigureCameraActivity extends Activity {
	List<String> flashModes;
	Preview configPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config_camera);
		configPreview = new Preview(this, Camera.CameraInfo.CAMERA_FACING_BACK);
		FrameLayout mainPreview = (FrameLayout) this
				.findViewById(R.id.PreviewCameraSelect);
		configPreview.setCamera(MainActivity.cameraSurfaceView.getCameraId());
		configPreview.setParameters(MainActivity.cameraSurfaceView
				.getParameters());
		mainPreview.addView(configPreview);

		if (MainActivity.cameraSurfaceView == null
				|| MainActivity.cameraSurfaceView.getCamera() == null) {
			throw new RuntimeException("Error in creating camera");
		}

		flashModes = configPreview.getParameters().getSupportedFlashModes();

		// Current flash mode
		if (flashModes.size() <= 1) {
			this.findViewById(R.id.flash_options).setVisibility(View.GONE);
		} else {
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.expandable_list_content, flashModes);
			((ExpandableListView) this.findViewById(R.id.flash_options))
					.setAdapter(adapter);
			((ExpandableListView) this.findViewById(R.id.flash_options))
					.setOnChildClickListener(new OnChildClickListener() {
						@Override
						public boolean onChildClick(ExpandableListView parent,
								View v, int groupPosition, int childPosition,
								long id) {
							configPreview.getParameters().setFlashMode(
									flashModes.get(childPosition));
							MainActivity.cameraSurfaceView
									.setParameters(configPreview
											.getParameters());
							return false;
						}
					});
		}
		// Current camera
		if (Camera.getNumberOfCameras() <= 1) {
			this.findViewById(R.id.select_camera).setVisibility(View.GONE);
		} else {
			this.findViewById(R.id.default_camera).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
						}
					});
			this.findViewById(R.id.other_camera).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

						}
					});
			if (MainActivity.cameraSurfaceView.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				((RadioButton) this.findViewById(R.id.default_camera))
						.setSelected(false);
				((RadioButton) this.findViewById(R.id.other_camera))
						.setSelected(true);
			} else {
				((RadioButton) this.findViewById(R.id.other_camera))
						.setSelected(false);
				((RadioButton) this.findViewById(R.id.default_camera))
						.setSelected(true);
			}
		}
	}

	private void switchCamera(int cameraOption) {
		if (cameraOption != MainActivity.cameraSurfaceView.getCameraId()) {
			MainActivity.cameraSurfaceView.setCamera(cameraOption);
			configPreview.setCamera(cameraOption);
			configPreview.startPreview();
		}
	}
}
