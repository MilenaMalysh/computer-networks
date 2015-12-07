package processScheduler.model;

import com.google.common.collect.TreeBasedTable;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Graph implements Serializable {
    transient EventBus eventBus = new EventBus();
    TreeMap<Integer, Vertex> vertexes;
    TreeBasedTable<Vertex, Vertex, Channel> edges;

    public Graph() {
        //bus for update listeners
        eventBus = new EventBus(new SubscriberExceptionHandler() {
            @Override
            public void handleException(Throwable exception, SubscriberExceptionContext context) {
                exception.printStackTrace();
            }
        });
        vertexes = new TreeMap<>();
        edges = TreeBasedTable.create();
    }

    public Vertex findVertex(int id) {
        return vertexes.get(id);
    }

    public Channel findChannel(Vertex source, Vertex target) {
        return edges.get(source, target)==null?edges.get(target, source):edges.get(source, target);
    }

    public Collection<Channel> connectedEdges(Vertex v){
        ArrayList<Channel> connected = new ArrayList<>(edges.row(v).values());
        connected.addAll(edges.column(v).values());
        return connected;
    }
    public boolean addVertex(Vertex v) {
        if (!vertexes.containsKey(v.getId())) {
            vertexes.put(v.getId(), v);
            eventBus.post(new GraphEvent.Vertex(v, GraphEvent.ADDED));
            return true;
        } else return false;
    }

    public boolean addEdge(Channel e) {
        int source = e.getSource().getId();
        int target = e.getTarget().getId();
        if(source>target){
            Vertex temp = e.getSource();
            e.setSource(e.getTarget());
            e.setTarget(temp);
        }
        boolean alreadyAdded = edges.contains(e.getSource(), e.getTarget());
        if(alreadyAdded){
            removeEdge(e);
        }
        edges.put(e.getSource(), e.getTarget(), e);
        eventBus.post(new GraphEvent.Edge(e, GraphEvent.ADDED));
        return !alreadyAdded;
    }

    public Channel generateEdge(int source, int target, int weight, Class<? extends Channel> edgeClass) {
        Channel newEdge = null;
        try {
            newEdge = edgeClass.getConstructor(Vertex.class, Vertex.class, int.class)
                    .newInstance(findVertex(source), findVertex(target), weight);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return newEdge;
    }

    public boolean removeVertex(Vertex v) {
        Object[] tempEdges = edges.values().toArray();
        for (int i = 0; i < tempEdges.length; i++) {
            Channel e = (Channel) tempEdges[i];
            if (e.getSource().getId() == v.getId() || e.getTarget().getId() == v.getId()) {
                eventBus.post(new GraphEvent.Edge(e, GraphEvent.REMOVED));
                removeEdge(e);
            }
        }
        eventBus.post(new GraphEvent.Vertex(v, GraphEvent.REMOVED));
        return vertexes.remove(v.getId())!=null;
    }

    public boolean removeEdge(Channel e) {
        eventBus.post(new GraphEvent.Edge(e, GraphEvent.REMOVED));
        return edges.remove(e.getSource(), e.getTarget())!=null;
    }

    public void registerListener(Object listener) {
        eventBus.register(listener);
    }

    public Collection<Vertex> getVertexeses(){
        return vertexes.values();
    }

    public Collection<Integer> getVertexesesId(){
        return vertexes.keySet();
    }

    public Collection<Channel> getEdges(){
        return edges.values();
    }

    public TreeMap<Integer, Vertex> getVertexes() {
        return vertexes;
    }
}