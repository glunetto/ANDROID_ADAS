/*
###################################################################
#
#   Author: Lunetto Gaspare
#   Date:   17/09/15
#
###################################################################
*/

#include "common_defs.h"

/* || */
class Kalman_Filter
{
  private:
    cv::KalmanFilter _kf;
    cv::Mat_<float> _measurement;

  public:
    /* Constructors */
    Kalman_Filter() : _kf(4, 2, 0), _measurement(2, 1) {}

    /* Methods */
    void init(float x, float y)
    {
      _measurement = cv::Mat_<float>::zeros(2,1);
      _measurement.at<float>(0, 0) = x;
      _measurement.at<float>(1, 0) = y;

      _kf.statePre.setTo(0);
      _kf.statePre.at<float>(0, 0) = x;
      _kf.statePre.at<float>(1, 0) = y;

      _kf.statePost.setTo(0);
      _kf.statePost.at<float>(0, 0) = x;
      _kf.statePost.at<float>(1, 0) = y; 

      _kf.transitionMatrix = (cv::Mat_<float>(4, 4) << 1,0,1,0,   0,1,0,1,  0,0,1,0,  0,0,0,1);
      _kf.measurementMatrix = (cv::Mat_<float>(2, 4) << 1,0,0,0, 0,1,0,0);
      setIdentity(_kf.processNoiseCov, cv::Scalar::all(.005));
      setIdentity(_kf.measurementNoiseCov, cv::Scalar::all(1e-1));
      setIdentity(_kf.errorCovPost, cv::Scalar::all(.1));
    }

    cv::Point kalman_predict() 
    {
      cv::Mat prediction = _kf.predict();
      cv::Point predict_point(prediction.at<float>(0),prediction.at<float>(1));

      _kf.statePre.copyTo(_kf.statePost);
      _kf.errorCovPre.copyTo(_kf.errorCovPost);

      return predict_point;
    }

    cv::Point kalman_correct(float x, float y)
    {
      _measurement(0) = x;
      _measurement(1) = y;
      cv::Mat estimated = _kf.correct(_measurement);
      cv::Point state_point(estimated.at<float>(0),estimated.at<float>(1));

      return state_point;
    }
};
