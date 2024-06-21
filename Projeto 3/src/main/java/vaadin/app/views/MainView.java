package vaadin.app.views;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import rea.Manager;
import rea.ReaException;
import rea.gameplay.games.CartoonAvatar;
import rea.gaming.GameInstance;
import vaadin.app.views.home.HomePanel;
import vaadin.app.views.manageGames.ManageGamesPanel;
import rea.components.Character;

/**
 * Main view for the Application.
 * User can access Manage Games area, and the area for playing a Game from here.
 */
@Route("")
@PageTitle("REA | Realm of Endless Adventures")
public class MainView extends Div {
    //REAService service;
    Manager manager;

    // Tabs
    TabSheet tabSheet;
    public Tab homeTab;
    public Tab manageGamesTab;
    Span gamesCounterBadge; // Badge for manageGamesTab
    public Tab playGameTab;

    // Tabs Contents
    HomePanel homePanel;
    ManageGamesPanel manageGamesPanel;

    GameInstance gameInstanceBeingPlayed;

    //TODO apenas criei esta personagem vazia para não dar erro
    Character characterBeingPlayed = new Character("Anonymous", CartoonAvatar.BUNNY);

    /**
     * Constructor
     * @throws ReaException exception
     */
    public MainView() throws ReaException {

        System.out.println("ACONTECEU REFRESH NA MAINVIEW " + this.hashCode());

        initializesManager();

        addClassName("main-view");

        // Style
        this.setSizeFull();

        tabSheet = createTabSheet();

        this.add( tabSheet );
    }

    /**
     * Initializes Manager instance
     */
    private void initializesManager() {
        try {
            manager = Manager.getInstance();
            manager.addGamesUpdateListener(e -> {
                this.getUI().get().access(() ->{
                    Notification.show("Added a new Game").addThemeVariants(NotificationVariant.LUMO_WARNING);
                }
                );
                manager.updateGrid(manageGamesPanel.grid);
                this.gamesCounterBadge.setText(String.valueOf(manager.getGameInstances().size()));
            });

        } catch (ReaException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates TabSheet that will provide access to the app areas by the user.
     * @return TabSheet
     * @throws ReaException exception
     */
    private TabSheet createTabSheet() throws ReaException {
        TabSheet tabSheet = new TabSheet();

        // Initialize all tab content panels
        homePanel = new HomePanel(this);
        manageGamesPanel = new ManageGamesPanel(this);

        // Prefix (Logo)
        Span logo = createLogo();
        tabSheet.setPrefixComponent(logo);

        // Home tab
        homeTab = new Tab(VaadinIcon.HOME.create(), new Span("Home"));
        tabSheet.add(homeTab, new Div(homePanel));

        // Manage Games tab
        // TODO verificar linha abaixo
        gamesCounterBadge = new Span(String.valueOf(manager.getGameInstances().size()));

        gamesCounterBadge.getElement().getThemeList().add("badge pill small contrast");
        gamesCounterBadge.getStyle().set("margin-inline-start", "var(--lumo-space-xs)");
        manageGamesTab = new Tab(VaadinIcon.COG.create(), new Span("Manage Games"), gamesCounterBadge);
        //Tab manageGameTab = new Tab(VaadinIcon.COG.create(), new Span("Manage Games"));
        tabSheet.add(manageGamesTab, new Div(manageGamesPanel));

        // Style
        tabSheet.setSizeFull();
        tabSheet.getStyle().set("margin", "0px");
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_SMALL);
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);

        return tabSheet;
    }

    /**
     * Changes the selected Tab on the TabSheet.
     * @param tab Tab to go to
     */
    public  void goToTab(Tab tab) {
        tabSheet.setSelectedTab(tab);

    }

    public void removePlayGameTab(){
        tabSheet.remove(2);
    }


    /**
     * Adds Tab and a Div with content to TabSheet.
     * @param tab Tab to be added to TabSheet
     * @param tabContent content that will be added in a Div
     */
    public void addTabToTabSheet(Tab tab, Div tabContent){
        tabSheet.add(tab, tabContent);
    }

    /**
     * Removes designated Tab from TabSheet.
     * @param tabIndex int index of Tab to be removed from TabSheet
     */
    public void removeTabFromTabSheet(int tabIndex){
        tabSheet.remove( tabIndex );
    }

    /**
     * Created the Logo to be displayed on the left side of the TabSheet.
     * @return Span with logo.
     */
    private Span createLogo() {
        Span logo = new Span("Realm of Endless Adventures");

        // Style
        logo.getStyle().set("font-stretch", "ultra-condensed");
        logo.getStyle().set("padding", "var(--lumo-space-m)");
        logo.getStyle().set("font-weight", "bold");
        logo.getStyle().set("color", "var(--lumo-tertiary-text-color)");

        return logo;
    }

    /**
     * Getter method for gameInstanceBeingPlayed
     * @return gameInstanceBeingPlayed
     */
    public GameInstance getGameInstanceBeingPlayed() {
        // TODO: remover depois de resolver o issue de tratar o caso gameInstance null
        if (gameInstanceBeingPlayed == null) {
            Notification.show("gameInstanceBeingPlayed == null");
            return manager.getGameInstances().getFirst();
        }
        else {
            return gameInstanceBeingPlayed;
        }
    }

    /**
     * Setter method to gameInstanceBeingPlayed.
     * @param gameInstance GameInstance to be set
     */
    public void setGameInstanceBeingPlayed(GameInstance gameInstance) {
        this.gameInstanceBeingPlayed = gameInstance;
    }

    /**
     * Getter method for characterBeingPlayed.
     * @return characterBeingPlayed
     */
    public Character getCharacterBeingPlayed() {
        return characterBeingPlayed;
    }

    /**
     * Setter method for characterBeingPlayed.
     * @param character Character to be set
     */
    public void setCharacterBeingPlayed(Character character) {
        this.characterBeingPlayed = character;
    }

}