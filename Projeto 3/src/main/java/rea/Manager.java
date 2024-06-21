package rea;

import com.vaadin.flow.component.grid.Grid;
import org.springframework.stereotype.Service;
import rea.events.EventBroadcast;
import rea.events.GamesUpdateEvent;
import rea.events.UpdateListener;
import rea.gameplay.AbstractGameplayFactory;
import rea.gameplay.Gameplay;
import rea.gaming.GameInstance;
import rea.gaming.GameStage;

import java.io.Serializable;
import java.util.*;

/**
 * A pool of game instances. It is responsible for creating and managing game instances.
 * It can also be used to get the names of the available games, and the available game instances.
 * A listener can be added to be notified of changes in the list of games about to start.
 * implNote - follows the <b>Singleton</b> design pattern.
 */
@Service
public class Manager implements Serializable {

    /**
     * Default time in milliseconds to keep a game instance after it has ended.
     */
    //static final long KEEP_AFTER_END = 300000L;
    static final long KEEP_AFTER_END = 30000L; // REMOVER

    private static volatile Manager instance;
    private static long keepAfterEnd = KEEP_AFTER_END;

    private static volatile AbstractGameplayFactory gameplayFactory;
    private static volatile List<GameInstance> gameInstancesPool;
    private static volatile EventBroadcast<GamesUpdateEvent> gamesUpdate;

    //private static volatile Set<UpdateListener<GamesUpdateEvent>> gamesListeners = new HashSet<>();

    /**
     * Constructor
     */
    private Manager(){
        gamesUpdate = new EventBroadcast<>();
    }

    /**
     * Get the instance of the game instance pool.
     * @return the instance of the game instance pool.
     * @throws ReaException if the instance cannot be created.
     */
    public static Manager getInstance()
            throws ReaException{
        try {

            if(instance == null) {
                synchronized (Manager.class) {

                    if (instance == null) {
                        instance = new Manager();
                    }
                }
            }
            return instance;

        }
        catch (Exception e) {
            throw new ReaException(e.getMessage());
        }
    }

    /**
     * Set the gameplay factory. Use only for testing.
     * @param gameplayFactory the gameplay factory to set.
     */
    public void setGameplayFactory(AbstractGameplayFactory gameplayFactory){

        // ERA SUPOSTO TER UM gameplayFactory default
       Manager.gameplayFactory = gameplayFactory;
    }

    /**
     * Get the gameplay factory. Use only for testing.
     * @return the gameplay factory.
     */
    AbstractGameplayFactory getGameplayFactory(){
        return gameplayFactory;
    }

    /**
     * Reset the pool to its initial state.
     * Clears the list of games and sets the gameplay factory to a new instance.
     * Use only for testing.
     * @throws ReaException if the gameplay factory cannot be created.
     */
    protected static synchronized void reset()
            throws ReaException{

        // No enunciado: "Não esquecer que este método deve também limpar o ficheiro de serialização."
        try {
            instance = null;

            // TODO: limpar ficheiro de serialização

            gameplayFactory = null;
            gameInstancesPool = new ArrayList<>();
            gamesUpdate = new EventBroadcast<>();
        }
        catch (Exception e){
            throw new ReaException(e.getMessage());
        }
    }

