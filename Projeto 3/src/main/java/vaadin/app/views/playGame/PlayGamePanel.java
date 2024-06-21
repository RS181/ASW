package vaadin.app.views.playGame;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import rea.Manager;
import rea.ReaException;
import rea.components.Character;
import rea.components.Item;
import rea.components.Positionable;
import rea.gameplay.games.CartoonAvatar;
import rea.gaming.Action;
import rea.gaming.GameInstance;
import rea.gaming.GameStage;
import rea.gaming.Player;
import vaadin.app.views.MainView;
import vaadin.app.views.playGame.avatar.AvatarPanel;
import vaadin.app.views.playGame.chat.ChatPanel;
import vaadin.app.services.ChatService;
import vaadin.app.views.playGame.inventory.InventoryPanel;
import vaadin.app.views.playGame.scene.SceneLayout;
import vaadin.app.views.playGame.scene.ScenePanel;

/**
 * Panel for playing a game.
 */
public class PlayGamePanel extends VerticalLayout {
    static private ChatService chatService = new ChatService();
    private  final String MAX_HEIGHT_SCROLLER = "200px";
    GameInstance gameInstance;
    Character character;

    // Panels
    AvatarPanel avatarPanel;
    ScenePanel scenePanel;
    ChatPanel chatPanel;
    InventoryPanel inventoryPanel;

    // Elements
    SceneLayout sceneLayout;
    ConfirmDialog playDialog;
    VerticalLayout content = new VerticalLayout();
    Span welcomeSpan = new Span();

    private Manager manager;

    //este campo é que vai guardar o player
    Player player;

    MainView mainView;

    public PlayGamePanel(Player p,GameInstance gi,MainView main) throws ReaException {

        // Initializations
        this.gameInstance = gi;
        this.player = p;
        this.character = player.getCharacter();
        this.mainView = main;
        manager = Manager.getInstance();

        try {
            addListenersToGameInstance();
        } catch (ReaException e) {
            Notification.show("ERROR in addListeners : PlayGamePanel");
            throw new RuntimeException(e);
        }

        playDialog = createPlayDialog();

        HorizontalLayout topPanels = createTopPanelLayout();
        HorizontalLayout bottomPanels = createBottomPanelLayout();

        content = new VerticalLayout();
        content.add(playDialog, topPanels, bottomPanels);

        // Style
        content.setSizeFull();
        this.getStyle().set("margin", "0px");
        this.setSizeFull();
        this.addClassNames(
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.NONE,
                LumoUtility.Gap.XSMALL,
                LumoUtility.Overflow.HIDDEN
        );

        // Adds different content according to GameStage
        boolean isEnded = gameInstance.getCurrentStage().equals(GameStage.ENDED);
        if (isEnded) {
            welcomeSpan.setText(character.getName() + ", " + gameInstance.getName() + " ended. Select a new game to play. ");
            welcomeSpan.add(new Button("Go to Manage Games", e-> mainView.goToTab(mainView.manageGamesTab)));
        }
        if ( gameInstance.getCurrentStage().equals(GameStage.PLAYING) ) {
            welcomeSpan.setText(character.getName() + ", " + gameInstance.getName() + " has started playing already and you can't join. Select a new game to play. ");
            welcomeSpan.add(new Button("Go to Manage Games", e-> mainView.goToTab(mainView.manageGamesTab)));
        }

        // Hides content if no game is being played
        if ( !(character.getName().equals("Anonymous")) && !(isEnded) ) {
            welcomeSpan.setText(character.getName() + ", welcome to this new " + gameInstance.getName() + " adventure!");
            welcomeSpan.setVisible(false);

            add(content);
            playDialog.open();
        }
    }

