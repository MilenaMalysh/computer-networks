package processScheduler.model;

import java.util.ArrayDeque;
import java.util.Deque;
import processScheduler.logic.Package;
/**
 * Created by Milena on 05.12.2015.
 */
public class HalfDuplexChannel extends Channel {

    transient Deque<Package> packageQueue;

    public HalfDuplexChannel(Vertex source, Vertex target, int weight) {
        super(source, target, weight);
        packageQueue = new ArrayDeque<>();
    }

    @Override
    public void addToQueue(Package p) {
        p.setCounter(getWeight()*p.getSize());
        packageQueue.add(p);
        workloadProperty().setValue(!packageQueue.isEmpty());

    }

    @Override
    public int getQueueSize() {
        return packageQueue.size();
    }

    @Override
    public boolean update() {
        if(!packageQueue.isEmpty()){
            Package head = packageQueue.getFirst();
            head.update(this.getWeight());
            if(head.isDelivered()){
                return true;
            }
        }
        return false;
    }

    public Package getDelivered() {
        Package first = null;
        if (!packageQueue.isEmpty() && packageQueue.getFirst().isDelivered()) {
            first = packageQueue.removeFirst();
            return first;
        }
        workloadProperty().setValue(!packageQueue.isEmpty());
        return first;
    }

    @Override
    public int getTraffic(Vertex dest) {
        return packageQueue.stream().mapToInt(Package::getCounter).sum();
    }

    @Override
    public void cancel() {
        packageQueue.clear();
        workloadProperty().set(false);
    }

    @Override
    public int getRealWeight(Vertex dest) {
        return getWeight()+packageQueue.stream().mapToInt(Package::getCounter).sum();
    }
}