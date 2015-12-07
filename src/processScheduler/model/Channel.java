package processScheduler.model;

import processScheduler.logic.io.Serializer;
import processScheduler.logic.io.network.Package;

public abstract class Channel implements Comparable<Channel> {
    private int id;
    private int weight;
    private Vertex source;
    private Vertex target;

    public Channel(Vertex source, Vertex target, int weight){
        this.id = Serializer.pairingFunction(source.getId(), target.getId());
        this.source = source;
        this.target = target;
        source.registerEdge(this);
        target.registerEdge(this);
        this.weight = weight;

    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Channel o) {
        return Integer.compare(getId(), o.getId());
    }

    public abstract void addToQueue(Package p);

    public abstract int getQueueSize();

    public abstract boolean update();

    public abstract int getTraffic(Vertex dest);
}
