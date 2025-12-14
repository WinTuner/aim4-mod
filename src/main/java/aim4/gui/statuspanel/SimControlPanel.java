/*
Copyright (c) 2011 Tsz-Chiu Au, Peter Stone
University of Texas at Austin
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the University of Texas at Austin nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package aim4.gui.statuspanel;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import aim4.config.SimConfig;
import aim4.gui.StatusPanelInterface;
import aim4.gui.Viewer;
import aim4.gui.component.LabeledSlider;

/**
 * The Speed Control Panel
 */
public class SimControlPanel extends JPanel
                             implements StatusPanelInterface,
                                        ChangeListener {

  private static final long serialVersionUID = 1L;

  // ///////////////////////////////
  // PRIVATE FIELDS
  // ///////////////////////////////

  LabeledSlider targetSimSpeedSlider;
  LabeledSlider targetFrameRateSlider;
  LabeledSlider trafficLevelSlider;
  LabeledSlider pedestrianSlider;

  /** The viewer object */
  Viewer viewer;

  // ///////////////////////////////
  // CONSTRUCTORS
  // ///////////////////////////////

  /**
   * Create a simulation control panel.
   *
   * @param viewer the viewer object
   */
  public SimControlPanel(Viewer viewer) {
    this.viewer = viewer;
    targetSimSpeedSlider =
      new LabeledSlider(
        0.0, Viewer.TURBO_SIM_SPEED,
        Math.min(Viewer.DEFAULT_SIM_SPEED, Viewer.TURBO_SIM_SPEED),
        1.0, 0.25,
        "Simulation Speed: %.1f simulation second / second",
        "%.1f", this);
    targetSimSpeedSlider.setTickLabel(Viewer.TURBO_SIM_SPEED, "max",
                                "Simulation Speed: %s");

    targetFrameRateSlider =
      new LabeledSlider(
        0.0, SimConfig.CYCLES_PER_SECOND,
        Math.min(Viewer.DEFAULT_TARGET_FRAME_RATE, SimConfig.CYCLES_PER_SECOND),
        5.0, 1.0,
        "Target frame rate: %.0f frame / second", "%.0f",
        this);
    targetFrameRateSlider.setTickLabel(SimConfig.CYCLES_PER_SECOND, "max",
                                       "Target frame rate: %s");

    // Traffic level slider displayed as vehicles/hour/lane (0..2500)
    // Internally Viewer expects vehicles/second/lane, so conversion is done
    // in stateChanged.
    trafficLevelSlider =
      new LabeledSlider(0.0, 2500.0,
                        viewer.getInitTrafficLevel() * 3600.0,
                        500.0, 100.0,
                        "Traffic Level: %.0f vehicles/hour/lane",
                        "%.0f", this);
    // Pedestrian density slider: people per sidewalk (0..10)
    pedestrianSlider =
      new LabeledSlider(0.0, 10.0,
                        viewer.getInitPedestrianDensity(),
                        2.0, 1.0,
                        "Pedestrians per sidewalk: %.0f",
                        "%.0f", this);
    // layout
    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setAutoCreateGaps(false);
    layout.setAutoCreateContainerGaps(false);

    layout.setHorizontalGroup(layout
      .createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(targetSimSpeedSlider)
      .addComponent(targetFrameRateSlider)
      .addComponent(trafficLevelSlider)
      .addComponent(pedestrianSlider));

    layout.setVerticalGroup(layout.createSequentialGroup()
      .addComponent(targetSimSpeedSlider)
      .addComponent(targetFrameRateSlider)
      .addComponent(trafficLevelSlider)
      .addComponent(pedestrianSlider));
  }

  // ///////////////////////////////
  // PUBLIC METHODS
  // ///////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public void update() {
    // do nothing
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    // do nothing
  }

  /**
   * Get the simulation speed.
   *
   * @return the simulation speed
   */
  public double getSimSpeed() {
    return targetSimSpeedSlider.getValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stateChanged(ChangeEvent evt) {
    if (evt.getSource() == targetSimSpeedSlider) {
      viewer.setTargetSimSpeed(targetSimSpeedSlider.getValue());
    } else if (evt.getSource() == targetFrameRateSlider) {
      viewer.setTargetFrameRate(targetFrameRateSlider.getValue());
    } else if (evt.getSource() == trafficLevelSlider) {
      // slider value is vehicles/hour/lane -> convert to vehicles/sec/lane
      double vph = trafficLevelSlider.getValue();
      viewer.setTrafficLevel(vph / 3600.0);
    } else if (evt.getSource() == pedestrianSlider) {
      viewer.setPedestrianDensity((int) pedestrianSlider.getValue());
    }
  }
}