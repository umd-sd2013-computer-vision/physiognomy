package com.timetravellingtreasurechest.vision;

public class ReportData {
	
	private static final double AVG_FOREHEAD_HEIGHT = 0.2969582371113202;
	private static final double AVG_EYE_SIZE = 0.05727096626510349;
	private static final double AVG_EYE_SPACE = 0.38660547183984056;
	private static final double AVG_NOSE_SIZE = 0.08849819639003767;
	private static final double AVG_NOSE_HEIGHT = 0.26920041703726744;
	private static final double AVG_NOSE_WIDTH = 0.3240249760921849;
	private static final double AVG_MOUTH_SIZE = 0.09566388018849253;
	private static final double AVG_MOUTH_HEIGHT = 0.2359406533854216;
	private static final double AVG_MOUTH_WIDTH = 0.39466172485312856;
	
	private static final double RANGE_FOREHEAD_HEIGHT = 0.01698916518799997;
	private static final double RANGE_EYE_SIZE = 0.01154345017140788;
	private static final double RANGE_EYE_SPACE = 0.0315783722604372;
	private static final double RANGE_NOSE_SIZE = 0.021015865906068354;
	private static final double RANGE_NOSE_HEIGHT = 0.032709543593996644;
	private static final double RANGE_NOSE_WIDTH = 0.03904940791865713;
	private static final double RANGE_MOUTH_SIZE = 0.03084221971405636;
	private static final double RANGE_MOUTH_HEIGHT = 0.03918164958657194;
	private static final double RANGE_MOUTH_WIDTH = 0.06522356424520552;
	
	private String report = "";
	
	public ReportData(FacialFeatures face) {		
		
		// determine if features are above/below average, this will output to console for now (this will be changed later)
		calcFeature(AVG_FOREHEAD_HEIGHT, RANGE_FOREHEAD_HEIGHT, face.foreheadHeight, "forehead height");
		calcFeature(AVG_EYE_SIZE, RANGE_EYE_SIZE, face.eyeSize, "eye size");
		calcFeature(AVG_EYE_SPACE, RANGE_EYE_SPACE, face.eyeSpace, "eye space");
		calcFeature(AVG_NOSE_SIZE, RANGE_NOSE_SIZE, face.noseSize, "nose size");
		calcFeature(AVG_NOSE_HEIGHT, RANGE_NOSE_HEIGHT, face.noseHeight, "nose height");
		calcFeature(AVG_NOSE_WIDTH, RANGE_NOSE_WIDTH, face.noseWidth, "nose width");
		calcFeature(AVG_MOUTH_SIZE, RANGE_MOUTH_SIZE, face.mouthSize, "mouth size");
		calcFeature(AVG_MOUTH_HEIGHT, RANGE_MOUTH_HEIGHT, face.mouthHeight, "mouth height");
		calcFeature(AVG_MOUTH_WIDTH, RANGE_MOUTH_WIDTH, face.mouthWidth, "mouth width");
	}
	
	public String getReportText() {
		return report;
	}
	
	// determines if feature is above/below average
	// this manipulates the report string for now
	// later on probably best to just return whether a value is above or below the average
	private void calcFeature(double avg, double range, double val, String feature) {
		if (val > avg + range)
			report += "above average " + feature + ". ";
		if (val < avg - range)
			report += "below average " + feature + ". ";
	}
}
