#include <DetectionBasedTracker_jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;


JNIEXPORT void JNICALL Java_org_idra_adas_DetectionBasedTracker_nativeHOGDetector(JNIEnv *, jclass, jlong input, jlong obj)
{
	LOGD("HOG Detect");

	Mat in = *((Mat*)input);

	vector<Entity> items;

	// DETECTING

	LOGD("In order to detect..");
	items = detector.detect(in.getUMat(cv::ACCESS_READ));
	LOGD("Just detected..");

	vector<Rect> output;

	for(int i=0; i<items.size(); i++)
	{
		Entity temp = items[i];
		output.push_back(temp.found);
	}

	*((Mat*)obj) = Mat(output, true);
	output.clear();
	in.release();
}
