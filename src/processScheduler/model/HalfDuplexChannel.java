package processScheduler.model;

import java.util.ArrayDeque;
import java.util.Deque;
import processScheduler.logic.Package;
import processScheduler.logic.SysPackage;

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
        packageQueue.addLast(p);
        updateWorkload();

    }

@Override
    public void pushToQueue(Package p) {
        p.setCounter(getWeight()*p.getSize());
        packageQueue.addFirst(p);
    updateWorkload();

}

    private void updateWorkload() {
        if(packageQueue.isEmpty()){
            workloadProperty().set(0);
        }else {
            try {
                SysPackage p = (SysPackage) packageQueue.getFirst();
                workloadProperty().setValue(p.mode.modeColorSelector);
            }
            catch (ClassCastException e){
                workloadProperty().setValue(1);
            }
        }
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
        }
        updateWorkload();
        return first;
    }

    @Override
    public int getTraffic(Vertex dest) {
        return packageQueue.stream().mapToInt(Package::getCounter).sum();
    }

    @Override
    public void cancel() {
        packageQueue.clear();
        workloadProperty().set(0);
    }

    @Override
    public int getRealWeight(Vertex dest) {
        return getWeight()+packageQueue.stream().mapToInt(Package::getCounter).sum();
    }
}