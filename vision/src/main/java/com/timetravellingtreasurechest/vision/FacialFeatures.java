package com.timetravellingtreasurechest.vision;

import java.util.Arrays;
import java.util.List;

import com.timetravellingtreasurechest.features.Eyes;
import com.timetravellingtreasurechest.features.Face;
import com.timetravellingtreasurechest.features.FacialFeature;
import com.timetravellingtreasurechest.features.Mouth;
import com.timetravellingtreasurechest.features.Nose;

import static com.googlecode.javacv.cpp.opencv_core.*;

public class FacialFeatures {
	private Face face;
	private Mouth mouth;
	private Nose nose;
	private Eyes eyes;

	private double foreheadHeight;
	private double eyeSpace;

	public FacialFeatures(CvMat image) {
		if(image == null) return;
		// detect face first to create roi to search for facial features, can
		// greatly increase speed (esp for large images)
		face = FacialFeature.getImage(Face.class, image);
		
		// if no face detected, abort
		if (face == null) {
			return;
		}
		eyes = FacialFeature.getFromImageAndFace(Eyes.class, image, face);
		mouth = FacialFeature.getFromImageAndFace(Mouth.class, image, face);
		nose = FacialFeature.getFromImageAndFace(Nose.class, image, face);
		eyeSpace = eyes.getEyeSpacing(face);
		foreheadHeight = eyes.getForeheadHeight(face);
	}
	
	public Eyes getEyes() {
		return eyes;
	}
	public Mouth getMouth() {
		return mouth;
	}
	public Nose getNose() {
		return nose;
	}
	public Face getFace() {
		return face;
	}
	public double getForeheadHeight() {
		return foreheadHeight;
	}
	public double getEyeSpacing() {
		return eyeSpace;
	}

	public List<FacialFeature<?>> getFeatures() {
		return Arrays.asList(face, eyes, nose, mouth);
	}
}