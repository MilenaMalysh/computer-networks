package processScheduler.model;


import processScheduler.logic.io.network.*;
import processScheduler.logic.io.network.Package;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeMap;

public class Vertex implements Comparable<Vertex> {
    private int id;
    transient private TreeMap<Vertex, Channel> neighbours;
    transient private Vertex routeTo;
    transient private Vertex routeFrom;
    private RouterModel router;
    private Deque<Package> queue;
    private Deque<Message> messages;
    public TreeMap<Vertex, Channel> getNeighbours() {
        return neighbours;
    }

    public Vertex(int id){
        this.id = id;
        neighbours = new TreeMap<>();
        this.queue = new ArrayDeque<>();
        this.messages = new ArrayDeque<>();
    }

    public void registerEdge(Channel e){
        if(e.getSource().equals(this)){
            neighbours.put(e.getTarget(), e);
        }else if(e.getTarget().equals(this)){
            neighbours.put(e.getSource(), e);
        }
    }

    public int getId() {
        return id;
    }

    public Channel findPath(Vertex vertex){
        return neighbours.get(vertex);
    }

    public boolean hasPath(Vertex vertex){
        return neighbours.containsKey(vertex);
    }

    public Deque<Package> getQueue() {
        return queue;
    }

    public boolean isQueueEmpty(){
        return queue.isEmpty();
    }


    @Override
    public int compareTo(Vertex o) {
        return Integer.compare(getId(), o.getId());
    }

    @Override
    public String toString() {
        return String.format("Vertex: id=%d", id);
    }

    public Vertex getRouteTo() {
        return routeTo;
    }

    public void setRouteTo(Vertex routeTo) {
        this.routeTo = routeTo;
    }

    public Vertex getRouteFrom() {
        return routeFrom;
    }

    public void setRouteFrom(Vertex routeFrom) {
        this.routeFrom = routeFrom;
    }

    public Deque<Message> getMessages() {
        return messages;
    }

    public void setMessages(Deque<Message> messages) {
        this.messages = messages;
    }
}
