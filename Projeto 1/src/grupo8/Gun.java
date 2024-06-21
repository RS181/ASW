package grupo8;

import robocode.*;
import java.awt.*;

/**
 * Gun - gun class for PartsBot
 * @author Carla Henriques
 * @author Rui Santos
 */
public class Gun implements RobotPart {

    /**
     * Gun constructor.
     */
    public Gun() {
    }


    /**
     * init: Defines the initialization for Gun
     * @param r AdvancedRobot, our robot
     */
    public void init(AdvancedRobot r) {
        r.setGunColor(Color.black);
        r.setBulletColor(Color.red);
    }

    /**
     * move: Defines how the Gun should move. Predictive firing.
     * @param r AdvancedRobot, our robot
     */
    public void move(AdvancedRobot r) {
        // Don't shoot if I've got no enemy
        if (PartsBot.enemyNotSet())
            return;

        // Calculates firepower based on distance
        double firePower = Math.min(500 / PartsBot.getEnemyDistance(), 3);

        // Calculates speed of bullet
        double bulletSpeed = 20 - firePower * 3;

        // distance = rate * time, solved for time
        long time = (long)(PartsBot.getEnemyDistance() / bulletSpeed);

        // Calculate gun turn to predicted x,y location
        double futureX = PartsBot.getEnemyFutureX(time);
        double futureY = PartsBot.getEnemyFutureY(time);
        double absDeg = absoluteBearing(r.getX(), r.getY(), futureX, futureY);

        // Non-predictive firing can be done like this:
        // double absDeg = absoluteBearing(getX(), getY(), enemy.getX(), enemy.getY());

        // Turns the gun to the predicted x,y location
        r.setTurnGunRight(normalizeBearing(absDeg - r.getGunHeading()));

        // If the gun is cool, and we're pointed in the right direction, shoot!
        if (r.getGunHeat() == 0 && Math.abs(r.getGunTurnRemaining()) < 10)
        {
            r.setFire(firePower);
        }
    }
}