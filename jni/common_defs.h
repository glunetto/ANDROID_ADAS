#ifndef __COMMON_DEFS__
#define __COMMON_DEFS__

/* Standard headers */
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>

/* openCV headers */
#include <opencv2/core/ocl.hpp>
#include <opencv2/core/utility.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/video/tracking.hpp>


/* detector TYPES */
enum TYPES {PEDESTRIAN, CYCLE, CAR};

/* Utilities */
#define TTL 5
#define TRESHOLD 20

#endif  /*  __COMMON_DEFS__  */
