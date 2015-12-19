package processScheduler.logic;

import processScheduler.model.Vertex;

import java.util.*;

/**
 * Created by Milena on 02.12.2015.
 */
public class Message {
    public static int PACK_SIZE = 8;
    public static int PACK_HEADER_SIZE = 1;
    private int pack_amount;
    private LinkedHashMap<Package, Boolean> packages = new LinkedHashMap<>();
    private Vertex target;
    private Vertex source;
    private int message_number;
    private int start;
    private int deliveryTime;
    private int confirmations;
    private boolean transmitted;

    public Message(int message_size, Vertex target, Vertex source, int message_number) {
        this.target = target;
        this.source = source;
        this.confirmations = 0;
        this.transmitted = false;
        this.message_number = message_number;
        this.pack_amount =  message_size/ (PACK_SIZE-PACK_HEADER_SIZE);
        if(pack_amount*(PACK_SIZE-PACK_HEADER_SIZE)<message_size)
            pack_amount++;
        for (int i = 1; i <= this.pack_amount; i++) {
            packages.put(new Package(this.target, this.source, i, PACK_SIZE, this), false);
        }
    }

    public Set<Package> getPackages() {
        return packages.keySet();
    }

    public int getPack_amount() {
        return pack_amount;
    }

    public static int getPACK_SIZE() {
        return PACK_SIZE;
    }

    public Vertex getSource() {
        return source;
    }

    public int getMessage_number() {
        return message_number;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public void confirmDelivery(Package pkg){
        packages.put(pkg, true);
    }

    public LinkedHashMap<Package, Boolean> getDeliveryMap(){
        return packages;
    }

    public boolean isDelivered(){
        return packages.entrySet().stream().allMatch(Map.Entry::getValue);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void notifyDelivery(Package pkg){
        confirmations++;
    }

    public boolean isNotified(){
        return packages.size()<=confirmations;
    }

    public boolean isTransmitted() {
        return transmitted;
    }

    public void setTransmitted(boolean transmitted) {
        this.transmitted = transmitted;
    }

    @Override
    public String toString() {
        return String.format("Message#%d(size=%d)", message_number, pack_amount*PACK_SIZE);
    }
}
