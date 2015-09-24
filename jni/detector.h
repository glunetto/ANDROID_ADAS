/*
###################################################################
#
#   Authors: Lunetto Gaspare, Ciotti Giorgio, Zomparelli Simone
#   Date: 21/09/15
#
###################################################################
*/

#include "common_defs.h"
// 
/**/

struct Detected
{
  std::vector<cv::Rect> found;
  std::vector<enum TYPES> id;
};

class Detector
{
  private:
    bool _pedestrians;
    bool _cycles;
    bool _cars;
    cv::HOGDescriptor _hog_pedestrians;
    cv::HOGDescriptor _hog_cycles;
    cv::HOGDescriptor _hog_cars;

  public:
    /* Constructors */
    Detector () : _hog_pedestrians (cv::Size(48, 96), cv::Size(16, 16), cv::Size(8, 8), cv::Size(8, 8), 9, 1, -1, cv::HOGDescriptor::L2Hys, 0.2, true, cv::HOGDescriptor::DEFAULT_NLEVELS),
                  _hog_cycles (cv::Size(48, 96), cv::Size(16, 16), cv::Size(8, 8), cv::Size(8, 8), 9, 1, -1, cv::HOGDescriptor::L2Hys, 0.2, true, cv::HOGDescriptor::DEFAULT_NLEVELS),
                  _hog_cars (cv::Size(48, 96), cv::Size(16, 16), cv::Size(8, 8), cv::Size(8, 8), 9, 1, -1, cv::HOGDescriptor::L2Hys, 0.2, true, cv::HOGDescriptor::DEFAULT_NLEVELS)
    {
      _pedestrians = true;
      _cycles = false;
      _cars = false;
      _hog_pedestrians.setSVMDetector ( cv::HOGDescriptor::getDaimlerPeopleDetector() );
      //_hog_cycles.setSVMDetector ();
      //_hog_cars.setSVMDetector ();
    }

    /* Other methods, needed in order to elaborate the image */
    Detected detect (cv::UMat input)
    {
      Detected output;
      std::vector<cv::Rect> pedestrians, cars, cycles;

      if(_pedestrians) _hog_pedestrians.detectMultiScale(input.getMat(cv::ACCESS_READ), pedestrians, 1.4, cv::Size(8, 8), cv::Size(0, 0), 1.05, 8);
      for( size_t i = 0; i < pedestrians.size(); i++)
      {
        output.id.push_back(PEDESTRIAN);
        output.found.push_back(pedestrians.at(i));
      }

      /*if(_cycles) _hog_cycles.detectMultiScale(input.getMat(cv::ACCESS_READ), cycles, 1.4, cv::Size(8, 8), cv::Size(0, 0), 1.05, 8);
      for( size_t i = 0; i < cycles.size(); i++)
      {
        output.id.push_back(CYCLE);
        output.found.push_back(cycles.at(i));
      }

      if(_cars) _hog_cars.detectMultiScale(input.getMat(cv::ACCESS_READ), cars, 1.4, cv::Size(8, 8), cv::Size(0, 0), 1.05, 8);
      for( size_t i = 0; i < cars.size(); i++)
      {
        output.id.push_back(CAR);
        output.found.push_back(cars.at(i));
      }*/

      return output;
    }

    void set_pedestrians(bool status)  {  _pedestrians = status;  }
    void set_cycles(bool status)  {  _cycles = status;  }
    void set_cars(bool status)  {  _cars = status;  }

    bool get_pedestrians() { return _pedestrians;  }
    bool get_cycles() { return _cycles;  }
    bool get_cars() { return _cars;  }

};
