package rea.components;

/**
 * A position in the game map. Provides the x and y coordinates of the position to
 * the {@link Positionable} components.
 * @author José Paulo Leal <code>jpleal@fc.up.pt</code>
 */
public class Position {

    private int positionX = 0;
    private int positionY = 0;

    /**
     * Create a position with the given coordinates.
     * @param x coordinate
     * @param y coordinate
     */
    public Position(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    /**
     * Get the x coordinate of the position.
     * @return x coordinate
     */
    public int getX() {
        return this.positionX;
    }

    /**
     * Get the y coordinate of the position.
     * @return y coordinate
     */
    public int getY() {
        return this.positionY;
    }

    /**
     * Additional method.
     * @param x x coordinate value
     */
    public void setX(int x) {
        this.positionX = x;
    }

    /**
     * Additional method.
     * @param y y coordinate value
     */
    public void setY(int y) {
        this.positionY = y;
    }

    /**
     * Show the position as a pair, within round brackets.
     * @return string representation.
     */
    @Override
    public String toString() {
        // String format must be this to pass PositionTest!
        return "("+this.getX()+","+this.getY()+")";
    }
}
