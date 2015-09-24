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


JNIEXPORT void JNICALL Java_org_idra_adas_DetectionBasedTracker_nativeHOGDetector(JNIEnv *, jclass, jlong thiz)
{
	LOGD("HOG Detect");
}
