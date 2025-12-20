package aim4.pedestrian;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * A simple pedestrian that walks on sidewalks and can cross to a target point.
 * Rendered as a circle (ellipse) centered at its position.
 */
public class SidewalkPedestrian implements Pedestrian {

  private final int vin;
  private final double width; // meters (gauge)
  private Point2D.Double position;
  private double velocity; // m/s
  private double heading; // radians, 0 = +x

  // crossing target
  private Point2D.Double target;
  private boolean crossing;
  // following a line path (centerline)
  private java.awt.geom.Line2D pathLine;
  private boolean loopPath;
  private boolean movingForward;
  // roaming along sidewalk centerline
  private java.awt.geom.Line2D assignedCenterLine;
  private double sidewalkHalfWidth;
  private double lateralOffset; // meters from centerline (signed)
  private double currentT; // parameter along line [0,1]
  private java.util.Random rnd = new java.util.Random();
  private boolean roaming;

  public SidewalkPedestrian(int vin, Point2D start, double speed, double heading, double width) {
    this.vin = vin;
    this.position = new Point2D.Double(start.getX(), start.getY());
    this.velocity = speed;
    this.heading = heading;
    this.width = width;
    this.crossing = false;
    this.target = null;
    this.pathLine = null;
    this.loopPath = false;
  }

  @Override
  public int getVIN() {
    return vin;
  }

  @Override
  public Point2D getPosition() {
    return (Point2D) position.clone();
  }

  @Override
  public double getVelocity() {
    return velocity;
  }

  @Override
  public double getHeading() {
    return heading;
  }

  @Override
  public Shape getShape() {
    double r = width / 2.0;
    return new Ellipse2D.Double(position.x - r, position.y - r, width, width);
  }

  @Override
  public double getWidth() {
    return width;
  }

  /**
   * Step the pedestrian forward by dt seconds. If crossing to a target, move toward it.
   */
  public void step(double dt) {
    if (roaming && assignedCenterLine != null) {
      // move along centerline with lateral offset
      double x1 = assignedCenterLine.getX1();
      double y1 = assignedCenterLine.getY1();
      double x2 = assignedCenterLine.getX2();
      double y2 = assignedCenterLine.getY2();
      double dx = x2 - x1;
      double dy = y2 - y1;
      double len = Math.hypot(dx, dy);
      if (len < 1e-6) return;
      double move = (velocity * dt) / len; // normalized along t
      int dir = movingForward ? 1 : -1;
      currentT += dir * move;
      if (currentT >= 1.0) {
        currentT = 1.0;
        if (loopPath) movingForward = false; else roaming = false;
      } else if (currentT <= 0.0) {
        currentT = 0.0;
        if (loopPath) movingForward = true; else roaming = false;
      }
      // slight lateral jitter within half width
      lateralOffset += (rnd.nextDouble() - 0.5) * 0.02; // small wiggle
      lateralOffset = Math.max(-sidewalkHalfWidth, Math.min(sidewalkHalfWidth, lateralOffset));
      double cx = x1 + dx * currentT;
      double cy = y1 + dy * currentT;
      double nx = -dy / len;
      double ny = dx / len;
      position.x = cx + nx * lateralOffset;
      position.y = cy + ny * lateralOffset;
      heading = Math.atan2(dy, dx) + (lateralOffset >= 0 ? Math.PI/2 : -Math.PI/2);
      return;
    }
    if (pathLine != null) {
      // move exactly along the pathLine
      double x1 = pathLine.getX1();
      double y1 = pathLine.getY1();
      double x2 = pathLine.getX2();
      double y2 = pathLine.getY2();
      double dx = x2 - x1;
      double dy = y2 - y1;
      double len = Math.hypot(dx, dy);
      if (len <= 1e-6) return;
      double ux = dx / len;
      double uy = dy / len;

      double t;
      if (Math.abs(dx) >= Math.abs(dy)) {
        t = (position.x - x1) / dx;
      } else {
        t = (position.y - y1) / dy;
      }
      t = Math.max(0.0, Math.min(1.0, t));
      int dir = movingForward ? 1 : -1;
      double move = velocity * dt / len;
      double newt = t + dir * move;
      if (newt >= 1.0) {
        position.x = x2;
        position.y = y2;
        if (loopPath) movingForward = false; else { pathLine = null; crossing = false; target = null; }
      } else if (newt <= 0.0) {
        position.x = x1;
        position.y = y1;
        if (loopPath) movingForward = true; else { pathLine = null; crossing = false; target = null; }
      } else {
        position.x = x1 + (x2 - x1) * newt;
        position.y = y1 + (y2 - y1) * newt;
      }
      heading = Math.atan2(uy * dir, ux * dir);
      return;
    }
    if ((crossing) && target != null) {
      double dx = target.x - position.x;
      double dy = target.y - position.y;
      double dist = Math.hypot(dx, dy);
      if (dist < 1e-3) {
        position.setLocation(target);
        crossing = false;
        target = null;
        return;
      }
      double maxMove = velocity * dt;
      double ratio = Math.min(1.0, maxMove / dist);
      position.x += dx * ratio;
      position.y += dy * ratio;
      heading = Math.atan2(dy, dx);
    } else {
      // walk straight by heading
      position.x += Math.cos(heading) * velocity * dt;
      position.y += Math.sin(heading) * velocity * dt;
    }
  }

