package grupo8;

import robocode.*;
import java.awt.geom.Point2D;

/**
 * RobotPart - interface for robot parts.
 * @author Carla Henriques
 * @author Rui Santos
 */
public interface RobotPart {

    /**
     * init: Initialize the robot part
     * @param r AdvancedRobot, our robot
     */
    public abstract void init(AdvancedRobot r);

    /**
     * move: Tells how to move a robot part
     * @param r AdvancedRobot, our robot
     */
    public abstract void move(AdvancedRobot r);

    // AUXILIARY METHODS

    /**
     * normalizeBearing: Normalizes a bearing to an angle between +180 and -180 degrees.
     * Method's math inspired by Maxim Galuska's EnemyBot.
     * @param angle angle of bearing to be normalized
     * @return double normalized bearing angle
     */
    default double normalizeBearing(double angle) {
        while (angle >  180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    /**
     * absoluteBearing: Computes the absolute bearing between two points.
     * Method's math inspired by Maxim Galuska's EnemyBot.
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of second point
     * @return double with absolute bearing
     */
    default double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2-x1;
        double yo = y2-y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }
}
