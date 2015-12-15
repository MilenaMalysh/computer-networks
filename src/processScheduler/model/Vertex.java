package processScheduler.model;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import processScheduler.logic.Message;
import processScheduler.logic.RouterModel;
import processScheduler.logic.Package;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.TreeMap;

public class Vertex implements Comparable<Vertex> {
    private int id;
    transient private TreeMap<Vertex, Channel> neighbours;
    private RouterModel router;
    private Deque<Package> queue;
    private Deque<Message> messages;
    private IntegerProperty status;
    private int configurationCount;
    public TreeMap<Vertex, Channel> getNeighbours() {
        return neighbours;
    }

    public Vertex(int id){
        this.id = id;
        neighbours = new TreeMap<>();
        this.queue = new ArrayDeque<>();
        this.messages = new ArrayDeque<>();
        status = new SimpleIntegerProperty(0);
        configurationCount = 0;
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


    public Deque<Message> getMessages() {
        return messages;
    }

    public void setMessages(Deque<Message> messages) {
        this.messages = messages;
    }

    public void setStatus(int status) {
        this.status.set(status);
    }

    public int getStatus() {
        return status.get();
    }

    public IntegerProperty statusProperty() {
        return status;
    }

    public void configure(){
        configurationCount++;
    }

    public void deconfigure(){
        configurationCount--;
    }

    public boolean isConfigured(){
        return configurationCount>0;
    }
}
