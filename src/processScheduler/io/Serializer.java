package processScheduler.io;


import processScheduler.model.Graph;

import java.io.File;

public class Serializer {
    private final Graph graph;
    public static int pairingFunction(int a, int b){
        return (a+b)*(a+b+1)/2+b;
    }

    public Serializer(Graph graph){
        this.graph = graph;
    }

    public void serialiseToFile(String file){
        File f = new File(file);

    }
}
