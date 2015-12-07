package processScheduler.logic;

import processScheduler.model.Channel;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;

import java.util.*;

/**
 * Created by Milena on 01.12.2015.
 */
public class RouterModel {

    private Graph graph;
    private TreeMap<Vertex, Integer> list_unprocessed;
    private RouterTable table;
    private Vertex source;
    private TreeMap<Vertex, Integer> weigths;
    private Vertex temp;
    private Integer temp_weigth;
    private TreeMap<Vertex,Vertex> rev_path;

    public RouterModel(Graph graph, Vertex source) {
        this.graph = graph;
        this.list_unprocessed =new TreeMap<>();
        this.weigths = new TreeMap<>();
        this.table = new RouterTable();
        this.source = source;
        this.rev_path = new TreeMap<>();
        buildTree();
        reverseTable();
    }

    public void buildTree(){

        for(Map.Entry<Integer, Vertex> i:graph.getVertexes().entrySet()){
            weigths.put(i.getValue(), -1);
            list_unprocessed.put(i.getValue(),0);
    }
        weigths.put(source,0);
        while (list_unprocessed.size()!=0){
            temp = getMin(list_unprocessed);
            list_unprocessed.remove(temp);

            for (Map.Entry<Vertex, Channel>i : temp.getNeighbours().entrySet()){
                temp_weigth = weigths.get(temp)+i.getValue().getRealWeight(i.getKey());
                if (  (temp_weigth< weigths.get(i.getKey()))  ||  (weigths.get(i.getKey())==-1)){
                    weigths.put(i.getKey(), temp_weigth);
                    rev_path.put(i.getKey(),temp);
                }
            }
        }
    }

    public Vertex getMin(TreeMap list){
        Integer min =weigths.get(list_unprocessed.firstEntry().getKey());
        Vertex ver_min =list_unprocessed.firstEntry().getKey();
        for (Map.Entry <Vertex, Integer> i:list_unprocessed.entrySet()){
            if (   (weigths.get(i.getKey())!=-1)  &&((weigths.get(i.getKey())<min)||(min ==-1)) ) {
                min = weigths.get(i.getKey());
                ver_min = i.getKey();
            }
        }
        return ver_min;
    }

    public RouterTable getTable() {
        return table;
    }


    public TreeMap<Vertex, Vertex> getRev_path() {
        return rev_path;
    }

    public void reverseTable(){
        for (Map.Entry<Vertex,Vertex> i:rev_path.entrySet()){
            Vertex next;
            next =i.getValue();
            if (next != source) {
                while (rev_path.get(next)!=source){
                  next =rev_path.get(next);
                }
                table.getRouting().put(i.getKey(),next);
            }else{
                table.getRouting().put(i.getKey(),i.getKey());
            }

        }
    }

}
