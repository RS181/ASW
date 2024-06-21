package vaadin.app.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import vaadin.app.views.MainView;

/**
 * VerticalLayout for the home panel
 */
public class HomePanel extends VerticalLayout {
    private MainView mainView;


    /**
     * Contructor
     * @param main MainView
     */
    public HomePanel(MainView main) {
        addClassName("home-panel");

        mainView = main;

        add(getHomeContent());

    }

    /**
     * Gets content for the home panel
     * @return VerticalLayout with content
     */
    private VerticalLayout getHomeContent() {
        VerticalLayout homeContent = new VerticalLayout();

        homeContent.add (
                new Span("In this app, players can start a new game chosen from assorted possible games, or join an existing game. "),
                new Span("In the game you can explore a virtual world, interact with other players, and cooperativaly finish the quests!"),
                new HorizontalLayout(
                        new Span("Start playing a new adventure: "),
                        new Button("Go to Manage Games", e-> mainView.goToTab(mainView.manageGamesTab)) )
        );

        return homeContent;
    }
}
