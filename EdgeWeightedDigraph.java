/*************************************************************************
 *  Compilation:  javac EdgeWeightedDigraph.java
 *  Execution:    java EdgeWeightedDigraph V E
 *  Dependencies: Bag.java DirectedEdge.java
 *
 *  An edge-weighted digraph, implemented using adjacency lists.
 *
 *************************************************************************/

/**
 *  The <tt>EdgeWeightedDigraph</tt> class represents an directed graph of vertices
 *  named 0 through V-1, where each edge has a real-valued weight.
 *  It supports the following operations: add an edge to the graph,
 *  iterate over all of edges leaving a vertex.
 *  Parallel edges and self-loops are permitted.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */

import java.util.*;
import java.util.Iterator;


public class EdgeWeightedDigraph {
    private final int V;
    private int E;
    public Bag<DirectedEdge>[] adj;

    private Bag<DirectedEdge>[] down;
    private boolean [] nodeStatus;
    
    /**
     * Create an empty edge-weighted digraph with V vertices.
     */
    @SuppressWarnings("unchecked")
    public EdgeWeightedDigraph(int V) {
        if (V < 0) throw new RuntimeException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        down = new Bag[V];
        adj = (Bag<DirectedEdge>[]) new Bag[V];
        for (int v = 0; v < V; v++)
            adj[v] = new Bag<DirectedEdge>();

        //make boolean array for vertices, if index is false, means node is down. 
        //Also place empty Bags for each index of the down array
        nodeStatus = new boolean [V];
        for(int i = 0; i<V; i++){
            nodeStatus[i] = true; down[i] = new Bag<DirectedEdge>();}
    }

