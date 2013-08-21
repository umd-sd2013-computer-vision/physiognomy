package com.timetravellingtreasurechest.physiognomy.service;

import com.timetravellingtreasurechest.physiognomy.FacialFeatures;

public interface IFacialFeatureService {
	public FacialFeatures getFeatures(byte[] rawImage);
}
