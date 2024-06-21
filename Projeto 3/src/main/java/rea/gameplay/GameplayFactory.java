package rea.gameplay;

import rea.ReaException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * <p>A factory for creating {@link Gameplay} instances.
 * This factory method creates instances of {@link Gameplay} using reflection.
 * and selects the gameplay to use based on the name of the game.
 * The available games are collected from a package, either {@link #GAMEPLAY_PACKAGE}
 * if the default constructor is used, or package name passed as parameter
 * to the constructor.</p>
 * implNote - a concrete participant of the <b>Factory method</b> design pattern.
 */
public class GameplayFactory
        implements AbstractGameplayFactory {

    /**
     * The class loader to find and load gameplays in the {@link #GAMEPLAY_PACKAGE} package.
     * implNote - Use <code>GameplayFactory.class.getClassLoader()</code>
     * (instead of <code>ClassLoader.getSystemClassLoader()</code>) to avoid
     * issues when calling this class from client side code in a web application.
     */
    static final ClassLoader LOADER = GameplayFactory.class.getClassLoader();

    /**
     * Default package name to lookup gameplay: the games subpackage of the gameplay package.
     * @see "Constant Field Values" (perguntar ao professor como gerar o link!!!)
     */
    static final String GAMEPLAY_PACKAGE = "rea.gameplay.games";

    private String gameplayPackage;

    /**
     * Create a factory for gameplays.
     * @throws ReaException if the package does not exist,
     * or IO exception related to the package directory was raised.
     */
    public GameplayFactory()
            throws ReaException {
        try {
            this.gameplayPackage = GAMEPLAY_PACKAGE;
        }
        catch (Exception e){
            throw new ReaException(e.getMessage());
        }
    }

    /**
     * Create a factory for gameplays in a given package.
     * @param gameplayPackage the package to look for gameplays.
     * @throws ReaException if the package does not exist,
     * or IO exception related to the package directory was raised.
     */
    public GameplayFactory(String gameplayPackage)
            throws ReaException {
        try {
            this.gameplayPackage = gameplayPackage;
        }
        catch (Exception e){
            throw new ReaException(e.getMessage());
        }
    }

    /**
     * Get the available games in this factory.
     * Specified by: getAvailableGameplays in interface AbstractGameplayFactory
     * @return the available games as a set.
     */
    public Set<String> getAvailableGameplays() {
        try {
            Map<String,Gameplay> gameplayMap = this.collectGameplayInPackage(this.gameplayPackage);
            return gameplayMap.keySet();
        }
        catch (ReaException e) {
            return  null;
        }
    }

    /**
     * Get the gameplay for a game with a given name.
     * Specified by: getGameplay in interface AbstractGameplayFactory
     * @param name of the game.
     * @return the gameplay for the given game, or <code>null</code> if the game does not exist.
     */
    public Gameplay getGameplay(String name) {
        try {
            Map<String,Gameplay> gameplayMap = this.collectGameplayInPackage(this.gameplayPackage);
            return gameplayMap.get(name);
        }
        catch (ReaException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * <p>Get an instance of a class with the given name.
     * The class is expected to be a subclass of {@link Gameplay} and have a default
     * constructor (without arguments). Classes that do not meet these criteria and
     * exceptions during instantiation will be ignored and null will be returned</p>
     * @param className the name of the class to get an instance of.
     * @return an instance of the class or <code>null</code>.
     */
    Gameplay getGameplayInstance(String className) {
        try {
            var clazz = LOADER.loadClass(className);
            return (Gameplay) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {

            return  null;
        }
    }

    /**
     * <p>Collect all gameplay in a package.
     * All classes in the package implementing {@link Gameplay} are instanced and returned as a <code>Map</code>.
     * The map uses the gameplay's name as the key Gameplay.getName() and an instance of the gameplay as the value.</p>
     * implNote - use streams to collect the gameplay
     * @param gameplayPackage with the gameplay to collect.
     * @return a map with the gameplay, indexed by their name.
     * @throws ReaException if the package does not exist, or IO exception related to the package directory was raised.
     */
    Map<String,Gameplay> collectGameplayInPackage(String gameplayPackage)
            throws ReaException {
        Map<String, Gameplay> gameplayInPackage = new HashMap<>();

        try {
            String packagePath = gameplayPackage.replace(".","/");

            URL resource = LOADER.getResource(packagePath);

            Stream<Path> f = Files.list(Path.of(resource.toURI()));

            for (Path p : f.toList()){
                String className = p.getFileName().toString().replace(".class", "");
                String binaryName = gameplayPackage + "." + className;

                // IntelliJ gets the test classes, this removes the test classes.
                binaryName = this.cleanTestFromString(binaryName);

                Gameplay g =  getGameplayInstance(binaryName);

                if (g != null){
                    gameplayInPackage.put(g.getName(),g);
                }
            }
            return gameplayInPackage;
        }
        catch (Exception e){
            throw new ReaException(e.getMessage());
        }

    }

    private String cleanTestFromString (String s){
        String resultString;

        if (s.endsWith("Test")) {
            resultString = s.substring(0, s.length() - 4);
        } else {
            resultString = s;
        }

        return resultString;
    }



    /**
     * Get the class name from a path.
     * The path is expected to be in the form of a package path.
     * The class name is the last part of the path without the extension.
     * @param path the path to get the class name from.
     * @return the class name.
     * @see rea.gameplay
     */
    String getClassName(Path path){
        String start = "rea.gameplay.";
        String fileName = path.getFileName().toString().replace(".class","");

        for (Path s : path){
            if (s.toString().equals("gameplay"))
                return start + "games." +  fileName;
        }
        return start + fileName;
    }
}
