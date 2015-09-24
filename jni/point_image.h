/* Author: Gaspare Lunetto
   Date:   15/09/2015
*/

#include "common_defs.h"

/* This class is used to store images, it can contain also the position of each pixel in a 3d space and normal vectors of each point. */
class Point_Image
{
  private:

    /* _image record the rgb data */
    cv::UMat _image;

    /* _data record the xyz data */
    cv::Mat _data;

    /* _normal_vectors of the points in the image */
    cv::Mat _normal_vectors;


  public:

    /* Constructors */
    Point_Image(cv::UMat image, cv::Mat data, cv::Mat normal_vectors)
    {
      _image = image;
      _data = data;
      _normal_vectors = normal_vectors;
    }

    Point_Image(cv::UMat image)
    {
      _image = image;
    }

    Point_Image() {}

    /* Methods to set values */
    void set_image(cv::UMat image) { _image = image; }

    void set_data(cv::Mat data) { _data = data; }

    void set_normal_vectors(cv::Mat normal_vectors) { _normal_vectors = normal_vectors; }

    /*Methods to get values */

    cv::UMat image() { return _image; }

    cv::Mat data() { return _data; }

    cv::Mat normal_vectors() { return _normal_vectors; }

};
