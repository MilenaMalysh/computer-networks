package processScheduler.model;

import processScheduler.logic.Package;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Milena on 05.12.2015.
 */
public class DuplexChannel extends Channel {

    transient Deque<Package> targetSourceQueue;
    transient Deque<Package> sourceTargetQueue;

    public DuplexChannel(Vertex source, Vertex target, int weight) {
        super(source, target, weight);
        targetSourceQueue = new ArrayDeque<>();
        sourceTargetQueue = new ArrayDeque<>();
    }

    @Override
    public void addToQueue(Package p) {
        p.setCounter(getWeight() * p.getSize());
        if (p.getSource().equals(getSource())) {
            sourceTargetQueue.add(p);
        } else {
            targetSourceQueue.add(p);
        }
        workloadProperty().setValue(!(sourceTargetQueue.isEmpty()&&targetSourceQueue.isEmpty()));
    }

    @Override
    public int getQueueSize() {
        return sourceTargetQueue.size() + targetSourceQueue.size();
    }

    @Override
    public boolean update() {
        boolean result = false;
        if (!sourceTargetQueue.isEmpty()) {
            Package sourceTargetHead = sourceTargetQueue.getFirst();
            sourceTargetHead.update(this.getWeight());
            if (sourceTargetHead.isDelivered()) {
                result = true;
            }
        }
        if (!targetSourceQueue.isEmpty()) {
            Package targetSourceHead = targetSourceQueue.getFirst();
            targetSourceHead.update(this.getWeight());
            if (targetSourceHead.isDelivered()) {
                result = true;
            }
        }
        workloadProperty().setValue(!(sourceTargetQueue.isEmpty()&&targetSourceQueue.isEmpty()));
        return result;
    }

    public Package getTargetDelivered() {
        Package first = null;
        if (!sourceTargetQueue.isEmpty() && sourceTargetQueue.getFirst().isDelivered()) {
            first = sourceTargetQueue.removeFirst();
            return first;
        }
        workloadProperty().setValue(!(sourceTargetQueue.isEmpty()&&targetSourceQueue.isEmpty()));
        return first;
    }

    public Package getSourceDelivered() {
        Package first = null;
        if (!targetSourceQueue.isEmpty() && targetSourceQueue.getFirst().isDelivered()) {
            first = targetSourceQueue.removeFirst();
            return first;
        }
        workloadProperty().setValue(!(sourceTargetQueue.isEmpty()&&targetSourceQueue.isEmpty()));
        return first;
    }

    @Override
    public int getTraffic(Vertex dest) {
        if (dest.equals(getSource())) {
            return targetSourceQueue.stream().mapToInt(Package::getCounter).sum();
        } else {
            return sourceTargetQueue.stream().mapToInt(Package::getCounter).sum();
        }
    }

    @Override
    public void cancel() {
        sourceTargetQueue.clear();
        targetSourceQueue.clear();
        workloadProperty().set(false);
    }

    @Override
    public int getRealWeight(Vertex dest) {
        if(dest.equals(getTarget()))
            return getWeight() + sourceTargetQueue.stream().mapToInt(Package::getCounter).sum();
        else{
            return getWeight() + targetSourceQueue.stream().mapToInt(Package::getCounter).sum();
        }
    }
}