package aim4.pedestrian;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * The interface for a pedestrian.
 */
public interface Pedestrian {
  
  /////////////////////////////////
  // PUBLIC METHODS
  /////////////////////////////////

  /**
   * Get the VIN number of this Pedestrian.
   *
   * @return the VIN number of this Pedestrian
   */
  int getVIN();

  /**
   * Get the current position of the Pedestrian.
   *
   * @return the current position of the Pedestrian
   */
  Point2D getPosition();

  /**
   * Get the current velocity of the Pedestrian.
   *
   * @return the current velocity of the Pedestrian
   */
  double getVelocity();

  /**
   * Get the heading of the Pedestrian.
   *
   * @return the heading of the Pedestrian
   */
  double getHeading();

  /**
   * Get the shape of the Pedestrian for rendering.
   *
   * @return the shape
   */
  Shape getShape();

  /**
   * Get the gauge (width) of the Pedestrian.
   *
   * @return the width in meters
   */
  double getWidth();
}
