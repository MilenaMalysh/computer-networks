package processScheduler.ui.graph;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import processScheduler.ui.graph.vertex.VertexView;


public class EdgeView extends Group {
    private static final Font FONT = new Font(14);
    protected VertexView source;
    protected VertexView target;
    private int weight;
    Line line;

    public EdgeView(VertexView source, VertexView target, int weight, IntegerProperty workload) {

        this.source = source;
        this.target = target;
        line = new Line();

        DoubleBinding sourceX = source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0);
        line.startXProperty().bind(sourceX);
        DoubleBinding sourceY = source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0);
        line.startYProperty().bind(sourceY);

        DoubleBinding targetX = target.layoutXProperty().add(target.getBoundsInParent().getWidth() / 2.0);
        line.endXProperty().bind(targetX);
        DoubleBinding targetY = target.layoutYProperty().add(target.getBoundsInParent().getHeight() / 2.0);
        line.endYProperty().bind(targetY);
        line.setStroke(Color.web("#BBBBBB"));
        line.strokeProperty().bind(new ObjectBinding<Paint>() {
            {
                super.bind(workload);
            }

            @Override
            protected Paint computeValue() {
                switch (workload.get()){
                    case 0:
                        return Color.web("#BBBBBB");
                    case 1:
                        return Color.web("#0000FF");
                    case 2:
                        return Color.web("#FF9933");
                    case 3:
                        return Color.web("#CC0033");
                    case 4:
                        return Color.web("#66FF66");
                    case 5:
                        return Color.web("#00FFFF");//goluboy notify
                    case 6:
                        return Color.web("#00FFFF");//goluboy notify_virt
                    default:
                        return null;
                }
            }
        });
        line.setStrokeWidth(2);
        Text text = new Text(Integer.toString(weight));
        text.setFont(FONT);
        text.layoutXProperty().bind(sourceX.add(targetX.subtract(sourceX).divide(2)));
        text.layoutYProperty().bind(sourceY.add(targetY.subtract(sourceY).divide(2)));
        getChildren().addAll(line, text);

    }

    public VertexView getSource() {
        return source;
    }

    public VertexView getTarget() {
        return target;
    }
}