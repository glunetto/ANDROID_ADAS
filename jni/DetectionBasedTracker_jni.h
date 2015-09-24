#include <jni.h>
#include "detector.h"

Detector detector;

#ifndef DETECTIONBASEDTRACKER_H
#define DETECTIONBASEDTRACKER_H
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_idra_adas_DetectionBasedTracker
 * Method:    nativeHOGDetector
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_idra_adas_DetectionBasedTracker_nativeHOGDetector(JNIEnv *, jclass, jlong, jlong);

#ifdef __cplusplus
}
#endif

#endif /* DETECTIONBASEDTRACKER_H */
