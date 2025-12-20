package aim4.pedestrian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple singleton manager for pedestrians used by the viewer/simulation.
 */
public class PedestrianManager {

  private static final PedestrianManager INSTANCE = new PedestrianManager();

  private final List<Pedestrian> pedestrians;

  private PedestrianManager() {
    pedestrians = new ArrayList<Pedestrian>();
  }

  public static PedestrianManager getInstance() {
    return INSTANCE;
  }

  public synchronized void addPedestrian(Pedestrian p) {
    pedestrians.add(p);
  }

  public synchronized List<Pedestrian> getPedestrians() {
    return Collections.unmodifiableList(new ArrayList<Pedestrian>(pedestrians));
  }

  public synchronized void step(double dt) {
    for (Pedestrian p : pedestrians) {
      if (p instanceof SidewalkPedestrian) {
        ((SidewalkPedestrian) p).step(dt);
      }
    }
  }
}
