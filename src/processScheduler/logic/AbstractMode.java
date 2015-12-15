package processScheduler.logic;

import com.google.common.eventbus.EventBus;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import processScheduler.model.*;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Milena on 05.12.2015.
 */
public abstract class AbstractMode {
    protected TreeMap<Vertex, RouterModel> nodes;
    protected Graph graph;
    protected AbstractBuilder builder;
    protected IntegerProperty amount_of_packages;
    protected IntegerProperty amount_of_messages;
    protected IntegerProperty amount_of_sys_package;
    protected EventBus deliveryBus;

    public AbstractMode(Graph graph, AbstractBuilder builder) {
        this.graph = graph;
        this.amount_of_messages = new SimpleIntegerProperty(0);
        this.amount_of_packages = new SimpleIntegerProperty(0);
        this.amount_of_sys_package = new SimpleIntegerProperty(0);
        this.builder = builder;
        nodes = new TreeMap<>();
        update_configuration();
    }

    public void tick() {
        graph.getEdges().stream().forEach(e -> {
            if (e.update()) {
                if (e instanceof DuplexChannel) {
                    deliverResult(((DuplexChannel) e).getSourceDelivered());
                    deliverResult(((DuplexChannel) e).getTargetDelivered());
                } else {
                    deliverResult(((HalfDuplexChannel) e).getDelivered());
                }
            }
        });

        for (Map.Entry<Integer, Vertex> i : graph.getVertexes().entrySet()) {
            if (!i.getValue().isQueueEmpty()) {
                processDeliveredPackage(i.getValue());
            }
        }
    }

    public Message generate_message() {
        amount_of_messages.setValue(amount_of_messages.get() + 1);
        Message message = builder.build(amount_of_messages.get());
        return message;
    }


    public abstract Message sendMessage();

    public AbstractBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(AbstractBuilder builder) {
        this.builder = builder;
    }

    public void cancel() {
        graph.getVertexeses().forEach(v -> {
            v.getMessages().clear();
            v.getQueue().clear();
            v.setStatus(0);
        });
        graph.getEdges().forEach(Channel::cancel);
    }

    public void update_configuration() {
        nodes.clear();
        for (Map.Entry<Integer, Vertex> i : graph.getVertexes().entrySet()) {
            nodes.put(i.getValue(), new RouterModel(graph, i.getValue()));
        }
    }

    public abstract void processDeliveredPackage(Vertex v);

    public void deliverResult(Package p) {
        if (p != null) {
            p.getTarget().getQueue().add(p);
            Message message = p.getMsg();
            System.out.println("Package " + p.getPackage_number() + " of message " + message.getMessage_number() + " has just received to the vertex ( " + p.getTarget().getId() + " )");
        }
    }

    public void registerObserver(EventBus eventBus) {
        this.deliveryBus = eventBus;
    }
}
