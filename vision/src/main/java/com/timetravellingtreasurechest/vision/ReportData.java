package com.timetravellingtreasurechest.vision;

public class ReportData {
	
	private static final double AVG_FOREHEAD_HEIGHT = 0.2939684744147797;
	private static final double AVG_EYE_SIZE = 0.057461373502253284;
	private static final double AVG_EYE_SPACE = 0.39088657211005245;
	private static final double AVG_NOSE_SIZE = 0.08370801614796489;
	private static final double AVG_NOSE_HEIGHT = 0.260168510692947;
	private static final double AVG_NOSE_WIDTH = 0.3132768055566671;
	private static final double AVG_MOUTH_SIZE = 0.09756818299160343;
	private static final double AVG_MOUTH_HEIGHT = 0.23787354797703603;
	private static final double AVG_MOUTH_WIDTH = 0.3984773779768884;
	
	private static final double RANGE_FOREHEAD_HEIGHT = 0.021091145309799105;
	private static final double RANGE_EYE_SIZE = 0.011491243527531217;
	private static final double RANGE_EYE_SPACE = 0.03275965635016891;
	private static final double RANGE_NOSE_SIZE = 0.029508910511422314;
	private static final double RANGE_NOSE_HEIGHT = 0.043005330034989644;
	private static final double RANGE_NOSE_WIDTH = 0.051401209584107696;
	private static final double RANGE_MOUTH_SIZE = 0.03278262029579936;
	private static final double RANGE_MOUTH_HEIGHT = 0.04088621229059656;
	private static final double RANGE_MOUTH_WIDTH = 0.06822046809329754;
	
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
		if (val <= 0.0)
			return;
		if (val > avg + range)
			report += "above average " + feature + ". ";
		if (val < avg - range)
			report += "below average " + feature + ". ";
	}
}
