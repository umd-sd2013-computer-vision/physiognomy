package com.timetravellingtreasurechest.services;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.timetravellingtreasurechest.features.FacialFeature;

public class ImageConverter {
	public static CvMat getCvMatFromRawImage(byte[] picture, Rect rect, boolean isNV21) {
		CvMat out = CvMat.create(rect.height(), rect.width(), CV_8UC4);

		// all this converts w/e format android camera captures with and
		// converts to a BGR
		if (isNV21) {
			if (rect.height() % 2 != 0)
				throw new RuntimeException("Odd height not supported");

			CvMat nv21Image = CvMat.create((int)(rect.height() * 1.5),
					rect.width(), CV_8UC1);

			nv21Image.getByteBuffer().put(picture);
			cvCvtColor(nv21Image, out, CV_YUV2RGBA_NV21);
			nv21Image.release();
		} else {
			Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0,
					picture.length);
			if (bitmap.getConfig() == Bitmap.Config.RGB_565) {
				CvMat rgb565Image = CvMat.create(rect.height(), rect.width(),
						CV_8UC2);
				bitmap.copyPixelsToBuffer(rgb565Image.getByteBuffer());
				cvCvtColor(rgb565Image, out, opencv_imgproc.CV_BGR5652RGBA);
				rgb565Image.release();
			} else if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
				bitmap.copyPixelsToBuffer(out.getByteBuffer());
			} else
				throw new RuntimeException("Unsupported bitmap config: "
						+ bitmap.getConfig());

			bitmap.recycle();
		}
		
		return out;
	}
	
	public static CvMat cvMatResize(CvMat src, int new_height) {
		double aspect = ((double) src.rows()) / src.cols();
		int height = new_height;
		int width = (int) (((double) new_height) / aspect);
		
	    CvMat dest = cvCreateMat(height, width, src.type());
	    cvResize(src, dest, CV_INTER_LINEAR);
	    return dest;
	}
	
	public static Bitmap cvMatToBitmap(CvMat in) {
		Bitmap bmp = Bitmap.createBitmap(in.cols(), in.rows(), Bitmap.Config.ARGB_8888);
		bmp.copyPixelsFromBuffer(in.getByteBuffer());
		return bmp;		
	}
	
	public static CvMat cvGetFace(CvMat in, CvRect face) {
		double aspect = ((double) in.rows()) / in.cols();
		int smallHeight = 400;
		int smallWidth = (int) (((double) smallHeight) / aspect);
		int scale = in.rows() / smallHeight;
		
		CvRect largeFace = new CvRect(face.x() * scale, face.y() * scale, face.width() * scale, face.height() * scale);
		
		return FacialFeature.crop(in, largeFace);
	}
	
//	public static CvMat bitmapToCvMat(Bitmap in) {
//		CvMat out;
//		out = CvMat.create(in.getWidth(), in.getHeight(), CV_8UC2);
//		in.copyPixelsToBuffer(out.asBuffer());
//		return out;
//	}
	
//	public static CvMat byteArrayToCvMat(byte[] in, int rows, int cols, int depth, int channels) {
//		CvMat image = CvMat.create(rows, cols, depth, channels);
//		image.getByteBuffer().put(in);
//		return image;
//	}
//	
//	public static byte[] cvMatToByteArray(CvMat in) {		
//		byte[] image = new byte[in.getByteBuffer().capacity()];
//		in.getByteBuffer().get(image, 0, image.length);
//		return image;
//	}
}
