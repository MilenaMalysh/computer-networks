package processScheduler.ui.graph;

import com.google.common.eventbus.EventBus;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import processScheduler.model.DuplexChannel;
import processScheduler.model.Vertex;
import processScheduler.ui.graph.vertex.TextCircleVertexView;
import processScheduler.ui.graph.vertex.VertexView;


import java.util.Optional;
import java.util.Set;

public class MouseGestures {

    final DragContext dragContext = new DragContext();

    GraphAdapter graphAdapter;

    EventBus eventBus;
    public MouseGestures(GraphAdapter graphAdapter, EventBus eventBus) {
        this.graphAdapter = graphAdapter;
        this.eventBus = eventBus;
    }

    public void makeDraggable(final Node node) {
        node.setOnMousePressed(onMousePressedEventHandler);
        node.setOnMouseDragged(onMouseDraggedEventHandler);
        node.setOnMouseReleased(onMouseReleasedEventHandler);
        node.setOnMouseClicked(onMouseClicked);
    }

    EventHandler<MouseEvent> onMouseClicked = new EventHandler<MouseEvent>() {

        private VertexView node;

        @Override
        public void handle(MouseEvent event) {
            System.out.println("Object clicked: " + event.getSource() + " button = " + event.getButton());
            try {
                node = (VertexView) event.getSource();
                if (event.getButton() == MouseButton.SECONDARY) {
                    TextCircleVertexView.CONTEXT_MENU.show(node.getView(), event.getScreenX(), event.getScreenY());
                    EventHandler<ActionEvent> onContextMenuClicked = new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            System.out.println(actionEvent.getTarget().equals(TextCircleVertexView.items[0]));
                            if (actionEvent.getTarget().equals(TextCircleVertexView.items[0])) {
                                graphAdapter.removeVertex(node.getVertexId());
                            } else if (actionEvent.getTarget().equals(TextCircleVertexView.items[1])) {
                                Set<Vertex> options = graphAdapter.allVertexes.keySet();
                                ChoiceDialog<Vertex> dialog = new ChoiceDialog<Vertex>(options.iterator().next(), options);
                                dialog.setTitle("Adding edge");
                                dialog.setHeaderText("Adding Edge from vertex " + node.getVertexId());
                                dialog.setContentText("Choose target edge");
                                Optional<Vertex> result = dialog.showAndWait();
                                if (result.isPresent()) {
                                    Vertex selected = result.get();
                                    Vertex source = graphAdapter.allVertexes.inverse().get(node);
                                    if (source.equals(selected)) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Adding error");
                                        alert.setHeaderText("Cannot add edge");
                                        alert.setContentText("Reason: cannot add circular edges");
                                        alert.show();
                                        return;
                                    }
                                    TextInputDialog dialogWeigth = new TextInputDialog("");
                                    dialog.setTitle("Add node");
                                    dialog.setContentText("Enter the node number:");
                                    dialogWeigth.getEditor().textProperty().addListener(new ChangeListener<String>() {
                                        @Override
                                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                            if (newValue.matches("\\d*")) {
                                            } else {
                                                dialogWeigth.getEditor().setText(oldValue);
                                            }
                                        }
                                    });
                                    Optional<String> resultWeight = dialogWeigth.showAndWait();
                                    if (resultWeight.isPresent()) {
                                        graphAdapter.model.addEdge(graphAdapter.model.generateEdge(source.getId(), selected.getId(), Integer.valueOf(resultWeight.get()),DuplexChannel.class));
                                    }
                                }
                            }
                        }
                    };
                    TextCircleVertexView.CONTEXT_MENU.setOnAction(onContextMenuClicked);
                }else{
                    if(event.getButton()==MouseButton.MIDDLE){
                        eventBus.post(new MouseGraphEvent(0, graphAdapter.allVertexes.inverse().get(node)));
                    }
                }
            } catch (Exception e) {
                System.out.println("Not an vertex view clicked: " + event.getSource());
            }

        }
    };

    EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                Node node = (Node) event.getSource();

                double scale = graphAdapter.getScale();

                dragContext.x = node.getBoundsInParent().getMinX() * scale - event.getScreenX();
                dragContext.y = node.getBoundsInParent().getMinY() * scale - event.getScreenY();
            }
        }
    };

    EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY) {
                Node node = (Node) event.getSource();

                double offsetX = event.getScreenX() + dragContext.x;
                double offsetY = event.getScreenY() + dragContext.y;

                // adjust the offset in case we are zoomed
                double scale = graphAdapter.getScale();

                offsetX /= scale;
                offsetY /= scale;

                node.relocate(offsetX, offsetY);
            }
        }
    };

    EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {

        }
    };

    class DragContext {
        double x;
        double y;
    }
}