    /**
     * Add a listener to the games update event.
     * @param listener of events
     */
    public void addGamesUpdateListener(UpdateListener<GamesUpdateEvent> listener){
        // TODO VERIFICAR ISTO
        try{
            if(! gamesUpdate.getListeners().contains(listener))
                gamesUpdate.addListener(listener);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        //if(! this.gamesListeners.contains(listener))
        //    this.gamesListeners.add(listener);
    }

    /**
     * Get the names available games.
     * These are the names of the gameplays available in the gameplay factory.
     * @return set with available games' names.
     */
    public Set<String> getAvailableGames(){
        return gameplayFactory.getAvailableGameplays();
    }

    /**
     * Create a game instance with the given name and add it to the pool.
     * The {@link #recycleGameInstances()} method is called to remove old game instances.
     * Changes in the created game are broadcasted to all {@link GamesUpdateEvent} listeners.
     * @param gameName the name of the game.
     * @return the game instance.
     */
    public GameInstance createGameInstance(String gameName) {
        Gameplay g = gameplayFactory.getGameplay(gameName);
        GameInstance newGameInstance = new GameInstance(g);

        if (gameInstancesPool == null)
            gameInstancesPool = new ArrayList<>();

        gameInstancesPool.add(newGameInstance);

        // calls recycleGameInstances() to remove old game instances
        recycleGameInstances();

        // Changes in the created game are broadcasted to all listeners
        broadcastGamesUpdate();

        return newGameInstance;
    }

    /**
     * Delete a game instance from the pool, if it can be deleted.
     * Changes in the list of games about to start are broadcasted to all {@link GamesUpdateEvent} listeners.
     * @param gameInstance the game instance to delete.
     * @see GameInstance#canDelete()
     */
    public static void deleteGameInstance(GameInstance gameInstance) {
        if (gameInstance != null) {
            if (gameInstance.canDelete()){
                gameInstancesPool.remove(gameInstance);
            }

            // it's supposed to broadcast for the listeners in GamesUpdateEvent
            Manager.broadcastGamesUpdate();
        }
    }

    /**
     * Get all the game instances.
     * @return the game instances.
     */
    public List<GameInstance> getGameInstances() {
        return gameInstancesPool;
    }

    /**
     * Broadcast an update in the list of games about to start.
     */
    public static void broadcastGamesUpdate(){
        List<GameInstance> gamesAboutToStart = Manager.getGamesInstancesAboutToStart();
        GamesUpdateEvent g = new GamesUpdateEvent(gamesAboutToStart);
        Manager.gamesUpdate.broadcast(g);
    }

    /**
     * Get the games instances about to start.
     * @return the instances of games about to start.
     */
    public static List<GameInstance> getGamesInstancesAboutToStart(){
        List<GameInstance> gamesAboutToStart = new ArrayList<>();
        for (GameInstance g : Manager.gameInstancesPool ){
            if (g.canStart())
                gamesAboutToStart.add(g);
        }
        return gamesAboutToStart;
    }

    /**
     * Get the time in milliseconds to keep a game instance after it has ended.
     * By default, it is 5 minutes.
     * @return the time to keep a game instance after it has ended.
     */
    public static long getKeepAfterEnd(){
        return keepAfterEnd;
    }

    /**
     * Set the time in milliseconds to keep a game instance after it has ended.
     * By default, it is 5 minutes.
     * @param keepAfterEnd the time to keep a game instance after it has ended.
     */
    public static void setKeepAfterEnd(long keepAfterEnd){
        Manager.keepAfterEnd = keepAfterEnd;
    }

    /**
     * Remove game instances that have ended more than keepAfterEnd milliseconds ago.
     */
    public static void recycleGameInstances() {
        long timeNow = new Date().getTime();

        List<GameInstance> gameInstanceList = Manager.gameInstancesPool;

        // Fazer teste com a gameInstancesPool sem ser static volatile
        // para verificar se dá o erro ConcurrentException

        for (Iterator<GameInstance> iterator = gameInstanceList.iterator(); iterator.hasNext();){
            GameInstance gameInstance = iterator.next();

            if (gameInstance.getCurrentStage().equals(GameStage.ENDED)){

                // Get time game has ended
                Date dateEnded = gameInstance.getPlayingUntil();

                if (dateEnded != null){
                    long timeEnded = dateEnded.getTime();
                    long keepAfter = Manager.getKeepAfterEnd();

                    if (timeNow >= (timeEnded + keepAfter)){
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * Updates data from Grid
     */
    public void updateGrid(Grid grid) {
        List<GameInstance> gameInstanceList = instance.getGameInstances();

        if (gameInstanceList == null)
            gameInstanceList = new ArrayList<>();

        grid.setItems(instance.getGameInstances());

        //grid.getDataProvider().refreshAll(); //Refreshes data from Grid
    }
}
