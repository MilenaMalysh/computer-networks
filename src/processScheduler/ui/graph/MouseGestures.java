package processScheduler.ui.graph;

import com.google.common.eventbus.EventBus;
import com.sun.javafx.geom.Edge;
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
import processScheduler.model.Channel;
import processScheduler.model.DuplexChannel;
import processScheduler.model.HalfDuplexChannel;
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
                            } else if (actionEvent.getTarget().equals(TextCircleVertexView.items[1]) || actionEvent.getTarget().equals(TextCircleVertexView.items[2])) {
                                ChoiceDialog<String> typeDialog = new ChoiceDialog<String>("Duplex", "Duplex", "HalfDuplex");
                                typeDialog.setTitle("Adding channel");
                                typeDialog.setHeaderText("Channel type");
                                typeDialog.setContentText("Choose channel type");
                                Optional<String> type = typeDialog.showAndWait();
                                if (type.isPresent()) {
                                    Class<? extends Channel> typeClass = type.equals("Duplex")?DuplexChannel.class: HalfDuplexChannel.class;
                                    Set<Vertex> options = graphAdapter.allVertexes.keySet();
                                    ChoiceDialog<Vertex> dialog = new ChoiceDialog<Vertex>(options.iterator().next(), options);
                                    dialog.setTitle("Adding channel");
                                    dialog.setHeaderText("Adding channel from vertex " + node.getVertexId());
                                    dialog.setContentText("Choose target vertex");
                                    Optional<Vertex> result = dialog.showAndWait();
                                    if (result.isPresent()) {
                                        Vertex selected = result.get();
                                        Vertex source = graphAdapter.allVertexes.inverse().get(node);
                                        if (source.equals(selected)) {
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("Adding error");
                                            alert.setHeaderText("Cannot add channel");
                                            alert.setContentText("Reason: cannot add circular channels");
                                            alert.show();
                                            return;
                                        }
                                        ChoiceDialog<Integer> dialogWeigth =  new ChoiceDialog<>(1, 1, 2, 4, 5, 6, 7, 10, 12, 15, 18);
                                        dialogWeigth.setTitle("Adding channel");
                                        dialogWeigth.setHeaderText("Set weight for channel");
                                        dialogWeigth.setContentText("Choose channel weight:");
                                        Optional<Integer> resultWeight = dialogWeigth.showAndWait();
                                        if (resultWeight.isPresent()) {
                                            Integer weight = resultWeight.get();
                                            if (actionEvent.getTarget().equals(TextCircleVertexView.items[2]))
                                                weight = weight * 3;
                                            graphAdapter.model.addEdge(graphAdapter.model.generateEdge(source.getId(), selected.getId(), weight, typeClass));
                                        }
                                    }
                                }
                            }
                        }
                    };
                    TextCircleVertexView.CONTEXT_MENU.setOnAction(onContextMenuClicked);
                } else {
                    if (event.getButton() == MouseButton.MIDDLE) {
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