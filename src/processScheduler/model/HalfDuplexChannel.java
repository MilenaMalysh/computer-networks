package processScheduler.model;

import java.util.ArrayDeque;
import java.util.Deque;
import processScheduler.logic.io.network.Package;
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

    public Package getDelivered(){
        if(!packageQueue.isEmpty()&&packageQueue.getFirst().isDelivered()){
            return packageQueue.removeFirst();
        }
        return null;
    }

    @Override
    public int getTraffic(Vertex dest) {
        return packageQueue.stream().mapToInt(Package::getCounter).sum();
    }
}