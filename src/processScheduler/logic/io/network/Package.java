package processScheduler.logic.io.network;

import processScheduler.model.Channel;
import processScheduler.model.Vertex;

/**
 * Created by Milena on 02.12.2015.
 */
public class Package {
    private Vertex target;
    private Vertex source;
    private Vertex globalTarget;
    private int package_number;
    private int size;
    private int timer;
    private int waiting_time;
    private int start_processing_time;
    private Message msg;

    public Package(Vertex target, Vertex source, int package_number, int size,Message msg) {
        this.target = target;
        this.source = source;
        this.globalTarget = target;
        this.package_number = package_number;
        this.size =size;
        this.msg = msg;
        this.timer = 0;
    }
    public int getCounter() {
        return timer;
    }

    public void setCounter(int counter) {
        this.timer = counter;
    }

    public void update(int chanel_weigth){
        this.timer--;
        System.out.println("Package with number "+package_number+" of message "+msg.getMessage_number()+" wait in channel "+chanel_weigth+". Needed time"+timer);
    }

    public boolean isDelivered(){
        return timer==0;
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

    public int getSize() {
        return size;
    }

    public int getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(int waiting_time) {
        this.waiting_time = waiting_time;
    }

    public void setStart_processing_time(int start_processing_time) {
        this.start_processing_time = start_processing_time;
    }

    public int getStart_processing_time() {
        return start_processing_time;
    }

    public int getPackage_number() {
        return package_number;
    }

    public Vertex getGlobalTarget() {
        return globalTarget;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }
}
