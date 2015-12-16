package processScheduler.logic;

import processScheduler.model.Vertex;

/**
 * Created by Milena on 02.12.2015.
 */
public class SysPackage extends Package {
    public Mode mode;
    public SysPackage(Vertex target, Vertex source, Message msg, Mode mode) {
        super(target, source, -1, mode.size, msg);
        this.mode = mode;
    }

    @Override
    public String toString() {
        return String.format("%sPackage of msg#%d", mode, getMsg().getMessage_number());
    }

    public enum Mode {
        CONFIGURE(4,2), DECONFIGURE(4,3), ACCEPT(4,4), NOTIFY(2,5), NOTIFY_VIRTUAL(4,6);

        public final int size;
        public final int modeColorSelector;

        Mode(int size, int modeColorSelector){

            this.size = size;
            this.modeColorSelector = modeColorSelector;
        }
    }
}
