package processScheduler.ui.graph.vertex;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TextCircleVertexView extends VertexView {

    private static final Font FONT = new Font(14);
    public static final MenuItem[] items = {new MenuItem("Delete"), new MenuItem("Add edge")};
    public static final ContextMenu CONTEXT_MENU = new ContextMenu(items);
    public TextCircleVertexView(int cellId) {
        super(cellId);
    }

    @Override
    protected Node createView() {
        StackPane stack = new StackPane();
        stack.setDisable(true);
        Text text = new Text(Integer.toString(getVertexId()));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(FONT);
        Circle circle = new Circle(20);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.web("#CCCCEE"));
        stack.getChildren().addAll(circle, text);
        return stack;
    }
}
