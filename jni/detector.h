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

struct Entity
{
  cv::Rect found;
  enum TYPES id;
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
    std::vector<Entity> detect (cv::UMat input)
    {
      std::vector<Entity> output;
      if(!input.empty())
      {
          std::vector<cv::Rect> detected;

		  if(_pedestrians) _hog_pedestrians.detectMultiScale(input.getMat(cv::ACCESS_READ), detected, 1.4, cv::Size(8, 8), cv::Size(0, 0), 1.05, 8);
		  for( int i = 0; i < detected.size(); i++)
		  {
			  Entity temp;
			  temp.found = detected[i];
			  temp.id = PEDESTRIAN;
			  output.push_back(temp);
		  }

		  detected.clear();

		  /*if(_cycles) _hog_cycles.detectMultiScale(input.getMat(cv::ACCESS_READ), detected, 1.4, cv::Size(8, 8), cv::Size(0, 0), 1.05, 8);
		  for( int i = 0; i < detected.size(); i++)
		  {
			  Entity temp;
			  temp.found = detected[i];
			  temp.id = CYCLE;
			  output.push_back(temp);
		  }

		  if(_cars) _hog_cars.detectMultiScale(input.getMat(cv::ACCESS_READ), detected, 1.4, cv::Size(8, 8), cv::Size(0, 0), 1.05, 8);
		  for( int i = 0; i < detected.size(); i++)
		  {
			  Entity temp;
			  temp.found = detected[i];
			  temp.id = CAR;
			  output.push_back(temp);
		  }*/
      }
      else
      {
    	  std::cerr << "Empty image!";
      }

      return output;
    }

    void set_pedestrians(bool status)  {  _pedestrians = status;  }
    void set_cycles(bool status)  {  _cycles = status;  }
    void set_cars(bool status)  {  _cars = status;  }

    bool get_pedestrians() { return _pedestrians;  }
    bool get_cycles() { return _cycles;  }
    bool get_cars() { return _cars;  }

};
