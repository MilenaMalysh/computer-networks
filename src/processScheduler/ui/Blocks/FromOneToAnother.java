package processScheduler.ui.Blocks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import processScheduler.logic.Strategy;
import processScheduler.model.Graph;
import processScheduler.model.Vertex;


/**
 * Created by Milena on 16.11.2015.
 */
public class FromOneToAnother extends BorderPane {
    private final Graph graph;
//    private final EventBus eventBus;
    private ListView<Integer> list1;
    private ListView<Integer> list2;
    private VBox vbox1;
    private VBox vbox2;
    private BorderPane bordpane1;
    private VBox vboxmain;
    private Button btn1;
    private TextField field;
//    private Button btn2;


    public FromOneToAnother(Graph graph) {
        this.graph = graph;
//        this.eventBus = eventBus;
        vboxmain = new VBox(10);

        Label label = new Label("One message transfer");

        label.setFont(new Font("Times New Roman", 16));


        bordpane1 = new BorderPane();
        //----------Left list(from)---------//

        vbox1 = new VBox();
        Label labelfrom = new Label("From");
        labelfrom.setFont(new Font("Times New Roman", 12));
        list1 = new ListView<Integer>();
        ObservableList<Integer> items1 = FXCollections.observableArrayList();
        items1.addAll(graph.getVertexesesId());

        list1.setItems(items1);
        vbox1.getChildren().addAll(labelfrom, list1);

        //----------Left list(to)---------//

        vbox2 = new VBox();
        Label labelto = new Label("To");
        labelto.setFont(new Font("Times New Roman", 12));
        list2 = new ListView<Integer>();
        ObservableList<Integer> items2 = FXCollections.observableArrayList();
        items2.addAll(graph.getVertexesesId());
        list2.setItems(items2);
        vbox2.getChildren().addAll(labelto, list2);


        bordpane1.setLeft(vbox1);
        bordpane1.setRight(vbox2);


        //---------------Set button and edit field-------------------//
        BorderPane bordpane2 = new BorderPane();

        VBox btnvsedit = new VBox();

        btn1 = new Button();
        btn1.setText("Send");

        field = new TextField();

        btnvsedit.getChildren().addAll(field, btn1);
        btnvsedit.setMargin(btn1, new Insets(2,2,2,2));
        bordpane2.setCenter(btnvsedit);

        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PlayEvent ev =new PlayEvent(graph.findVertex(list1.getSelectionModel().getSelectedItem()),
                        graph.findVertex(list2.getSelectionModel().getSelectedItem()),
                        Integer.parseInt(field.getText()),
                        TestNet.getRb1().isSelected() ? PlayEvent.DATAGRAM : PlayEvent.VIRTUAL);
/*                eventBus.post(new Event(
                        list1.getSelectionModel().selectedItemProperty().get(),
                        list2.getSelectionModel().selectedItemProperty().get(),
                        (field.getText().isEmpty())?0:Integer.parseInt(field.getText())));

                        */
                Strategy.eventBus.post(ev);
            }
        });

      /*btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //System.out.println("Hello World!");
                handler2.onButtonClicked2(list1.getSelectionModel().selectedItemProperty().get(),list2.getSelectionModel().selectedItemProperty().get());
            }
        });
        */

        //------------------Resume---------------------//
        vboxmain.getChildren().addAll(bordpane1,bordpane2);
        set_visual_part();
        this.setTop(label);
        this.setCenter(vboxmain);
        this.setMargin(label, new Insets(0,10,10,10));

    }
    private void set_visual_part(){

        list1.setMaxHeight(72);
        list1.setMinHeight(72);
        list1.setPrefHeight(72);
        list1.setMaxWidth(60);
        list1.setMinWidth(60);
        list1.setPrefWidth(60);

        list2.setMaxHeight(72);
        list2.setMinHeight(72);
        list2.setPrefHeight(72);
        list2.setMaxWidth(60);
        list2.setMinWidth(60);
        list2.setPrefWidth(60);

        btn1.setMaxWidth(120);
        btn1.setMinWidth(120);
        btn1.setPrefWidth(120);
        /*btn2.setMaxWidth(120);
        btn2.setMinWidth(120);
        btn2.setPrefWidth(120);*/

        vboxmain.setMaxWidth(150);
        vboxmain.setMinWidth(150);
        vboxmain.setPrefWidth(150);

        field.setPrefColumnCount(1);
        field.setPromptText("Enter Message size");
        field.setMinWidth(120);
        field.setMaxWidth(120);
        field.setPrefWidth(120);

        this.setMinSize(300,200);
        this.setMaxSize(300,200);
        this.setPrefSize(300,200);
    }


    public static class PlayEvent{
        public static final int DATAGRAM = 0;
        public static final int VIRTUAL = 1;
        public Vertex from;
        public Vertex to;
        public int msgSize;
        public int mode;
        public PlayEvent(Vertex from, Vertex to, int msgSize, int mode){
            this.from = from;
            this.to = to;
            this.msgSize = msgSize;
            this.mode = mode;
        }
    }
}
