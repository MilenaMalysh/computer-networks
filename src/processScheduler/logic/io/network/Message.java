package processScheduler.logic.io.network;

import processScheduler.model.Vertex;

import java.util.*;

/**
 * Created by Milena on 02.12.2015.
 */
public class Message {
    public static final int PACK_SIZE = 2;
    private float pack_amount_f;
    private int pack_amount;
    private LinkedHashMap<Package, Boolean> packages = new LinkedHashMap<>();
    private Vertex target;
    private Vertex source;
    private int message_number;

    public Message(int message_size, Vertex target, Vertex source, int message_number) {
        this.target = target;
        this.source = source;
        this.message_number = message_number;
        this.pack_amount_f = ((float) message_size) / PACK_SIZE;
        if (this.pack_amount_f != (int) this.pack_amount_f) {
            this.pack_amount = (int) this.pack_amount_f + 1;
        } else {
            this.pack_amount = (int) pack_amount_f;
        }

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
}
