package processScheduler.ui.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import processScheduler.logic.RouterModel;
import processScheduler.model.Channel;
import processScheduler.model.Graph;
import processScheduler.model.GraphEvent;
import processScheduler.model.Vertex;
import processScheduler.ui.graph.vertex.TextCircleVertexView;
import processScheduler.ui.graph.vertex.VertexLayer;
import processScheduler.ui.graph.vertex.VertexView;



import java.util.*;

public class GraphAdapter {
    private Group canvas;
    private ZoomableScrollPane scrollPane;
    MouseGestures mouseGestures;
    VertexLayer vertexLayer;
    HashBiMap<Vertex, VertexView> allVertexes;
    HashBiMap<Channel, EdgeView> allEdges;
    private final MenuItem[] items = {new MenuItem("Add")};
    private final ContextMenu contextMenu = new ContextMenu(items);
    {
        contextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(event.getTarget().equals(items[0])){
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Add node");
                    dialog.setContentText("Enter the node number:");
                    dialog.getEditor().textProperty().addListener(new ChangeListener<String>() {
                        @Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            if (newValue.matches("\\d*")) {
                            } else {
                                dialog.getEditor().setText(oldValue);
                            }
                        }
                    });
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()){
                        addedFromContext = true;
                        if(!model.addVertex(new Vertex(Integer.parseInt(result.get())))){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Adding error");
                            alert.setHeaderText("Cannot add vertex");
                            alert.setContentText("Reason: vertex with this id is already added");
                            alert.show();
                        }
                    }

                }
            }
        });
    }
    private boolean addedFromContext = false;
    private double contextAddX;
    private double contextAddY;
    Graph model;

    public GraphAdapter(Graph g) {
        model = g;
        model.registerListener(this);
        EventBus eventBus = new EventBus();
        eventBus.register(this);
        mouseGestures = new MouseGestures(this, eventBus);
        init();
    }

    public void init() {
        allVertexes = HashBiMap.create();
        allEdges = HashBiMap.create();
        canvas = new Group();
        vertexLayer = new VertexLayer();
        canvas.getChildren().add(vertexLayer);

        scrollPane = new ZoomableScrollPane(canvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()== MouseButton.SECONDARY&&!(event.getTarget() instanceof VertexView || event.getTarget() instanceof EdgeView)){
                    contextMenu.show(scrollPane, event.getScreenX(), event.getScreenY());
                    contextAddX = event.getSceneX();
                    contextAddY = event.getSceneY();
                }else{
                    contextMenu.hide();
                   //if (event.getButton()== MouseButton.MIDDLE&&(event.getTarget() instanceof VertexView)){
                    //    onRouterTableshow();
                   // }
                }
            }
        });
        //scrollPane.setMinSize(500, 400);
        //scrollPane.setMaxSize(500, 400);
        //scrollPane.setPrefSize(500, 400);
        /*scrollPane.setMinSize(1000, 550);
        scrollPane.setMinSize(1000, 550);
        scrollPane.setMinSize(1000, 550);*/
        //scrollPane.setMinSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //scrollPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Subscribe
    public void onVertexEvent(GraphEvent.Vertex event){
        if(event.getEvent()==GraphEvent.ADDED){
            onVertexAdded(event.getTarget());
        }else {
            onVertexRemoved(event.getTarget());
        }
    }

    @Subscribe
    public void onEdgeEvent(GraphEvent.Edge event){
        if(event.getEvent()==GraphEvent.ADDED){
            onEdgeAdded(event.getTarget());
        }else {
            onEdgeRemoved(event.getTarget());
        }
    }

    @Subscribe
    public void onMouseClickPosted(MouseGraphEvent event){
        Stage router = new Stage();
        router.setTitle("Routing Table");
        Scene scene = new Scene(new Group());

        VBox vbox = new VBox();
        Label label =new Label("From vertex #"+event.vertex.getId());

        ListView<String> list = new ListView<String>();
        ObservableList<String> data = FXCollections.observableArrayList();

        for (Map.Entry <Vertex,Vertex> k: new RouterModel(model, event.vertex).getTable().getRouting().entrySet()) {
            data.addAll("Goal: "+k.getKey().getId()+ "Through: "+k.getValue().getId()+ "Length: "+model.findChannel(k.getValue(),event.vertex).getWeight());
        }

        list.setItems(data);
        //list.setMinSize(100, 200);
        //list.setMaxSize(100, 200);
        //list.setPrefSize(100, 200);

        vbox.getChildren().addAll(label,list);




        ((Group) scene.getRoot()).getChildren().add(vbox);
        router.setScene(scene);
        router.show();
    }
    public void onVertexAdded(Vertex v) {
        VertexView view = new TextCircleVertexView(v.getId(), v.statusProperty());
        allVertexes.put(v, view);
        vertexLayer.getChildren().add(view);
        if(addedFromContext){
            view.setLayoutX(contextAddX);
            view.setLayoutY(contextAddY);
            addedFromContext = false;
        }
        mouseGestures.makeDraggable(view);
    }

    public void onVertexRemoved(Vertex v) {
        VertexView view = allVertexes.get(v);
        vertexLayer.getChildren().remove(view);
        allVertexes.remove(v);
    }

    public void onEdgeAdded(Channel e) {
        EdgeView view = new EdgeView(allVertexes.get(e.getSource()), allVertexes.get(e.getTarget()), e.getWeight(), e.workloadProperty());
        allEdges.put(e, view);
        vertexLayer.getChildren().add(0,view);
        mouseGestures.makeClickable(view);
    }




    public void onEdgeRemoved(Channel e) {
        EdgeView view = allEdges.get(e);
        vertexLayer.getChildren().remove(view);
        allEdges.remove(e);
    }

    public Set<Map.Entry<Vertex, VertexView>> getAllVertexes(){
        return allVertexes.entrySet();
    }
    public double getScale() {
        return this.scrollPane.getScaleValue();
    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getVertexLayer() {
        return this.vertexLayer;
    }

    public void removeVertex(int id){
        model.removeVertex(model.findVertex(id));
    }

    public Graph getModel() {
        return model;
    }
}