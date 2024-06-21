package vaadin.app.views.playGame.scene;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import rea.components.*;
import rea.components.Character;
import rea.gaming.Action;
import rea.gaming.GameInstance;
import rea.gaming.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * HorizontalPanel with scene from the game
 */
public class ScenePanel extends HorizontalLayout {
    GameInstance gameInstance;
    Player player;
    SceneLayout sceneLayout;
    int backgroundHeight = 500;

    /**
     * Size of positionables on the styles
     */
    final int POSITIONABLE_HEIGHT = 120;

    /**
     * Constructor
     * @param gi GameInstance being played
     * @param p Player playing the game
     */
    public ScenePanel(GameInstance gi, Player p)  {
        // Attribute Initializations
        this.sceneLayout = new SceneLayout();
        this.gameInstance = gi;
        this.player = p;

        // Initialize a sceneLayout
        sceneLayout = new SceneLayout();
        sceneLayout.setAbsoluteCoordinates();
        //sceneLayout.addClassName("highlight_green");
    }

    /**
     * Changes the background in the scene.
     * @param bgVisual Visual for the background
     */
    public void changeBackground (Visual bgVisual) {
        backgroundHeight = bgVisual.getHeight() - POSITIONABLE_HEIGHT;
        Image bgImage = new Image(bgVisual.getPathname(), bgVisual.getPathname());
        sceneLayout.setBackground(bgImage,bgVisual.getWidth(),bgVisual.getHeight());
    }

