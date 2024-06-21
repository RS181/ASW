package grupo8;

import robocode.*;
import java.awt.*;

/**
 * Tank - Tank class for PartsBot
 * @author Carla Henriques
 * @author Rui Santos
 */
public class Tank implements RobotPart {

    /**
     * Direction we are moving to
     */
    public int direction = 1;

    final int wallMargin = PartsBot.wallMargin;

    // Minimum amount we can move while strafing
    final int MIN_STRAFE_MOVEMENT = 100;

    // Maximum amount we can move while strafing
    final int MAX_STRAFE_MOVEMENT = 500;

    // Maximum distance robot want's to be from enemy (while strafing)
    final int MAX_DISTANCE_STRAFE = 150;

    // Maximum distance we want to be from enemy (while circling)
    final int MAX_DISTANCE_CIRCLING = 200;

    // Constant that indicates if robot has low energy value
    final int LOW_ENERGY_VALUE = 50;

    /**
     * Tank constructor.
     */
    public Tank() {
    }

    /**
     * init: Defines the initialization for Tank's body.
     * @param r AdvancedRobot, our robot
     */
    @Override
    public void init(AdvancedRobot r) {

        r.setBodyColor(Color.red);

        // Custom event for wall avoidance
        r.addCustomEvent(new Condition("tooCloseToWalls") {
            public boolean test() {
                return (
                        // we're too close to the left wall
                        (r.getX() <= wallMargin ||
                                // or we're too close to the right wall
                                r.getX() >= r.getBattleFieldWidth() - wallMargin ||
                                // or we're too close to the bottom wall
                                r.getY() <= wallMargin ||
                                // or we're too close to the top wall
                                r.getY() >= r.getBattleFieldHeight() - wallMargin)
                );
            }
        });

        // Custom event for detecting low energy
        r.addCustomEvent(new Condition("lowEnergy") {
            public boolean test() {
                return (
                        r.getEnergy() <= LOW_ENERGY_VALUE
                );
            }
        });

        // Custom event for detecting high energy
        r.addCustomEvent(new Condition("highEnergy") {
            public boolean test() {
                return (
                        r.getEnergy() > LOW_ENERGY_VALUE
                );
            }
        });

    }

    /**
     * move: Defines how the Tank should move.
     * In this case we move side to side (Strafe)
     * while facing our robot's body perpendicular to
     * scanned enemy
     * @param r AdvancedRobot, our robot
     */
    public void move(AdvancedRobot r) {

        // If we have a target
        if (!PartsBot.enemyNotSet())
        {
            r.out.println("Strafing");
            doTurn(r);

            // If we're close to the wall, eventually, we'll move away
            if (PartsBot.tooCloseToWall >0) PartsBot.tooCloseToWall --;

            // Normal movement: switch directions if we've stopped
            if (r.getVelocity() == 0) {
                // Set the velocity of robot to maximum
                r.setMaxVelocity(8);
                direction *= -1;

                // Move a random amount of distance in a [MIN_STRAFE_MOVEMENT,MAX_STRAFE_MOVEMENT] range
                r.setAhead(Math.floor(Math.random() *(MAX_STRAFE_MOVEMENT - MIN_STRAFE_MOVEMENT + 1) + MIN_STRAFE_MOVEMENT) * direction);
            }
        }
    }

    /**
     * doTurn: Decides how the robot should turn
     * based on the distance to enemy
     * @param r AdvancedRobot, our robot
     */
    public void doTurn(AdvancedRobot r){

        // If enemy is too far away, strafes towards enemy
        if (PartsBot.getEnemyDistance() > MAX_DISTANCE_STRAFE)
        {
            r.out.println("GETTING CLOSER TO ENEMY");

            // Turns slightly towards the enemy
            r.setTurnRight(normalizeBearing(PartsBot.getEnemyBearing() + 90 - (30 * direction)));
        }
        // otherwise do  perpendicular strafing
        else
        {
            r.out.println("STRAFING");

            // Turns perpendicular to enemy robot
            r.setTurnRight(PartsBot.getEnemyBearing() + 90);
        }
    }
}


/**
 * CircularTank - extends Tanks.
 * Overrides move, doTurn from Tank.
 * @see Tank
 * @author Rui Santos
 */
class CircularTank extends Tank {
    @Override
    public void move(AdvancedRobot r) {

        // If we have a target
        if (!PartsBot.enemyNotSet())
        {
            r.out.println("Circle movement");

            // Circles our enemy, while doing circular movement

            if (r.getVelocity() == 0)
            {
                // Switch directions if we've stopped
                direction *= -1;
                r.setMaxVelocity(8);
            }
            // if we're close to the wall, eventually, we'll move away
            if (PartsBot.tooCloseToWall >0) PartsBot.tooCloseToWall --;

            doTurn(r);
            if(r.getDistanceRemaining() <=0)
                r.setAhead(1000 * direction);

            r.execute();
        }
    }

    @Override
    public void doTurn(AdvancedRobot r) {
        // If enemy is too far away , circle towards enemy
        if (PartsBot.getEnemyDistance() > MAX_DISTANCE_CIRCLING)
        {
            r.out.println("CIRCLING CLOSER TO ENEMY");

            // Turns slightly towards the enemy
            r.setTurnRight(normalizeBearing(PartsBot.getEnemyBearing() + 90 - (15 * direction)));
        }
        // Otherwise normal circling
        else
        {
            r.out.println("CIRCLING");

            // Turns perpendicular to enemy robot
            r.setTurnRight(PartsBot.getEnemyBearing() + 90);
        }
    }
}


