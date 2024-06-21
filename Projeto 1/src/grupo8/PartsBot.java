package grupo8;

import robocode.*;

/**
 * PartsBot - a robot for Software Architecture course, at the
 *  Department of Computer Science - University of Porto.
 *  This robot has each component in a separate class.
 * @author Carla Henriques
 * @author Rui Santos
 * @version 4.0
 */
public class PartsBot extends AdvancedRobot {

    //Robots radar object
    private Radar radar = new Radar();

    // Robots gun object
    private Gun gun = new Gun();

    // Robot body (tank) object
    private Tank tank = new Tank();

    // Array of RobotPart (Tank,Gun and Radar)
    private RobotPart[] parts = new RobotPart[3];

    // Condition for lowEnergy
    private boolean lowEnergy = false;

    /**
     * Indicates that we're handling the event for wall avoidance
     */
    public static int tooCloseToWall = 0;

    /**
     * How close our robot can get to the wall
     */
    public static int wallMargin = 60;


    // ENEMY VARIABLES
    private static double enemyX = 0.0;
    private static double enemyY = 0.0;
    private static double enemyBearing = 0.0;
    private static double enemyDistance = 0.0;
    private static double enemyHeading = 0.0;
    private static String enemyName = "";
    private static double enemyVelocity = 0.0;

    /**
     * PartsBot constructor.
     */
    public PartsBot() {
    }

    /**
     * run: Robot's default behaviour
     */
    public void run() {
        parts[0] = radar;
        parts[1] = gun;
        parts[2] = tank;

        // Sets the radar to turn independent of the gun's turn.
        setAdjustRadarForGunTurn(true);

        // Sets the gun to turn independent of the robot's turn.
        setAdjustGunForRobotTurn(true);

        // Initializes each part of the robot
        for (int i = 0; i < parts.length; i++) {
            parts[i].init(this);
            // Enemy is already initialized uppon declaration of enemy's variables.
        }

        // ROBOT'S MAIN LOOP
        // Iterate through each part, moving them as we go
        for (int i = 0; true; i = (i + 1) % parts.length) {
            parts[i].move(this);

            // Does a 360 scan
            doScan();
            execute();
        }
    }



    /**
     * onScannedRobot: What a robot does when it scans another robot.
     *  When enemy is scanned, updates enemy object.
     * @param e ScannedRobotEvent, event for when adversary robot is scanned
     */
    public void onScannedRobot(ScannedRobotEvent e) {

        // Track if we have no enemy, the one we found is significantly
        // Closer, or we scanned the one we've been tracking.
        if ( enemyNotSet() || e.getDistance() < getEnemyDistance() - 70 ||
                e.getName().equals(getEnemyName()))
        {
            // track him using the NEW update method
            updateEnemyInfo(e);
        }

    }

    /**
     * doScan: turn the radar 360 degrees to find another target
     */
    public void doScan(){
        setTurnRadarRight(360);
    }

    /**
     * onCustomEvent: Handles custom events.
     * @param e CustomEvent, event when custom events happen
     */
    public void onCustomEvent(CustomEvent e) {

        // "tooCloseToWalls" event
        if (e.getCondition().getName().equals("tooCloseToWalls"))
        {
            if (tooCloseToWall <= 0)
            {
                out.println("Stopped, too close to a wall!");

                // if we weren't already dealing with the walls, we are now
                tooCloseToWall += wallMargin;

                // Stops
                setMaxVelocity(0);
            }
        }

        // "lowEnergy" event
        if (e.getCondition().getName().equals("lowEnergy") && !lowEnergy)
        {
            out.println("<<<< LOW ENERGY >>>>");

            // indicate that robot has low energy
            lowEnergy = true;

            // Updates Tank part, in RobotPart array
            parts[2] = new CircularTank();

            // Robot starts moving in circles
            parts[2].init(this);
        }

        // "highEnergy" event
        if (e.getCondition().getName().equals("highEnergy") && lowEnergy)
        {
            out.println("<<<< HIGH ENERGY >>>>");

            // robot no longer has low energy
            lowEnergy = false;

            parts[2] = new Tank();

            // Robot starts moving in strafes
            parts[2].init(this);
        }
    }

    /**
     * onHitWall: what robot does when hits wall.
     * @param e HitByWallEvent, event for when robot is hit by wall
     */
    public void onHitWall(HitWallEvent e) {
        out.println("OUCH! I hit a wall!");
    }