    /**
     * Adds listeners to the GameInstance being played
     * @throws ReaException except
     */
    private void addListenersToGameInstance() throws ReaException {

        gameInstance.addGameChangedListener( e -> {
            if (e.getGameInstance().getCurrentStage().equals(GameStage.ENDED)){
                // Parte que faz o "reset" da characterbeyingplayed
                mainView.setCharacterBeingPlayed(new Character("Anonymous", CartoonAvatar.BUNNY));
                mainView.removePlayGameTab();
                mainView.goToTab(mainView.manageGamesTab);

                //Display a notification indication tha the game ended
                //this.getUI().get().access(()->{
                //    Notification.show("Game has ended!").addThemeVariants(NotificationVariant.LUMO_WARNING);
                //});
            }
        });

        gameInstance.addInventoryUpdateListener(player,e ->{
            inventoryPanel.refreshInventoryPanelData(e.getInventory(), e.getHolding());

            //We tried using gameInstance.gameplay.gamedEnded(gameInstance.getGameMap())
            // but for some reason it didn't remove the treasure from the gameMap
            // , but it removed the key (we did a workaround , just verifying
            // if the player has the treasure , wich indicates we can end
            // in TreasureHunt) . For the EasterEggRace  check bellow
            //
            for (Item i : player.getCharacter().getInventory()){
                if (i.getDescription().equals("treasure")){
                    gameInstance.endPlayingGame();
                }
            }

        });

        gameInstance.addSceneUpdateListener(player , e ->{
            scenePanel.resetSceneLayout();
            scenePanel.changeBackground(e.getBackground());
            scenePanel.displayPositionables(e.getPositionables());

            //We had to put gameplay public to make this possible
            if (gameInstance.gameplay.gamedEnded(gameInstance.getGameMap())){
                gameInstance.endPlayingGame();
            }
        });

        gameInstance.addMessageUpdateListener(player ,e -> {
            String speaker = e.getSpeaker().getName();
            String message = e.getMessage();

            this.getUI().get().access(()->{
                Notification.show(speaker + " : " + message).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            });
        });
    }

    public void testInventoryUpdateListener(GameInstance g){
        for(Positionable p : g.getGameMap().getStartPlace().getPositionables()){
            if(p instanceof  Character c ){
                //System.out.println("---->Character : " + c.getName());
            }
            else {
                if (p.getDescription().equals("key"))
                    System.out.println(gameInstance.executeCommand(player,Action.PICK,p));
                //System.out.println("---->" + p.getDescription());
            }
        }
    }


    private void displayManagerInfo() {
        System.out.println("======PlayGamePannel=======");
        for (GameInstance g : manager.getGameInstances()){
            System.out.println("-->" + g.getName());
            displayPositionable(g);
        }
        System.out.println("======PlayGamePannel=======");
    }
    private void displayPositionable(GameInstance g){
        for(Positionable p : g.getGameMap().getStartPlace().getPositionables()){
            if(p instanceof  Character c ){
                System.out.println("---->Character : " + c.getName());
            }
            else {

                System.out.println("---->" + p.getDescription());
            }
        }
    }

    /**
     * Creates the bottom panel layout.
     * @return VerticalLayout with bottom panel
     */
    private HorizontalLayout createBottomPanelLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        avatarPanel = createAvatarPanel();
        inventoryPanel = createInventoryPanel();

        // Style
        horizontalLayout.addClassNames(
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.SMALL,
                LumoUtility.Gap.XSMALL,
                LumoUtility.Overflow.HIDDEN,
                LumoUtility.Width.FULL,
                "bottom-layout-height-added"
        );

        avatarPanel.addClassNames(
                "bottom-layout-width",
                "bottom-layout-height",
                LumoUtility.Overflow.HIDDEN
        );
        inventoryPanel.setWidthFull();
        inventoryPanel.addClassNames(
                "bottom-layout-height",
                LumoUtility.Overflow.SCROLL
        );

        horizontalLayout.add(avatarPanel, inventoryPanel);

