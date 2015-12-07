package processScheduler.logic;

import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Random;
import java.util.TreeMap;

public class DatagramMode extends AbstractMode{
    public DatagramMode(TreeMap<Vertex, RouterModel> nodes, Graph graph,AbstractBuilder builder) {
        super(nodes,graph, builder);
    }

    @Override
    public void sendMessage() {

    }

}
