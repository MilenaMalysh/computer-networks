package processScheduler.logic;

import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Collection;
import java.util.Random;


public class RandomBuilder extends AbstractBuilder {
    public RandomBuilder(Graph graph) {
        super(graph);
    }

    @Override
    public Message build(int amount_of_messages) {
        Random random = new Random();
        //FOR TEST
        int message_size = random.nextInt(32) + 32;
        //END
        //int message_size = random.nextInt(2047)+1;
        int current_message_size = align_message_size(message_size);

        Vertex source_vertex = null;
        Vertex target_vertex = null;
        Collection<Vertex> vertexes = graph.getVertexeses();
        int sourceN = random.nextInt(vertexes.size());
        int targetN;
        do {
            targetN = random.nextInt(vertexes.size());
        } while (sourceN == targetN);
        int i = 0;
        for (Vertex obj : vertexes) {
            if (i == sourceN) {
                source_vertex = obj;
                if (target_vertex != null)
                    break;
            }
            if (i == targetN) {
                target_vertex = obj;
                if (source_vertex != null)
                    break;
            }
            i = i + 1;
        }

        Message message = new Message(current_message_size, target_vertex, source_vertex, amount_of_messages);
        return message;
    }
}
