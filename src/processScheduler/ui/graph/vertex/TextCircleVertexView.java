package processScheduler.ui.graph.vertex;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TextCircleVertexView extends VertexView {

    private static final Font FONT = new Font(14);
    public static final MenuItem[] items = {new MenuItem("Delete"),
            new MenuItem("Add regular edge"),
            new MenuItem("Add satellite edge")};
    public static final ContextMenu CONTEXT_MENU = new ContextMenu(items);

    public TextCircleVertexView(int cellId, IntegerProperty status) {
        super(cellId, status);
    }

    @Override
    protected Node createView() {
        StackPane stack = new StackPane();
        stack.setDisable(true);
        Text text = new Text(Integer.toString(getVertexId()));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(FONT);
        Circle circle = new Circle(20);
        circle.setStrokeWidth(2);
        circle.setFill(Color.web("#CCCCEE"));
        circle.strokeProperty().bind(new ObjectBinding<Paint>() {
                                         {
                                             super.bind(status);
                                         }

                                         @Override
                                         protected Paint computeValue() {
                                             switch (status.get()) {
                                                 case 0:
                                                     return Color.web("#CCCCEE");
                                                 case 1:
                                                     return Color.BLUEVIOLET;
                                                 case 2:
                                                     return Color.GREEN;
                                                 case 3:
                                                     return Color.VIOLET;
                                             }
                                             return null;
                                         }
                                     }
        );
        stack.getChildren().addAll(circle, text);
        return stack;
    }
}