   /**
     * Create a edge-weighted digraph with V vertices and E edges.
     */
    public EdgeWeightedDigraph(int V, int E) {
        this(V);
        if (E < 0) throw new RuntimeException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = (int) (Math.random() * V);
            int w = (int) (Math.random() * V);
            double weight = Math.round(100 * Math.random()) / 100.0;
            DirectedEdge e = new DirectedEdge(v, w, weight);
            addEdge(e);
        }
    }
    
   /**
     * Return the number of vertices in this digraph.
     */
    public int V() {
        return V;
    }

   /**
     * Return the number of edges in this digraph.
     */
    public int E() {
        return E;
    }

    public void upDownStatus(){

        System.out.println("The following nodes are connected in the network:");

        for(int i = 0; i < V; i++){
            if(nodeStatus[i])
                System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("The following nodes down in the network are:");

        for(int i = 0; i < V; i++){
            if(nodeStatus[i] == false)
                System.out.print(i + " ");
        }

        System.out.println();
    }


   /**
     * Add the edge e to this digraph.
     */
    public boolean addEdge(DirectedEdge e) {
        int v = e.from();

        if(nodeStatus[v] && nodeStatus[e.to()]){
        adj[v].add(e);
        E++;
        return true;
        }

        return false;
    }

    public void allPaths(int start, int end, double limit){

        double totalWeight = 0;

        ArrayList<Integer> pathTracker = new ArrayList<Integer>();

        String starter = new String();
        starter += start;
        StringBuilder currPath = new StringBuilder(starter);

        for(DirectedEdge e : adj(start)){

            currPath.append(e.to());
            totalWeight = e.weight();
            pathTracker.add(new Integer(e.to()));
            allPathsRecursive(e.to() ,end, limit, totalWeight, currPath, pathTracker);
            currPath.deleteCharAt(currPath.length() - 1);
            pathTracker.remove(0);
        }

    }

    public void allPathsRecursive(int currVertex,int endVertex, double limit, double currWeight, StringBuilder currPath, ArrayList<Integer> pathTracker){

        double totalWeight;

        if(currWeight > limit)
            return;

        if(currVertex == endVertex){
           System.out.println(currPath.toString() + " Total Weight: " + currWeight); 
           return;
        }

        for(DirectedEdge e : adj(currVertex)){

            boolean notInPath = true;

            for(int i = 0; i<pathTracker.size(); i++){
                if(pathTracker.get(i).equals(e.to()))
                    notInPath = false;
            }

            if(notInPath){
                totalWeight = currWeight + e.weight();
                StringBuilder temper = new StringBuilder(currPath.append(e.to()));
                temper.append("-->");
                pathTracker.add(new Integer(e.to()));
                allPathsRecursive(e.to(), endVertex, limit, totalWeight, temper, pathTracker);
                pathTracker.remove(pathTracker.size()-1);
                currPath.deleteCharAt(currPath.length()-1);
            }

        }

    }

    public void changeEdge(int start, int end, double weight){

        DirectedEdge startEnd, endStart;
        DirectedEdge temp = null;

        //If one of the vertices is down, do not allow any edge manipulation
        if(nodeStatus[start] == false || nodeStatus[end] == false){
            System.out.println("One or both of the vertices you wish to change is down.");
            System.out.println("Operation cannot be done.");
            System.out.println();
            return;
        }

        //If no weight or less than 0, remove edge
        if(weight <= 0){

            Bag<DirectedEdge> newStart = new Bag<DirectedEdge>();
            Bag<DirectedEdge> newEnd = new Bag<DirectedEdge>();

            for(DirectedEdge e : adj(start)){
                if(e.to() == end)
                    continue;
                else
                    newStart.add(e);
            }
            adj[start] = newStart;

            for(DirectedEdge e : adj(end)){
                if(e.to() == start)
                    continue;
                else
                    newEnd.add(e);
            }
            adj[end] = newEnd;
            return;
        }

        //Normal case, either change the edge specified if it exsists, or create new edge with specified weight
        //if edge does not exsist. 
        for(DirectedEdge e : adj(start)){
            if(e.to() == end){
                temp = e; break;}
        }

        if(temp == null){
            startEnd = new DirectedEdge(start, end, weight); addEdge(startEnd);
            endStart = new DirectedEdge(end, start, weight); addEdge(endStart);
            return;
        }

        startEnd = temp;
        temp = null;

        for(DirectedEdge e : adj(end)) {
            if(e.to() == start){
                temp = e; break;}
        }
        if(temp == null)
            return;

        endStart = temp;

        if (startEnd.weight() == 0){
            startEnd.changeWeight(weight);
            endStart.changeWeight(weight);
            addEdge(startEnd);
            addEdge(endStart);
        }
        else{
            startEnd.changeWeight(weight);
            endStart.changeWeight(weight);
        }
    }

    //takes removes each edge going to or from a specified vertex.
    //Saves these edges so that they may be brought back up once the vertex restored. 
    public void takeDownVertex(int s) {

        Bag<DirectedEdge> [] newAdj = (Bag<DirectedEdge>[]) new Bag[V];  //created empty Bag array

        for(int i = 0; i<V; i++)
            newAdj[i] = new Bag<DirectedEdge>();

        //for each edge in the graph that doesn't correspond to s, add to new array which will represent the updated graph
        //if edge does correspond to s, place that edge in down at respective indices, so that they be restored later.
        for(DirectedEdge e : edges()){
            if(e.to() == s || e.from() == s){
                Bag<DirectedEdge> temp = down[e.from()];
                temp.add(e);
                down[e.from()] = temp;
                E--;
            }
            else{
                Bag<DirectedEdge> temp = newAdj[e.from()];
                temp.add(e);
                newAdj[e.from()] = temp;
            }
        }
        nodeStatus [s] = false;
        adj = newAdj;
    }

    public void restoreVertex(int s){

        nodeStatus[s] = true;

        Bag<DirectedEdge> [] newDown = (Bag<DirectedEdge> []) new Bag[V];

        for(int i = 0; i<V; i++)
            newDown[i] = new Bag<DirectedEdge>();

        for(int i = 0; i<V;i++){
            Bag<DirectedEdge> temp = down[i];
            Iterator<DirectedEdge> it = temp.iterator();

            while(it.hasNext()){

                DirectedEdge e = it.next();

                if(e.to() == s || e.from() == s){

                   if(addEdge(e) == false){
                        Bag<DirectedEdge> temper = newDown[i];
                        temper.add(e);
                        newDown[i] = temper;
                   }
                }
                else{
                    Bag<DirectedEdge> temper = newDown[i];
                    temper.add(e);
                    newDown[i] = temper;
                }
            }
        }

        down = newDown;
    }


   /**
     * Return the edges leaving vertex v as an Iterable.
     * To iterate over the edges leaving vertex v, use foreach notation:
     * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
     */
    public Iterable<DirectedEdge> adj(int v) {
        return adj[v];
    }

   /**
     * Return all edges in this graph as an Iterable.
     * To iterate over the edges, use foreach notation:
     * <tt>for (DirectedEdge e : graph.edges())</tt>.
     */
    public Iterable<DirectedEdge> edges() {
        Bag<DirectedEdge> list = new Bag<DirectedEdge>();
        for (int v = 0; v < V; v++) {
            for (DirectedEdge e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    } 

   /**
     * Return number of edges leaving v.
     */
    public int outdegree(int v) {
        return adj[v].size();
    }

   /**
     * Return a string representation of this graph.
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (DirectedEdge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
}