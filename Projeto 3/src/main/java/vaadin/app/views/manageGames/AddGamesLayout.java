package vaadin.app.views.manageGames;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import rea.Manager;
import rea.ReaException;

/**
 * Horizontal panel with buttons for adding new games.
 */
public class AddGamesLayout extends HorizontalLayout {
    private Grid grid;
    private Manager manager;

    /**
     * Constructor
     * @param g grid for the panel
     * @throws ReaException exception
     */
    public AddGamesLayout(Grid g) throws ReaException {

        // Initializations
        this.manager = Manager.getInstance();
        this.grid = g;

        this.add(new Text("Add new games:"));

        // Style
        this.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        // Game Buttons
        for(String gameName : manager.getAvailableGames()){
            Button button = new Button(gameName);

            // Game Buttons Style
            button.addClassNames(LumoUtility.Margin.Horizontal.SMALL);
            button.setIcon(VaadinIcon.PLUS_CIRCLE.create());

            // On Click:
            // 1. Create new game instance
            button.addClickListener(e -> manager.createGameInstance(gameName) );

            // 2. Refresh grid after Add button click.
            button.addClickListener(e ->  manager.updateGrid(grid) );

            this.add(button);
        }
    }
}
