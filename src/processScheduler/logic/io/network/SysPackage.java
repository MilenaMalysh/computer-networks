package processScheduler.logic.io.network;

import processScheduler.model.Vertex;

/**
 * Created by Milena on 02.12.2015.
 */
public class SysPackage extends Package{
    public Mode mode;
    public SysPackage(Vertex target, Vertex source, int size, Message msg, Mode mode) {
        super(target, source, -1, size, msg);
        this.mode = mode;
    }
    public static enum Mode {
        CONFIGURE, DECONFIGURE, ACCEPT
    }
}
