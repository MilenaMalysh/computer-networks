package processScheduler.logic;

import processScheduler.model.Channel;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Milena on 05.12.2015.
 */
public abstract class AbstractMode {
    protected TreeMap<Vertex, RouterModel> nodes;
    protected Graph graph;
    protected AbstractBuilder builder;
    protected int amount_of_packages;
    protected int amount_of_messages;
    protected int amount_of_sys_package;
    protected int summary_waiting_time;
    protected int system_time;

    public AbstractMode(TreeMap<Vertex, RouterModel> nodes, Graph graph, AbstractBuilder builder) {
        this.nodes = nodes;
        this.graph = graph;
        this.amount_of_packages = 0;
        this.amount_of_sys_package = 0;
        this.summary_waiting_time = 0;
        this.amount_of_messages = 0;
        this.builder = builder;
    }

    public void tick(){
    }

    public Message generate_message(){
       amount_of_messages++;
        return builder.build(amount_of_messages);
    }


    public abstract void sendMessage();

    public AbstractBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(AbstractBuilder builder) {
        this.builder = builder;
    }

    public void cancel(){
        graph.getVertexeses().forEach(v->{
            v.setRouteFrom(null);
            v.setRouteTo(null);
            v.getMessages().clear();
            v.getQueue().clear();
            v.setStatus(0);
        });
        graph.getEdges().forEach(Channel::cancel);
    }
}
