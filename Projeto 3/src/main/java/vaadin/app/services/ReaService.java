package vaadin.app.services;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;
import rea.Manager;
import rea.ReaException;
import rea.components.Avatar;
import rea.components.Character;
import rea.components.Visual;
import rea.gameplay.GameplayFactory;
import rea.gameplay.games.CartoonAvatar;
import rea.gaming.GameInstance;

@Service
public class ReaService implements Serializable {
    public static final String REA_GAMES = "rea.gameplay";
    static GameInstance gameInstanceBeingPlayed;
    static Character characterBeingPlayed;
    //Character characterBeingPlayed = createdEmptyCharacter();
    GameplayFactory defaultGameplayFactory;
    static Manager pool;
    Set<String> availableGames;

    // <-- Mock data !!!
    Character c1 = new Character("character 1", CartoonAvatar.BUNNY);
    Character c2 = new Character("character 2", CartoonAvatar.LAMB);
    private Character emptyCharacter = createdEmptyCharacter();
    //  Mock data -->

    public ReaService() throws ReaException {
        //this.gamesCount = gamesCount;

        pool = Manager.getInstance();
        defaultGameplayFactory = new GameplayFactory();
        pool.setGameplayFactory(defaultGameplayFactory);

        //Set<String> availableGames =  defaultGameplayFactory.getAvailableGameplays();
        availableGames = pool.getAvailableGames();

        // <-- Mock data !!!
        // TODO: remover esses, e deixar o usuario criar os proprios jogos

        GameInstance gi2 = pool.createGameInstance("Treasure Hunt");
        GameInstance gi3 = pool.createGameInstance("Easter Egg Race");

        GameInstance gi1 = pool.createGameInstance("Easter Egg Race");
        gi1.addPlayer(c1);

        GameInstance gi4 = pool.createGameInstance("Treasure Hunt");
        gi4.addPlayer(c1);
        //gi4.addPlayer(c2);
        //gi4.startPlayingGame();
        //  Mock data -->
    }

    public String greet(String name) {
        if (name == null || name.isEmpty()) {
            return "Hello anonymous user";
        } else {
            return "Hello " + name;
        }
    }

    public int getGamesCount(){
        return pool.getGameInstances().size();
    }

    public Set<String> getAvailableGames (){
        return availableGames;
    }

    public Boolean createGameInstance (String gameName){

        GameInstance gameInstance = pool.createGameInstance(gameName);

        return (gameInstance != null);
    }

    public List<GameInstance> getGameInstances (){
        return pool.getGameInstances();
    }

    public Boolean deleteGameInstance (GameInstance gameInstance){

        if (gameInstance == gameInstanceBeingPlayed){
            setGameInstanceBeingPlayed(null);
            setCharacterBeingPlayed(null);
        }

        pool.deleteGameInstance(gameInstance);

        return (! pool.getGameInstances().contains(gameInstance));
    }

    public Set<Avatar> getAvatarList(GameInstance gameInstance) {
        return gameInstance.getAvatars();
    }


    public void setGameInstanceBeingPlayed(GameInstance gi) {
        this.gameInstanceBeingPlayed = gi;
        //setCharacterBeingPlayed(null);
    }

    public GameInstance getGameInstanceBeingPlayed() {
        //return gameInstanceBeingPlayed;

        // TODO: remover depois de resolver o issue de tratar o caso gameInstance null
        if (gameInstanceBeingPlayed == null)
            return getGameInstances().getFirst();
        else
            return gameInstanceBeingPlayed;
    }

    public Character getCharacterBeingPlayed() {
        if (characterBeingPlayed == null)
           return getEmptyCharacter();

        UI ui = new UI();
        return characterBeingPlayed;
    }

    public void setCharacterBeingPlayed(Character c) {
        this.characterBeingPlayed = c;
    }

    public Character getEmptyCharacter() {
        return emptyCharacter;
    }

    public Boolean isCharacterAnonymous () {
        return getCharacterBeingPlayed().getVisual() != getEmptyCharacter().getVisual();
    }
    public Character createdEmptyCharacter () {
        return new Character("Anonymous", new Avatar() {
            @Override
            public String getAvatarName() {
                return "user placeholder";
            }

            @Override
            public Visual getAvatarVisual() {
                return new Visual("images/user_placeholder.png", 360, 360);
            }
        });
    }
}
