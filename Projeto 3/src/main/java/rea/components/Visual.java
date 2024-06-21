package rea.components;

import java.util.Objects;

/**
 * A visual representation of a game component.
 * It includes an image and the size in pixels to which it should be scaled.
 * @author José Paulo Leal <code>jpleal@fc.up.pt</code>
 */
public class Visual {

    private String visualPathname;
    private int visualWidth;
    private int visualHeight;

    /**
     * Create a visual representation of a game component.
     * @param pathname path to image
     * @param width width in pixels
     * @param height height in pixels
     */
    public Visual(String pathname, int width, int height) {
        this.visualPathname = pathname;
        this.visualWidth = width;
        this.visualHeight = height;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if (o instanceof Visual visual){
            return Objects.equals(this.getPathname(), visual.getPathname())
                    && Objects.equals(this.getWidth(), visual.getWidth())
                    && Objects.equals(this.getHeight(), visual.getHeight());
        }
        else
            return false;
    }

    /**
     * The path to image to be displayed. The path is relative to {resources/META-INF/resources} directory.
     * @return path to image
     */
    public String getPathname() {
        return this.visualPathname;
    }

    /**
     * The width in pixels to which the image should be scaled
     * @return width in pixels
     */
    public int getWidth() {
        return this.visualWidth;
    }

    /**
     * The height in pixels to which the image should be scaled
     * @return height in pixels
     */
    public int getHeight() {
        return this.visualHeight;
    }
}
