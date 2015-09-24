package org.idra.adas;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

public class DetectionBasedTracker
{

    public void detect(Mat mRGBA, MatOfRect rects) 
    {
        nativeHOGDetector(mRGBA.getNativeObjAddr(), rects.getNativeObjAddr());
    }

    private static native void nativeHOGDetector(long input, long obj);
}
