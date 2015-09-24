package org.idra.adas;

import org.opencv.core.Mat;

public class DetectionBasedTracker
{

    public void detect(Mat imageGray) 
    {
        nativeHOGDetector(imageGray.getNativeObjAddr());
    }

    private static native void nativeHOGDetector(long thiz);
}
