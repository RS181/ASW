package vaadin.app.views.playGame.avatar;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import rea.components.Character;
import rea.gaming.GameInstance;
import rea.gaming.Player;

/**
 * VerticalLayout with Avatar information and End Game button
 */
public class AvatarPanel extends VerticalLayout {
    Character character;
    Span avatarName;
    Avatar avatar;
    Button endGameButton;

    /**
     * Constructor
     * @param p Player
     * @param gi GameInstance
     */
    public AvatarPanel(Player p, GameInstance gi) {

        this.character = p.getCharacter();

        avatar = new Avatar(character.getName(), character.getVisual().getPathname());
        avatarName = new Span(avatar.getName());
        endGameButton = new Button("End game");
        endGameButton.addClickListener(e -> {
            gi.endPlayingGame();
        });

        setupStyles();

        add(avatar, avatarName , endGameButton);
    }

    /**
     * Sets up every style in AvatarPanel
     */
    private void setupStyles() {

        // AvatarPanel Style
        this.setAlignItems(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);
        this.addClassNames(
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.SMALL,
                LumoUtility.Gap.XSMALL,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextOverflow.CLIP,
                LumoUtility.Width.LARGE,
                LumoUtility.Height.LARGE);

        // Style Avater
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);

        // Style Avatar Name
        avatarName.addClassNames(
                LumoUtility.FontWeight.BOLD,
                LumoUtility.Overflow.HIDDEN,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.AlignContent.CENTER,
                LumoUtility.Width.FULL,
                LumoUtility.Height.SMALL);
    }

}
