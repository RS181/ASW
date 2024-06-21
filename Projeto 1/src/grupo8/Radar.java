package grupo8;

import robocode.*;
import java.awt.*;

/**
 * Radar - radar class for PartsBot
 * @author Carla Henriques
 * @author Rui Santos
 */
public class Radar implements RobotPart {

    /**
     * Radar constructor.
     */
    public Radar() {
    }

    /**
     * init: Defines the initialization for Radar.
     */
    public void init(AdvancedRobot r) {
        r.setRadarColor(Color.CYAN);
        r.setScanColor(Color.CYAN);
    }

    /**
     * move: Radar movement is done in {@link PartsBot#doScan()} method of class {@link PartsBot}.
     *
     * @see PartsBot
     */
    public void move(AdvancedRobot r) {
    }
}
