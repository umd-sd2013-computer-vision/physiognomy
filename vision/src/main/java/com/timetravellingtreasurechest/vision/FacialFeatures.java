package com.timetravellingtreasurechest.vision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.features.Eyes;
import com.timetravellingtreasurechest.features.Face;
import com.timetravellingtreasurechest.features.FacialFeature;
import com.timetravellingtreasurechest.features.Mouth;
import com.timetravellingtreasurechest.features.Nose;

import static com.googlecode.javacv.cpp.opencv_core.*;

public class FacialFeatures implements Serializable {
	private Face face;
	private Mouth mouth;
	private Nose nose;
	private Eyes eyes;

	private Double foreheadHeight = null;
	private Double eyeSpace = null;

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
	public Double getForeheadHeight() {
		return foreheadHeight;
	}
	public Double getEyeSpacing() {
		return eyeSpace;
	}

	public List<FacialFeature<?>> getFeatures() {
		
		List<FacialFeature<?>> list = new ArrayList<FacialFeature<?>>();
		if(face == null) return list;
		else list.add(face);
		if(eyes != null) list.add(eyes);
		if(nose != null) list.add(nose);
		if(mouth != null) list.add(mouth);
		return list;
	}

	public CvMat draw(CvMat image) {
		for(FacialFeature<?> f : getFeatures()) {
			f.drawBounds(image, face);
		}
		return image;
	}
}