        return horizontalLayout;
    }

    /**
     * Creates Panel with Avatar, to be displayed in the bottom left corner of screen.
     * @return AvatarPanel
     */
    private AvatarPanel createAvatarPanel() {
        AvatarPanel avatarPanel = new AvatarPanel(player,gameInstance);

        // Style
        avatarPanel.setWidthFull();

        return avatarPanel;
    }

    /**
     * Creates Panel with Inventory, to be displayed in the bottom of screen.
     * @return InventoryPanel
     */
    private InventoryPanel createInventoryPanel() {
        InventoryPanel inventoryPanel = new InventoryPanel(player,gameInstance);

        // Style
        inventoryPanel.setWidthFull();
        inventoryPanel.addClassNames(
                LumoUtility.Background.PRIMARY_10,
                LumoUtility.Margin.NONE,
                LumoUtility.Padding.SMALL,
                LumoUtility.Gap.XSMALL,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.TextOverflow.CLIP,
                LumoUtility.Width.LARGE,
                LumoUtility.Height.LARGE,
                LumoUtility.Overflow.HIDDEN);

        return inventoryPanel;
    }

    /**
     * Creates the top panel layout.
     * @return HorizontalLayout with top panel
     */
    private HorizontalLayout createTopPanelLayout() {

        // Initializations
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        scenePanel = new ScenePanel(gameInstance, player);
        sceneLayout  = scenePanel.getSceneLayout();
        chatPanel = createChatPanel();

        // Style
        scenePanel.addClassNames("scene-panel");
        horizontalLayout.setSizeFull();

        horizontalLayout.add(
                sceneLayout,
                chatPanel
        );

        return horizontalLayout;
    }

    private ChatPanel createChatPanel(){
        chatPanel = new ChatPanel(chatService);

        chatPanel.addClassNames("scroll-panel");

        return chatPanel;
    }

    /**
     * Creates confirm dialog that shows before playing game.
     * Buttons are shown accordingly to posibilities allowed in the GameInstance.
     * Possible buttons:
     * - start playing game, if not started;
     * - continue playing, if game has started;
     * - confirm/cancel, dismisses the dialog.
     * @return ConfirmDialog.
     */
    private ConfirmDialog createPlayDialog() {
        // Update game and character
        playDialog = new ConfirmDialog();

        playDialog.setHeader("Waiting to Play Game");

        VerticalLayout dialogLayout = new VerticalLayout();

        Avatar characterAvatar = new Avatar(character.getName(), character.getVisual().getPathname());
        H4 characterNameText = new H4(character.getName());
        Span gameNameText = new Span("You've joined " + gameInstance.getName());

        Span morePlayersText = new Span(
                gameInstance.canJoin()?
                        (gameInstance.canStart()?
                                "Wait for more players to Join or Start Playing Now!"
                                : "You have to wait more players to join to start game!")
                        : (gameInstance.getCurrentStage().equals(GameStage.ENDED)?
                        "Game Ended. Go to Manage Games and pick a new game to play."
                        : "No more players can join. Play Now!")
        );

        Boolean isPlaying = gameInstance.getCurrentStage().equals(GameStage.PLAYING);
        dialogLayout.add(characterAvatar,
                characterNameText,
                gameNameText,
                morePlayersText
        );

        playDialog.add(dialogLayout);

        // Game Stage: CREATED (not playing yet, click to Start Playing)
        Button startPlayingButton = new Button("Start Playing!");
        startPlayingButton.addClickListener(e -> {
            // Chat config
            chatPanel.setMessageUsername(character.getName());
            chatPanel.setUserImage(character.getVisual().getPathname());

            // Start Game
            if(gameInstance.isNotPlayingYet())
                gameInstance.startPlayingGame();
            playDialog.close();
        });

        if (!gameInstance.canStart())
            startPlayingButton.setEnabled(false);

        playDialog.setConfirmButton(startPlayingButton);

        // Game stage: PLAYING (already playing)
        Button keepPlayingButton  = new Button("Play Now!", e -> playDialog.close());
        keepPlayingButton.setVisible(isPlaying);
        playDialog.setRejectButton(keepPlayingButton);

        playDialog.setRejectable(gameInstance.getCurrentStage().equals(GameStage.PLAYING));
        if (isPlaying)
            startPlayingButton.setVisible(false);

        // Game stage: ENDED (clicking goes to Manage Games tab)
        Button gotoManageButton  = new Button("Go to Manage Games", e -> { mainView.goToTab(mainView.manageGamesTab); });
        playDialog.setCancelButton(gotoManageButton);

        playDialog.setCancelable(gameInstance.getCurrentStage().equals(GameStage.ENDED));

        return playDialog;
    }

    /**
     * Getter method for player.
     * @return Player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Setter method for player.
     * @param player player in the game
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
}
