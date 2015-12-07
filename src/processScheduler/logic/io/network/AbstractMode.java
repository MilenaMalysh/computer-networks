package processScheduler.logic.io.network;

import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.Collection;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Milena on 05.12.2015.
 */
public abstract class AbstractMode {
    protected Random random;
    protected TreeMap<Vertex, RouterModel> nodes;
    protected Graph graph;
    protected int amount_of_packages;
    protected int amount_of_messages;
    protected int amount_of_sys_package;
    protected int summary_waiting_time;
    protected int system_time;

    public AbstractMode(Random random,TreeMap<Vertex, RouterModel> nodes, Graph graph) {
        this.random = random;
        this.nodes = nodes;
        this.graph = graph;
        this.amount_of_packages = 0;
        this.amount_of_sys_package = 0;
        this.summary_waiting_time = 0;
        this.amount_of_messages = 0;
    }

    public void tick(){
    }

    public Message generate_message(){
        amount_of_messages++;
        //FOR TEST
        int message_size = random.nextInt(7)+1;
        //END
        //int message_size = random.nextInt(2047)+1;
        int current_message_size = align_message_size(message_size);

        Vertex source_vertex = null;
        Vertex target_vertex = null;
        Collection<Vertex> vertexes = graph.getVertexeses();
        int sourceN = random.nextInt(vertexes.size());
        int targetN;
        do{
            targetN = random.nextInt(vertexes.size());
        }while(sourceN==targetN);
        int i = 0;
        for(Vertex obj : vertexes)
        {
            if (i == sourceN){
                source_vertex = obj;
                if(target_vertex!=null)
                    break;
            }
            if (i == targetN){
                target_vertex = obj;
                if(source_vertex!=null)
                    break;
            }
            i = i + 1;
        }

        Message message = new Message(current_message_size,target_vertex, source_vertex,amount_of_messages);
        return message;
    }


/*    public int align_message_size(int message_size){
        int t = (int)((Math.log(message_size)) / (Math.log(2)));

        if (Math.pow(2, t) != message_size) {
            t++;
        }
        return (int)Math.pow(2,t);
    }
*/
    public int align_message_size(int message_size){
        int t = (int)(message_size) /Message.getPACK_SIZE();
        if (t != message_size) {
            t++;
        }
        return t*Message.getPACK_SIZE();
    }

    public void sendMessage(){};
}
