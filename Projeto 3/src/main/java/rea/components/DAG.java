package rea.components;

import java.util.*;

/**
 * Directed acyclic graph (DAG) where the nodes are places and arcs are passages.
 * Used in {@link GameMap}
 * @author Carla Henriques <code>up202201926@edu.fc.up.pt</code>
 * @author Rui Santos <code>up202109728@edu.fc.up.pt</code>
 */
public class DAG {

    /**
     * DAG main structure
     */
    protected Map<Place, List<Passage>> dag = new HashMap<>();

    /**
     * Constructor
     */
    public DAG() {
    }

    /**
     * Additional method.
     * Adds a new <b>vertex</b> in the dag. The vertices are of the {@link Place} class.
     * @param place a new {@link Place} (vertex) to this {@link GameMap} (dag).
     */
    public void addPlace(Place place) {
        if (!dag.containsKey(place)) {
            dag.put(place, new ArrayList<>());
        }
    }


    /**
     * Additional method.
     * Adds an <b>arc</b> in the dag. The arcs are of the class {@link Passage} class.
     * Each {@link Passage} object has a {@link Place}, directing to which this passage leads.
     * @param fromPlace {@link Place}, as a vertex to the dag
     * @param toPassage {@link Passage}, as an arc to the dag
     */
    public void addPassage(Place fromPlace, Passage toPassage) {
        if (!dag.containsKey(fromPlace)) {
            throw new IllegalArgumentException("DAG.addPassage: Place of origin not found in the game map.");
        }

        if (!dag.containsKey(toPassage.getPlace())) {
            throw new IllegalArgumentException("DAG.addPassage: Place of destiny not found in the game map.");
        }

        // Check if adding this passage would create a cycle
        if (isCyclic(fromPlace, toPassage.getPlace())) {
            throw new IllegalArgumentException("DAG.addPassage: Adding this passage would create a cycle.");
        }

        // Adds Passage to list of passages, with the Place as a key.
        dag.get(fromPlace).add(toPassage);
    }

    /**
     * Additional method. Auxiliary to addPassage.
     * Checks if there is a cycle from a Place to another Place, after adding a Passage between them.
     * @param from {@link Place} (vertex) of origin
     * @param to {@link Place} (vertex) of destiny
     * @return <code>true</code> if adds cycle, <code>false</code> if it doesn't
     */
    private boolean isCyclic(Place from, Place to) {
        // Perform a depth-first search (DFS) from 'to' to see if it reaches 'from'
        Set<Place> visited = new HashSet<>();
        visited.add(to);
        return isCyclicDFS(from, to, visited);
    }

    /**
     * Additional method. Auxiliary to isCyclic.
     * Depth-first search (DFS) from current Place to target Place
     * @param target target node
     * @param current current node
     * @param visited visited node
     * @return <code>true</code> if it detects a cycle, <code>false</code> if it doesn't
     */
    private boolean isCyclicDFS(Place target, Place current, Set<Place> visited) {
        if (current == target) {
            return true; // Cycle detected
        }

        for (Passage passage : dag.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(passage.getPlace())) {
                visited.add(passage.getPlace());
                if (isCyclicDFS(target, passage.getPlace(), visited)) {
                    return true;
                }
                visited.remove(passage.getPlace()); // Backtrack
            }
        }

        return false;
    }

    /**
     * Additional function.
     * @return all Places in a DAG.
     */
    public Set<Place> getPlaces (){
        return this.dag.keySet();
    }
}