    /**
     * Displays a list of positionables in the SceneLayout,
     * each one with menus for possible actions to perform.
     * @param positionables List of Positionables
     */
    public void displayPositionables(List<Positionable> positionables) {
        // used to avoid overlapping
        int characterCount = 0;

        for (Positionable p : positionables) {
            String className = p.getClass().getName().toLowerCase();
            className = className.substring(className.lastIndexOf(".") + 1);

            Image img = createPositionableImage(p);

            img.setWidth(String.valueOf(p.getVisual().getWidth()));
            img.setHeight(String.valueOf(p.getVisual().getHeight()));

            // Image Style
            img.addClassNames("scene-positionable-max-size",
                    className + "-positionable-img",
                    LumoUtility.Margin.XSMALL
            );

            // Menu with actions
            MenuBar menuBar = new MenuBar();
            menuBar.setHeight((POSITIONABLE_HEIGHT - 10) + "px");
            menuBar.addClassName(LumoUtility.Background.TRANSPARENT);
            MenuItem infoMenu = menuBar.addItem(img);
            infoMenu.addClassNames("scene-positionable-size-added",
                    LumoUtility.Background.TRANSPARENT);
            SubMenu subMenu = infoMenu.getSubMenu();

            // - Look and Use are possible interactions with any positionable.
            MenuItem lookMenu = subMenu.addItem(VaadinIcon.EYE.create());
            lookMenu.add("Look");
            lookMenu.addClickListener(e -> {
                String thingLooked = (p == player.getCharacter())? "image of yourself and contemplated it" : p.getDescription();
                String msg = gameInstance.executeCommand(player, Action.LOOK, p);
                Notification notification = Notification.show((msg != null ? "You tried to look at " : "You looked at ") + getStringWithArticle(thingLooked));
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            });
            lookMenu.setEnabled(true);

            MenuItem useMenu = subMenu.addItem(VaadinIcon.MAGIC.create());
            useMenu.add("Use");
            useMenu.addClickListener(e -> {
                Item holdingItemBeforeUse = player.getCharacter().getHolding();
                String holdingBeforeUseName = holdingItemBeforeUse.getDescription();
                String msg = gameInstance.executeCommand(player, Action.USE, p);
                Notification.show((msg != null ? "You tried to use" : "You used")
                        + (holdingItemBeforeUse == null ? " nothing" : " " + holdingBeforeUseName)
                        + " into " + p.getDescription());
            });
            useMenu.setEnabled(true);

            // - Set it all to Enabled = false, to set it to true if needed
            MenuItem backMenu = subMenu.addItem(VaadinIcon.ARROW_BACKWARD.create());
            backMenu.add("Back");
            backMenu.addClickListener(e -> {
                String msg = gameInstance.executeCommand(player, Action.BACK, null);
                Notification.show((msg != null ? "You tried to move back" : "You moved back") + " to previous place");
                if (msg != null) {
                    Notification notification = Notification.show(p.getDescription() + ": " + msg);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            backMenu.setEnabled(false);

            MenuItem talkMenu = subMenu.addItem(VaadinIcon.MEGAPHONE.create());
            talkMenu.add("Talk");
            talkMenu.addClickListener(e -> {
                Dialog dialog = new Dialog();
                VerticalLayout talkLayout = new VerticalLayout();
                H3 header = new H3("Talk option dialog");
                Text hint = new Text("Please enter the message you wish to send and then press enter");
                TextField sendMessage = new TextField();
                sendMessage.setHelperText("Enter message");
                Button sendButton = new Button("Send");

                sendButton.addClickListener(event -> {
                    gameInstance.executeCommand(player, Action.TALK, sendMessage.getValue());
                    dialog.close();
                });

                sendButton.addClickShortcut(Key.ENTER);

                talkLayout.add(header, hint, sendMessage, sendButton);

                dialog.add(talkLayout);
                dialog.open();

            });

            // Only talks if to a Player, and if there is someone else in the scene
            talkMenu.setEnabled((p instanceof Character) && (gameInstance.getPlayerCount() > 1));

            MenuItem pickMenu = subMenu.addItem(VaadinIcon.INSERT.create());
            pickMenu.add("Pick");
            pickMenu.addClickListener(e -> {
                String msg = gameInstance.executeCommand(player, Action.PICK, p);
                Notification.show((msg != null ? "You tried to pick " : "You picked ") + p.getDescription());
            });
            pickMenu.setEnabled(false);

            MenuItem moveMenu = subMenu.addItem(VaadinIcon.ARROW_FORWARD.create());
            moveMenu.add("Move");
            moveMenu.addClickListener(e -> {
                String msg = gameInstance.executeCommand(player, Action.MOVE, p);
                Notification.show((msg != null ? "You tried to move to " : "You moved to ") + p.getDescription());
            });
            moveMenu.setEnabled(false);

            // Randomness not used if positionable is a Character,
            // because of the Move action.
            int positionX = p.getPosition().getX();
            int positionY = p.getPosition().getY();

            if (!(p instanceof Character)) {
                positionX = addExtraRandomness(p.getPosition().getX());
                positionY = addExtraRandomness(p.getPosition().getY());

                // Enables Pick if item is pickable
                pickMenu.setEnabled(p instanceof Item item && item.isPickable());

                // Enables Move if it's a Passage
                moveMenu.setEnabled(p instanceof Passage);
            } else {
                // Enables Back if the Character is himself and if not in the StartPlace
                backMenu.setEnabled((p == player.getCharacter())
                        && (player.getCharacter().getPlace() != gameInstance.getGameMap().getStartPlace()));

                // Avoids overlapping of characters
                positionY = p.getPosition().getY() + characterCount * POSITIONABLE_HEIGHT;
                characterCount++;

            }
            sceneLayout.add(menuBar, positionX, positionY);
        }
    }

    /**
     * Returns a grammatically correct English language string with defined articles.
     * @param str string without article
     * @return string with article
     */
    private String getStringWithArticle(String str) {
        String lowerStr = str.toLowerCase();
        for ( char c : new char[]{'a', 'e', 'i', 'o', 'u'}){
            if (c == lowerStr.charAt(0))
                return "an " + str;
        }
        return "a " + str;
    }

    /**
     * Resets the Scene by removing all positionables in it.
     */
    public void resetSceneLayout(){
        sceneLayout.removeAll();
    }

    /**
     * Creates a Vaadin Image html component from a Positionable
     * @param p positionable
     * @return image created
     */
    private Image createPositionableImage(Positionable p){
        String description = p.getDescription();

        Image img = new Image(
                p.getVisual().getPathname(),
                description!= null? description : "no description");

        img.setWidth(String.valueOf(p.getVisual().getWidth()));
        img.setHeight(String.valueOf(p.getVisual().getHeight()));

        return  img;
    }

    /**
     * Creates random positions based on the backgroundHeight.
     * @param position initial position
     * @return random position
     */
    private int addExtraRandomness(int position){
        Random random = new Random();
        return random.nextInt(backgroundHeight - position);
    }

    /**
     * Getter method for SceneLayout.
     * @return SceneLayout
     */
    public SceneLayout getSceneLayout() {
        return sceneLayout;
    }

}
