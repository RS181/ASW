package vaadin.app.views.playGame.scene;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import elemental.json.JsonObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A layout that allows absolute positioning of components while preventing overlap, if required.
 * This layout is intended for use in games, where components are positioned in a scene.
 * </p>
 * <p>
 * To use basic absolute positionig, components are added to this layout using the
 * {@link #add(Component, int, int)} method.
 * The coordinates are relative to the top left corner of the layout.
 * </p>
 * <p>
 * To prevent overlap, use the {@link #add(Component, Rectangle, Side)} method,
 * where the rectangle is the position and dimensions intended for the component (it will be resized),
 * and {@link Side} is the side to which the component should be moved if overlapping.
 * For instance, if the component should be moved to the right if overlapping, use {@link Side#RIGHT}.
 * </p>
 * <p>
 * To sets the background image and dimensions of the layout use the
 * {@link #setBackground(Image, int width, int height)} method.
 * This method will add the image to the layout and resize it to the given dimensions.
 * It should be executed before adding any components to the layout.
 * </p>
 * <p>
 * To transform absolute coordinates, such as those reported by events,
 * use the {@link #getAbsoluteTop()} and {@link #getAbsoluteLeft()} methods.
 * </p>
 * <p>
 * If made disabled, the content will be more opaque. You should use {@link #setEnabled(boolean)}
 * to change the appearance of the layout. Don't use directly the {@link #onEnabledStateChanged(boolean)} method.
 * The layout (as other game panel components) should be disabled if the game has not started yet or has ended.
 * </p>
 *
 * @author Jos&eacute; Paulo Leal  {@code jpleal@fc.up.pt}
 */
public class SceneLayout extends Div {

    Page page = UI.getCurrent().getPage();
    Element element = getElement();
    Style style = element.getStyle();
    int absoluteTop;
    int absoluteLeft;

    /**
     * Create a scene layout.
     */
    public SceneLayout() {
        init();
    }

    void init() {
        setEnabled(true);

        style.set("overflow", "clip");
        style.set("position", "relative");

        onLoad(this::setAbsoluteCoordinates);
    }

    /**
     * Set the background image of this layout with given dimensions
     * It should be executed before adding any components to the layout.
     * @param background image
     * @param width of layout (and to scale image)
     * @param height of layout (and to scale image)
     */
    public void setBackground(Image background, int width, int height) {
        this.add(background,0,0);
        this.setWidth(width, Unit.PIXELS);
        this.setHeight(height, Unit.PIXELS);
    }

    /**
     * If disabled, the content opacity will decrease (be more transparent).
     * The layout should be disabled if the content should not be interacted with,
     * either because the game has not started yet or because it has ended.
     * Use {@link #setEnabled(boolean)} to change the appearance of the layout,
     * don't use directly this method.
     *
     * @param enabled true if enabled, false otherwise.
     */
    @Override
    public void onEnabledStateChanged(boolean enabled) {
        var style = getElement().getStyle();

        style.set("opacity", enabled ? "1" : "0.3");
    }

    /**
     * Run given {@link Runnable} when the page is loaded.
     * @param runnable to run when the page is loaded.
     * @implNote This method uses JavaScript to detect the page load event.
     */
    void onLoad(Runnable runnable) {

        page.executeJs("window.addEventListener('load', () => {} );")
                .then( (event) -> runnable.run() );
    }

    /**
     * Set the absolute coordinates of this layout.
     * Should be called when the page is loaded.
     * @implNote This method uses JavaScript to calculate the absolute coordinates.
     */
    public void setAbsoluteCoordinates() {
        Element element = getElement();

        page.executeJs( "{" +
                "    let el = $0;\n" +
                "    let x = 0;\n" +
                "    let y = 0;\n" +
                "    while( el && !isNaN( el.offsetLeft ) && !isNaN( el.offsetTop ) ) {\n" +
                "        x += el.offsetLeft - el.scrollLeft;\n" +
                "        y += el.offsetTop  - el.scrollTop;\n" +
                "        el = el.offsetParent;\n" +
                "    }\n" +
                "    return { top: y, left: x };\n" +
                "}",element).then( jsonValue -> {
            if (jsonValue instanceof JsonObject offset) {
                absoluteTop  = (int) offset.getNumber("top");
                absoluteLeft = (int) offset.getNumber("left");
            }
        });
    }

    /**
     * Get the absolute top coordinate of this layout.
     * That is, the coordinate of the top side relative the page.
     *
     * @return the absolute top coordinate of this layout.
     */
    public int getAbsoluteTop() {
        return absoluteTop;
    }

    /**
     * Get the absolute left coordinate of this layout.
     * That is, the coordinate left side relative to the page.
     * @return absolute left coordinate of this layout.
     */
    public int getAbsoluteLeft() {
        return absoluteLeft;
    }

    /**
     * Add a component to this layout at the given top and left coordinates.
     * The coordinates are relative to the top left corner of the layout.
     *
     * @param component to add.
     * @param top coordinate.
     * @param left coordinate.
     */
    public void add(Component component, int top, int left) {
        var style = component.getElement().getStyle();

        add(component);

        style.set("position", "absolute");
        style.set("top", top + "px");
        style.set("left", left + "px");
    }

    /**
     * List of non-overlapping rectangles that are already in place.
     */
    List<Rectangle> rectangles = new ArrayList<>();

    /**
     * The side to which a component should be moved if overlapping.
     */
    public enum Side {
        /** Move the component up. */
        UP,
        /** Move the component down. */
        DOWN,
        /** Move the component to the left. */
        LEFT,
        /** move the component to the right.
         */
        RIGHT
    }

    /**
     * Move the tentative rectangle to the given side of the overlap rectangle.
     * Dimensions are preserved.
     * @param tentative rectangle to move.
     * @param overlap rectangle aready in place.
     * @param side to which it should be moved.
     * @return the new rectangle.
     */
    Rectangle onSide(Rectangle tentative, Rectangle overlap, Side side) {

        switch(side) {
            case UP:
                return new Rectangle(tentative.x, overlap.y - tentative.height, tentative.width, tentative.height);
            case DOWN:
                return new Rectangle(tentative.x, overlap.y + overlap.height, tentative.width, tentative.height);
            case LEFT:
                return new Rectangle(overlap.x - tentative.width, overlap.y, tentative.width, tentative.height);
            case RIGHT:
                return new Rectangle(overlap.x + overlap.width, overlap.y, tentative.width, tentative.height);
            default:
                throw new IllegalArgumentException("Invalid side: " + side);
        }
    }

    /**
     * Add a component to this layout at the given rectangle.
     * The rectangle is relative to the top left corner of the layout.
     * The component will be moved to a non-overlapping position if necessary.
     *
     * @param component to add.
     * @param rectangle where to add the component.
     * @param side to which it should be moved if overlapping.
     */
    public void add(Component component, Rectangle rectangle, Side side) {

        var style = component.getElement().getStyle();

        var overlap = rectangles.stream().filter(r -> r.intersects(rectangle)).findFirst().orElse(null);

        if (overlap != null) {
            add(component, onSide( rectangle, overlap, side), side);
        } else {

            rectangles.add(rectangle);

            add(component);

            style.set("position", "absolute");
            style.set("top",    rectangle.y + "px");
            style.set("left",   rectangle.x + "px");
            style.set("width",  rectangle.width + "px");
            style.set("height", rectangle.height + "px");
        }
    }

}