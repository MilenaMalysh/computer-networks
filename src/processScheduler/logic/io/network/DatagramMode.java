package processScheduler.logic.io.network;

import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Milena on 04.12.2015.
 */
public class DatagramMode extends AbstractMode{
    public DatagramMode(Random random, TreeMap<Vertex, RouterModel> nodes, Graph graph) {
        super(random,nodes,graph);
    }

}
