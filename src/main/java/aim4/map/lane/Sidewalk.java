package aim4.map.lane;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Color;
import java.awt.geom.Line2D;

/**
 * Simple Sidewalk representation tied to a Lane. Provides an automatic
 * unique ID, basic geometry accessors (length, shape, width) and a
 * contains() helper. This is lightweight and relies on the underlying
 * Lane implementations for geometry (leftBorder / getLength).
 */
public class Sidewalk {

  // simple thread-safe id generator for sidewalks
  private static final AtomicInteger NEXT_ID = new AtomicInteger(0);

  private int id = -1;
  private final Lane lane;
  private final double width; // meters

  /**
   * Create a Sidewalk for the given lane with the specified width.
   * An automatic unique ID is assigned.
   */
  public Sidewalk(Lane lane, double width) {
    if (lane == null) {
      throw new IllegalArgumentException("lane must not be null");
    }
    this.lane = lane;
    this.width = width;
    this.id = NEXT_ID.incrementAndGet();
  }

  /** Get the unique id of this sidewalk. */
  public int getId() {
    return id;
  }

  /** Manually set the id (use with care). */
  public void setId(int id) {
    this.id = id;
  }

  /** Get the lane this sidewalk is associated with. */
  public Lane getLane() {
    return lane;
  }

  /** Get the length of the sidewalk in meters (delegated to lane length). */
  public double getLength() {
    return lane.getLength();
  }

  /** Get the width of the sidewalk in meters. */
  public double getWidth() {
    return width;
  }

  /**
   * Return a Shape representing the sidewalk. This implementation
   * currently returns the lane.leftBorder() as a placeholder because
   * accurate offset-area generation depends on lane implementation.
   * Implementations can be refined later to build a filled polygon
   * offset from the lane center/left border by 'width'.
   */
  public Shape getShape() {
    try {
      return lane.leftBorder();
    } catch (Throwable t) {
      return null;
    }
  }

  /**
   * Compute and return the sidewalk centerline as a Line2D. Returns
   * null if the geometry cannot be determined. The centerline is the
   * inner lane border offset outward by half the sidewalk width.
   */
  public Shape getCenterLine() {
    try {
      if (lane == null) return null;
      // Determine which border is the inner edge of the sidewalk: if the
      // lane has no right neighbor the sidewalk is expected to be on the
      // right side (inner edge = rightBorder), otherwise use leftBorder.
      Shape innerShape = (!lane.hasRightNeighbor()) ? lane.rightBorder() : lane.leftBorder();
      Line2D innerLine = null;
      if (innerShape instanceof Line2D) {
        innerLine = (Line2D) innerShape;
      } else {
        Point2D p0 = lane.getStartPoint();
        Point2D p1 = lane.getEndPoint();
        if (p0 == null || p1 == null) return null;
        innerLine = new Line2D.Double(p0, p1);
      }

      double x1 = innerLine.getX1();
      double y1 = innerLine.getY1();
      double x2 = innerLine.getX2();
      double y2 = innerLine.getY2();
      double dx = x2 - x1;
      double dy = y2 - y1;
      double len = Math.hypot(dx, dy);
      if (len == 0) return null;
      double nx = -dy / len;
      double ny = dx / len;

      // Test which direction points outside the lane (reuse lane shape test)
      Point2D mid = new Point2D.Double((x1 + x2) / 2.0, (y1 + y2) / 2.0);
      double testDist = Math.max(0.5, Math.abs(width) / 2.0);
      Point2D testOut = new Point2D.Double(mid.getX() + nx * testDist,
          mid.getY() + ny * testDist);
      boolean normalPointsOutside = false;
      try {
        Shape laneShape = lane.getShape();
        if (laneShape == null) {
          normalPointsOutside = true; // best-effort default
        } else {
          normalPointsOutside = !laneShape.contains(testOut);
        }
      } catch (Throwable t) {
        normalPointsOutside = true;
      }
      if (!normalPointsOutside) {
        nx = -nx;
        ny = -ny;
      }

      double offset = Math.abs(width) / 2.0; // half width to reach center
      double cx1 = x1 + nx * offset;
      double cy1 = y1 + ny * offset;
      double cx2 = x2 + nx * offset;
      double cy2 = y2 + ny * offset;
      return new Line2D.Double(cx1, cy1, cx2, cy2);
    } catch (Throwable t) {
      return null;
    }
  }

  /**
   * The display color for this sidewalk. Renderers can use this when
   * drawing the sidewalk shape.
   */
  public Color getColor() {
    return Color.GRAY;
  }

  /**
   * Whether the provided point is inside the sidewalk shape. Returns
   * false if no shape is available.
   */
  public boolean contains(Point2D pos) {
    Shape s = getShape();
    if (s == null || pos == null) return false;
    return s.contains(pos);
  }
}