  /**
   * Start crossing toward the given point (e.g., across a crosswalk).
   */
  public void startCrossing(Point2D target) {
    this.target = new Point2D.Double(target.getX(), target.getY());
    this.crossing = true;
  }

  /**
   * Follow a straight centerline (Line2D). If loop==true, oscillate back and forth.
   */
  public void startFollowing(java.awt.geom.Line2D line, boolean loop) {
    if (line == null) return;
    this.pathLine = new java.awt.geom.Line2D.Double(line.getX1(), line.getY1(),
      line.getX2(), line.getY2());
    this.loopPath = loop;
    Point2D.Double p1 = new Point2D.Double(line.getX1(), line.getY1());
    Point2D.Double p2 = new Point2D.Double(line.getX2(), line.getY2());
    // start moving toward the nearer end
    double d1 = position.distance(p1);
    double d2 = position.distance(p2);
    this.target = (d1 < d2) ? p2 : p1;
    this.crossing = true;
  }

  public boolean isCrossing() {
    return crossing;
  }

  /**
   * Start roaming along the sidewalk centerline. Pedestrian will stay within
   * +/- halfWidth from center and walk along the centerline back and forth.
   */
  public void startRoamingOnSidewalk(java.awt.geom.Line2D center, double halfWidth,
                                    boolean loop) {
    if (center == null) return;
    this.assignedCenterLine = new java.awt.geom.Line2D.Double(center.getX1(), center.getY1(),
        center.getX2(), center.getY2());
    this.sidewalkHalfWidth = Math.max(0.0, halfWidth);
    // compute nearest t for current position
    double x1 = assignedCenterLine.getX1();
    double y1 = assignedCenterLine.getY1();
    double x2 = assignedCenterLine.getX2();
    double y2 = assignedCenterLine.getY2();
    double dx = x2 - x1, dy = y2 - y1;
    double len2 = dx*dx + dy*dy;
    double t = 0.0;
    if (len2 > 1e-9) {
      t = ((position.x - x1) * dx + (position.y - y1) * dy) / len2;
      t = Math.max(0.0, Math.min(1.0, t));
    }
    this.currentT = t;
    // lateral offset small random within half width (keep small)
    this.lateralOffset = (rnd.nextDouble() - 0.5) * Math.min(sidewalkHalfWidth, 0.5);
    this.roaming = true;
    this.loopPath = loop;
    this.movingForward = rnd.nextBoolean();
    // snap position onto centerline+offset
    double cx = x1 + dx * currentT;
    double cy = y1 + dy * currentT;
    double len = Math.hypot(dx, dy);
    if (len > 1e-6) {
      double nx = -dy / len;
      double ny = dx / len;
      position.x = cx + nx * lateralOffset;
      position.y = cy + ny * lateralOffset;
    }
  }

}
