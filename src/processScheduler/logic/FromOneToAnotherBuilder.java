package processScheduler.logic;

import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Collection;
import java.util.Random;


public class FromOneToAnotherBuilder extends AbstractBuilder {
    private Vertex source;
    private Vertex target;
    private int message_size;

    public FromOneToAnotherBuilder(Graph graph, Vertex source, Vertex target, int message_size) {
        super(graph);
        this.source = source;
        this.target = target;
        this.message_size = message_size;
    }

    @Override
    public Message build(int amount_of_messages) {
        //int current_message_size = align_message_size(message_size);
        Message message = new Message(message_size, target, source, amount_of_messages);
        return message;
    }

}
