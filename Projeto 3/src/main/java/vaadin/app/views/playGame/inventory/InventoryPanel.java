package vaadin.app.views.playGame.inventory;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import rea.components.Character;
import rea.components.Item;
import rea.gaming.Action;
import rea.gaming.GameInstance;
import rea.gaming.Player;

import java.util.List;

/**
 * Horizontal Panel that has inventory items
 */
public class InventoryPanel extends HorizontalLayout {
    Player player;
    GameInstance gameInstance;
    Character character;
    public List<Item> itemList;
    public Item holdingItem;
    HorizontalLayout itemListLayout = new HorizontalLayout();
    VerticalLayout holdingItemLayout = new VerticalLayout();
    Span title = new Span("Your Inventory");

    /**
     * Constructor
     * @param p Player playing
     * @param g GameInstance being played
     */
    public InventoryPanel(Player p ,GameInstance g) {
        // Initialization
        this.gameInstance = g;
        this.player = p;
        this.character = player.getCharacter();
        this.itemList = character.getInventory();
        this.holdingItem = character.getHolding();

        System.out.println("======InventoryPannel=======");
        System.out.println(character.getInventory());
        System.out.println("======InventoryPannel=======");

        // Style
        setupStyles();

        populatesItemListLayout();

        add(title, holdingItemLayout, itemListLayout);
    }

    /**
     * Sets up all style configurations for the items in the InventoryPanel
     */
    private void setupStyles() {
        setPadding(true);

        itemListLayout.addClassNames(
                LumoUtility.Overflow.SCROLL,
                LumoUtility.Gap.SMALL,
                LumoUtility.Padding.SMALL);
        itemListLayout.setSizeFull();
        itemListLayout.add(new Text("Empty inventory"));

        holdingItemLayout.add(new Text("Holding nothing"));
        holdingItemLayout.addClassNames(
                "inventory-holding-item-size",
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.SMALL,
                LumoUtility.Gap.XSMALL,
                LumoUtility.Background.PRIMARY_10,
                LumoUtility.Border.ALL,
                LumoUtility.AlignContent.CENTER,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.AlignSelf.CENTER,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Display.INLINE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Overflow.HIDDEN);
        holdingItemLayout.getStyle().set("width", "var(--holding-item)");
        holdingItemLayout.getStyle().set("min-width", "var(--holding-item)");
        holdingItemLayout.getStyle().set("height", "100%");

        title.addClassNames(LumoUtility.FontWeight.BOLD,
                LumoUtility.FontSize.SMALL,
                LumoUtility.AlignContent.CENTER);
        title.getStyle().set("writing-mode", "sideways-lr");
    }

    /**
     * Adds Item character is holding to panel holdingItemLayout, or adds a
     * "Holding nothing" message, if character is not holding anything at the moment.
     */
    public void populatesHoldingItemLayout(){
        if (holdingItem == null) {
            holdingItemLayout.removeAll();
            holdingItemLayout.add(new Text("Holding nothing"));
        }
        else {
            Image img = new Image(holdingItem.getVisual().getPathname(), holdingItem.getDescription());
            img.addClassName("inventory-item-max-size");

            Button itemButton = new Button(img);
            itemButton.addClassNames("inventory-item-size-added",
                    LumoUtility.Background.TRANSPARENT);

            Span subtitle = new Span("Holding");
            subtitle.addClassNames(LumoUtility.FontSize.XSMALL);

            holdingItemLayout.add(itemButton, subtitle);
        }
    }

    /**
     * Refreshes inventory panel's itemListLayout and holdingPanelLayout
     * @param itemList list of items in character's inventory
     * @param holdingItem item character holds
     */
    public void refreshInventoryPanelData(List<Item> itemList, Item holdingItem) {
        itemListLayout.removeAll();
        this.itemList = itemList;
        populatesItemListLayout();

        holdingItemLayout.removeAll();
        this.holdingItem = holdingItem;
        populatesHoldingItemLayout();
    }

    /**
     * Adds Items from itemList to panel itemListDisplay, or adds an
     * "Empty Inventory" message, if not items are in the list.
     */
    private void populatesItemListLayout(){
        if (itemList == null || itemList.isEmpty()){
            itemListLayout.removeAll();
            itemListLayout.add(new Text("Empty inventory"));
        }
        else {
            for (Item item : itemList) {
                HorizontalLayout itemLayout = new HorizontalLayout();

                boolean iReusable = item.isReusable();

                Image itemImg = new Image(item.getVisual().getPathname(), item.getDescription());
                itemImg.addClassName("inventory-item-max-size");

                Button itemButton = new Button(itemImg);
                itemButton.addClassNames("inventory-item-size-added",
                        LumoUtility.Background.TRANSPARENT);

                Span descriptionSpan = new Span(item.getDescription());

                Span reusableBadge = new Span(VaadinIcon.RECYCLE.create());
                String theme = String.format("badge pill %s", iReusable ? "success" : "error");
                reusableBadge.getElement().setAttribute("theme", theme);
                reusableBadge.setEnabled(iReusable);

                Button holdButton = new Button(VaadinIcon.GRAB.create());
                holdButton.addClickListener(e -> {
                    Notification.show("You held item");
                    gameInstance.executeCommand(player, Action.HOLD, item);
                });
                Button dropButton = new Button(VaadinIcon.EXTERNAL_LINK.create());
                dropButton.addClickListener(e -> {
                    Notification.show("You dropped item");
                    if (item == character.getHolding())
                        character.holdItem(null);
                    gameInstance.executeCommand(player, Action.DROP, item);
                });

                itemLayout.add(itemButton, new VerticalLayout(
                        new HorizontalLayout(descriptionSpan, reusableBadge),
                        new HorizontalLayout(holdButton, dropButton)
                ));

                itemLayout.addClassNames(//"highlight_green",
                        LumoUtility.Margin.NONE,
                        LumoUtility.Padding.SMALL,
                        LumoUtility.Gap.XSMALL,
                        LumoUtility.Background.CONTRAST_5,
                        LumoUtility.BoxShadow.SMALL,
                        LumoUtility.BorderRadius.LARGE);

                itemListLayout.add(itemLayout);

                itemListLayout.setSizeFull();

            }
        }
    }

}
