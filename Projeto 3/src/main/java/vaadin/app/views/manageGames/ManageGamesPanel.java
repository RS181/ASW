package vaadin.app.views.manageGames;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import rea.Manager;
import rea.ReaException;
import rea.components.Avatar;
import rea.components.Character;
import rea.gameplay.games.CartoonAvatar;
import rea.gaming.GameInstance;
import rea.gaming.GameStage;
import rea.gaming.Player;
import vaadin.app.views.MainView;
import vaadin.app.views.playGame.PlayGamePanel;

import java.util.*;

/**
 * Painel de gestão dos jogos.
 * Permite criar instâncias de jogo,
 * bem como ver informações sobre os jogos já criados,
 * e juntar-se a eles para começar a jogar.
 */
public class ManageGamesPanel extends VerticalLayout {
    public Grid<GameInstance> grid;

    private AddGamesLayout newGameButtons;
    private MainView mainView;
    private Manager manager;
    PlayGamePanel playGamePanel;
    public Tab playGameTab;


    /**
     * Constructor
     * @param main MainView
     */
    public ManageGamesPanel(MainView main) throws ReaException {
        mainView = main;
        manager = Manager.getInstance();

        // Style
        addClassName("manage-panel");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Content
        configurateGameInstancesGrid();

        newGameButtons = new AddGamesLayout(grid);

        add( new H2("Manage Games"),
                newGameButtons,
                grid
        );

        manager.updateGrid(grid);
    }