    /**
     * onHitRobot : when adversary robots hits us, reset tooCloseToWall variable back to 0.
     * @param e HitByRobotEvent, event for when robot is hit by adversary robot
     */
    public void onHitRobot(HitRobotEvent e) {
        out.println("OUCH! My enemy hit me!");
        tooCloseToWall = 0;
    }

    /**
     * onHitByBullet: what robot does when bullet hits.
     * @param e HitByBulletEvent, event for when robot is hit by bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        out.println("GOT SHOT! CHANGING DIRECTION ...");

        // Stops
        setMaxVelocity(0);
    }

    /**
     * onBulletHitBullet: stop moving when bullet hits a bullet
     * @param e BulletHitBulletEvent, event for when bullet hits a bullet
     */
    public void onBulletHitBullet(BulletHitBulletEvent e) {
        out.println("BULLET COLLISION! CHANGING DIRECTION ...");

        // Stops
        setMaxVelocity(0);

    }


    /**
     ** onWin:  Do a victory dance
     * @param e WinEvent, event when robot wins
     **/
    public void onWin(WinEvent e) {
        for (int i = 0; i < 25; i++) {
            turnRight(20);
            turnLeft(20);
        }
    }

    /**
     ** onDeath: what robot does when it dies.
     * @param e DeathEvent, event for when robot dies
     */
    public void onDeath(DeathEvent e) {
        out.println("I died... :'(");
    }


    // ENEMY METHODS

    /**
     * updateEnemyInfo: Updates enemy's variables.
     * Method's math inspired by Maxim Galuska's EnemyBot.
     * @param e ScannedRobotEvent, event for when robot was scanned
     */
    public void updateEnemyInfo(ScannedRobotEvent e){
        enemyBearing = e.getBearing();
        enemyDistance = e.getDistance();
        enemyHeading = e.getHeading();
        enemyName = e.getName();
        enemyVelocity = e.getVelocity();

        double absBearingDeg = (this.getHeading() + e.getBearing());
        if (absBearingDeg < 0)
        {
            absBearingDeg += 360;
        }

        // yes, you use the _sine_ to get the X value because 0 deg is North
        enemyX = this.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();

        // yes, you use the _cosine_ to get the Y value because 0 deg is North
        enemyY = this.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
    }

    /**
     * getEnemyDistance: getter method for enemyDistance variable
     * @return double, the enemy robot's distance
     */
    public static double getEnemyDistance() {
        return enemyDistance;
    }

    /**
     * getEnemyBearing: getter method for enemyBearing variable
     * @return double, the enemy robot's bearing
     */
    public static double getEnemyBearing() {
        return enemyBearing;
    }

    /**
     * getEnemyName: getter method for enemyName variable
     * @return double, the enemy robot's name
     */
    public static String getEnemyName() {
        return enemyName;
    }

    /**
     * getEnemyHeading: getter method for enemyHeading variable
     * @return double, the enemy robot's heading
     */
    public static double getEnemyHeading() {
        return enemyHeading;
    }

    /**
     * getEnemyVelocity: getter method for enemyVelocity variable
     * @return double, the enemy robot's velocity
     */
    public static double getEnemyVelocity() {
        return enemyVelocity;
    }

    /**
     * getEnemyFutureX: method that predicts enemy's future X coordinate.
     * Method's math inspired by Maxim Galuska's EnemyBot.
     * @param when long, antecipated time
     * @return double, the enemy robot's estimated future x coordinate
     */
    public static double getEnemyFutureX(long when){
        return enemyX + Math.sin(Math.toRadians(getEnemyHeading())) * getEnemyVelocity() * when;
    }

    /**
     * getEnemyFutureY: method that predicts enemy's future Y coordinate.
     * Method's math inspired by Maxim Galuska's EnemyBot.
     * @param when long, antecipated time
     * @return double, the enemy robot's estimated future y coordinate
     */
    public static double getEnemyFutureY(long when){
        return enemyY + Math.cos(Math.toRadians(getEnemyHeading())) * getEnemyVelocity() * when;
    }

    /**
     * enemyNotSet: Tells if enemy has been set/found.
     * @return boolean, true if the enemy was encountered before.
     */
    public static boolean enemyNotSet() {
        return "".equals(getEnemyName());
    }

}

