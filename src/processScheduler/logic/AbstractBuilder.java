package processScheduler.logic;

import processScheduler.model.Graph;

public abstract class AbstractBuilder {
    protected Graph graph;

    public AbstractBuilder(Graph graph){
        this.graph = graph;
    }

    public abstract Message build(int amount_of_messages);

    public int align_message_size(int message_size){
        int t = (int)(message_size) /Message.getPACK_SIZE();
        if (t != message_size) {
            t++;
        }
        return t*Message.getPACK_SIZE();
    }
}
