package com.timetravellingtreasurechest.services;

import static com.googlecode.javacv.cpp.opencv_core.CV_8UC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC2;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC3;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC4;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR5652BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_YUV2BGR_NV21;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvSize;

public class ImageConverter {
	public static CvMat getCvMatFromRawImage(byte[] picture, CvSize size, boolean isNV21) {
		CvMat rgbImage;

		// all this converts w/e format android camera captures with and
		// converts to a BGR
		if (isNV21) {
			if (size.height() % 2 != 0)
				throw new RuntimeException("Odd height not supported");

			CvMat nv21Image = CvMat.create((int)(size.height() * 1.5),
					size.width(), CV_8UC1);
			rgbImage = CvMat.create(size.height(), size.width(), CV_8UC3);

			nv21Image.getByteBuffer().put(picture);
			cvCvtColor(nv21Image, rgbImage, CV_YUV2BGR_NV21);
			nv21Image.release();
		} else {
			Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0,
					picture.length);
			if (bitmap.getConfig() == Bitmap.Config.RGB_565) {
				CvMat rgb565Image = CvMat.create(size.height(), size.width(),
						CV_8UC2);
				bitmap.copyPixelsToBuffer(rgb565Image.getByteBuffer());
				rgbImage = CvMat.create(size.height(), size.width(), CV_8UC3);
				cvCvtColor(rgb565Image, rgbImage, CV_BGR5652BGR);
				rgb565Image.release();
			} else if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
				rgbImage = CvMat.create(size.height(), size.width(), CV_8UC4);
				bitmap.copyPixelsToBuffer(rgbImage.getByteBuffer());
			} else
				throw new RuntimeException("Unsupported bitmap config: "
						+ bitmap.getConfig());

			bitmap.recycle();
		}
		return rgbImage;
	}
}
