package processScheduler.logic.io.network;

import processScheduler.model.Vertex;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Milena on 01.12.2015.
 */
public class RouterTable {


    private TreeMap<Vertex,Vertex> routing;

    public RouterTable() {
        this.routing = new TreeMap<>();
    }


    public TreeMap<Vertex, Vertex> getRouting() {
        return routing;
    }

    public void setRouting(TreeMap<Vertex, Vertex> routing) {
        this.routing = routing;
    }


}