    /**
     * Create columns and sets style for game instances grid.
     */
    private void configurateGameInstancesGrid() {
        grid = new Grid<>(GameInstance.class, false);

        // Style
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES); // alternated colouring of rows
        grid.setWidthFull();
        grid.setAllRowsVisible(true); // dynamic height
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_COMPACT);
        grid.addClassName(LumoUtility.AlignItems.CENTER);

        // Defining Columns
        grid.addColumn(createJoinComponentRenderer())
                .setHeader("Join")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(GameInstance::getName)
                .setHeader("Game Name")
                .setAutoWidth(true);

        grid.addColumn(createCurrentStageComponentRenderer())
                .setHeader("Current Stage")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(GameInstance::getPlayerCount)
                .setHeader("Players in Game")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(GameInstance::getPlayingSince)
                .setHeader("Started Playing");

        grid.addColumn(GameInstance::getPlayingUntil)
                .setHeader("Ended Playing");

        grid.addColumn(createEndComponentRenderer())
                .setHeader("End")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);

        grid.addColumn(createDeleteComponentRenderer())
                .setHeader("Delete")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0);
    }

    /**
     * Component renderer for the Play game button for the grid.
     * @return Play button for a determined game instance
     */
    private ComponentRenderer<Button, GameInstance> createPlayButtonComponentRenderer() {
        SerializableBiConsumer<Button, GameInstance> statusComponentUpdater = (
                button, gameInstance) -> {

            // Style
            button.setIcon(VaadinIcon.PLAY.create());
            button.setEnabled(gameInstance.canStart());

            // On Click
            button.addClickListener(new PlayButtonListener(gameInstance));
        };

        return new ComponentRenderer<>(Button::new, statusComponentUpdater);
    }

    /**
     * Component renderer for the End game button for the grid.
     * @return End button for a determined game instance
     */
    private ComponentRenderer<Button, GameInstance> createEndComponentRenderer() {
        SerializableBiConsumer<Button, GameInstance> statusComponentUpdater = (
                button, gameInstance) -> {
            boolean isPlaying = "playing".equalsIgnoreCase(gameInstance.getCurrentStage().toString());

            // Style
            button.setIcon(VaadinIcon.CLOSE.create());
            button.setEnabled(isPlaying);

            // on Click
            button.addClickListener(e -> gameInstance.endPlayingGame() );
            button.addClickListener(e ->  manager.updateGrid(grid) );

        };

        return new ComponentRenderer<>(Button::new, statusComponentUpdater);
    }

    /**
     * Component renderer for the Join game button for the grid.
     * @return Join button for a determined game instance
     */
    private ComponentRenderer<Button, GameInstance>
    createJoinComponentRenderer() {
        SerializableBiConsumer<Button, GameInstance> statusComponentUpdater = (
                button, gameInstance) -> {
            boolean canJoin = gameInstance.canJoin();

            // Style
            button.setIcon(VaadinIcon.PLUS.create());
            button.setEnabled(canJoin);

            // on Click
            button.addClickListener(e -> createCharacterDialog(gameInstance).open() );
            button.addClickListener(e ->  manager.updateGrid(grid) );
        };

        return new ComponentRenderer<>(Button::new, statusComponentUpdater);
    }

    /**
     * Created a Dialog for character creation, and adds player with this Character to game.
     * @param gi game instance of game to be played
     * @return Dialog
     */
    private Dialog createCharacterDialog(GameInstance gi) {
        Dialog dialog = new Dialog();

        //TODO isto é uma forma de impedir que sejam criadas varia tabs
        // Play game , mas isto implica que vamos ter que ter um cuidado extra
        // de quando um jogo termina , temos de fazer o
        // mainView.setCharacterBeingPlayed(new Character (Anonymous, CartoonAvatar.BUNNY ))
        if (mainView.getCharacterBeingPlayed().getName().equals("Anonymous")) {

            dialog.setHeaderTitle("Create your Character");

            VerticalLayout dialogLayout = new VerticalLayout();

            TextField charNameTextField = new TextField();
            charNameTextField.setLabel("Character's name");
            charNameTextField.setRequired(true);
            charNameTextField.setRequiredIndicatorVisible(true);
            charNameTextField.setErrorMessage("This field is required");

            Select selectAvatar = createAvatarSelectField(gi);
            selectAvatar.setRequiredIndicatorVisible(true);
            selectAvatar.setErrorMessage("This field is required");

            dialogLayout.add(charNameTextField, selectAvatar);

            dialog.add(dialogLayout);

            Button saveButton = new Button("Confirm Character", e -> {
                // Character creation
                String characterName = charNameTextField.getValue();
                String characterAvatar = selectAvatar.getValue().toString();

                Avatar newAvatar = CartoonAvatar.valueOf(characterAvatar);
                Character newCharacter = new Character(characterName, newAvatar);

                System.out.println("Character name = " + mainView.getCharacterBeingPlayed().getName());

                //TODO verificar código comentado
                mainView.setCharacterBeingPlayed(newCharacter);
                mainView.setGameInstanceBeingPlayed(gi);

                //TODO cria a playgame tab quando jogador faz join

                Player p = gi.addPlayer(newCharacter);
                try {
                    createPlayGameTab(p);
                } catch (ReaException ex) {
                    throw new RuntimeException(ex);
                }

                manager.updateGrid(grid);

                //TODO REMOVI O RELOAD DA PAGINA PARA IMPEDIR DE FAZER
                // REFRESH DA MAINVIEW , QUE FAZ RESET AS ALTERAÇÕES FEITAS
                //mainView.goToTab(mainView.playGameTab);
                //UI.getCurrent().getPage().reload();
                dialog.close();
            });

            Button cancelButton = new Button("Cancel", e -> dialog.close());
            dialog.getFooter().add(cancelButton);
            dialog.getFooter().add(saveButton);

        } else {
            dialog.setHeaderTitle("You are alreading playing a game !!!");
            dialog.add(new Text("You can only play one game at a time"));
            Button okButton = new Button("OK!!", e -> dialog.close());
            dialog.getFooter().add(okButton);
        }
        return dialog;
    }

    /**
     * Create Play Game tab
     * @param p player
     */
    private void createPlayGameTab(Player p) throws ReaException {
        playGamePanel = new PlayGamePanel(p,mainView.getGameInstanceBeingPlayed(),mainView);
        playGamePanel.setSizeFull();
        playGameTab = new Tab(VaadinIcon.GAMEPAD.create(), new Span("Play Game"));
        mainView.addTabToTabSheet(playGameTab,new Div(playGamePanel));
    }

    /**
     * Gets different avatar possibilities for game
     * @param gameInstance game instance to get possible avatars from
     * @return Set of rea.components.Avatar
     */
    public Set<Avatar> getAvatarList(GameInstance gameInstance) {
        return gameInstance.getAvatars();
    }

    /**
     * Select component with options of Avatars for the chosen game.
     * @param gi GameInstance for the chosen game
     * @return Select component
     */
    private Select createAvatarSelectField(GameInstance gi) {
        Select<Avatar> select = new Select<>();
        select.setLabel("Avatars");

        // TODO: Use a custom renderer for items in the dropdown
        //select.setRenderer(createAvatarRenderer());

        // Display name as selected value label
        select.setItemLabelGenerator(Avatar::getAvatarName);
        select.setPlaceholder("Select a avatar");
        select.setPrefixComponent(VaadinIcon.USER.create());

        Set<Avatar> avatarSet = getAvatarList(gi);

        select.setItems(avatarSet);

        return select;
    }


    /**
     * Component renderer for the Delete game button for the grid.
     * @return Delete button for a determined game instance
     */
    private  ComponentRenderer<Button, GameInstance>
    createDeleteComponentRenderer() {

        SerializableBiConsumer<Button, GameInstance> statusComponentUpdater = (
                button, gameInstance) -> {
            boolean canDelete = gameInstance.canDelete();

            // Style
            button.setIcon(VaadinIcon.TRASH.create());
            button.setEnabled(canDelete);

            // On Click
            button.addClickListener(e -> manager.deleteGameInstance(gameInstance) );
            button.addClickListener(e -> manager.updateGrid(grid) );
        };
        return new ComponentRenderer<>(Button::new, statusComponentUpdater);
    }

    /**
     * Component renderer for gameStage used on the grid.
     * Game stage is shown in a Vaadin badge theme.
     * @return Span with game stage for a determined game instance
     */
    private ComponentRenderer<Span, GameInstance> createCurrentStageComponentRenderer() {

        SerializableBiConsumer<Span, GameInstance> statusComponentUpdater = (
                span, gameInstance) -> {
            String currentStage = gameInstance.getCurrentStage().toString();
            Boolean canStart = gameInstance.canStart();

            if (canStart && currentStage == "CREATED"){
                currentStage = "Waiting to Start";
            }
            span.setText(currentStage);

            boolean isCreated = "created".equals(currentStage.toLowerCase());
            boolean isPlaying = "playing".equals(currentStage.toLowerCase());
            boolean isEnded = "ended".equals(currentStage.toLowerCase());

            // Style
            String theme = String.format("badge pill %s", isPlaying ?
                    "error"
                    : (isEnded ?
                        "contrast"
                        : (isCreated ?
                            "success"
                            : "warning")));

            span.getElement().setAttribute("theme", theme);
        };

        return new ComponentRenderer<>(Span::new, statusComponentUpdater);
    }

    /**
     * Class for Listener for Play button on grid.
     */
    class PlayButtonListener
            implements ComponentEventListener<ClickEvent<Button>> {
        GameInstance gameInstance;
        public PlayButtonListener(GameInstance gi) {
            this.gameInstance = gi;
        }

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            gameInstance.startPlayingGame();

            // Se jogo estiver como Playing: Desativar botão Play. Ativar botão End.
            if (gameInstance.getCurrentStage() == GameStage.PLAYING){

                // Sets button Play to Disabled
                event.getSource().setEnabled(false);

                //TODO verificar este abaixo
                mainView.setGameInstanceBeingPlayed(gameInstance);

                // Goes to Play Game tab
                mainView.goToTab(mainView.playGameTab);
            }
        }
    }

}
