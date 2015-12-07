package processScheduler.model;

import java.util.ArrayDeque;
import java.util.Deque;
import processScheduler.logic.io.network.Package;
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
        p.setCounter(getWeight()*p.getSize());
        if(p.getSource().equals(getSource())){
            sourceTargetQueue.add(p);
        }else{
            targetSourceQueue.add(p);
        }
    }

    @Override
    public int getQueueSize() {
        return sourceTargetQueue.size()+targetSourceQueue.size();
    }

    @Override
    public boolean update() {
        boolean result = false;
        if(!sourceTargetQueue.isEmpty()){
            Package sourceTargetHead = sourceTargetQueue.getFirst();
            sourceTargetHead.update(this.getWeight());
            if(sourceTargetHead.isDelivered()){
                result = true;
            }
        }
        if(!targetSourceQueue.isEmpty()){
            Package targetSourceHead = targetSourceQueue.getFirst();
            targetSourceHead.update(this.getWeight());
            if(targetSourceHead.isDelivered()){
                result = true;
            }
        }
        return result;
    }

    public Package getTargetDelivered(){
        if(!sourceTargetQueue.isEmpty()&&sourceTargetQueue.getFirst().isDelivered()){
            return sourceTargetQueue.removeFirst();
        }
        return null;
    }

    public Package getSourceDelivered(){
        if(!targetSourceQueue.isEmpty()&&targetSourceQueue.getFirst().isDelivered()){
            return targetSourceQueue.removeFirst();
        }
        return null;
    }

    @Override
    public int getTraffic(Vertex dest) {
        if(dest.equals(getSource())){
            return targetSourceQueue.stream().mapToInt(Package::getCounter).sum();
        }
        else {
            return sourceTargetQueue.stream().mapToInt(Package::getCounter).sum();
        }
    }
}