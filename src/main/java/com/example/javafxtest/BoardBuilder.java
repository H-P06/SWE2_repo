package com.example.javafxtest;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.util.Map;

// Builds the visual game board grid — column/row constraints, tile buttons, and border styling.
public class BoardBuilder {

    @FunctionalInterface
    public interface TileClickHandler {
        void onClick(int x, int y, Button tile);
    }

    // Populates gameBoard with octagon and diamond tile buttons, registering each in buttonMap.
    public static void build(GridPane board, StackPane sizeRef,
                             Map<String, Button> buttonMap,
                             TileClickHandler onClick) {
        // Narrow odd columns/rows act as visual spacers between tiles
        for (int col = 0; col < 21; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            if (col % 2 == 1) { cc.setPrefWidth(3); cc.setMaxWidth(3); }
            board.getColumnConstraints().add(cc);
        }
        for (int row = 0; row < 21; row++) {
            RowConstraints rc = new RowConstraints();
            if (row % 2 == 1) { rc.setPrefHeight(3); rc.setMaxHeight(3); }
            board.getRowConstraints().add(rc);
        }

        // Even,even = octagon tile; odd,odd = diamond connector tile
        for (int vRow = 0; vRow < 21; vRow++) {
            for (int vCol = 0; vCol < 21; vCol++) {
                if (vRow % 2 == 0 && vCol % 2 == 0)
                    addTile(board, sizeRef, buttonMap, vCol, vRow, "board-tile", 30, onClick);
                else if (vRow % 2 == 1 && vCol % 2 == 1)
                    addTile(board, sizeRef, buttonMap, vCol, vRow, "diamond-tile", 42, onClick);
            }
        }
    }

    private static void addTile(GridPane board, StackPane sizeRef, Map<String, Button> buttonMap,
                                int x, int y, String styleClass, double divisor,
                                TileClickHandler onClick) {
        Button btn = new Button();
        btn.getStyleClass().add(styleClass);
        bindSize(btn, sizeRef, divisor);

        if (styleClass.equals("diamond-tile")) {
            GridPane.setHalignment(btn, HPos.CENTER);
            GridPane.setValignment(btn, VPos.CENTER);
        }

        // Corner and edge tiles get coloured borders to mark the board perimeter
        if      (x == 0  && y == 0)  btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 3 0 0 0, 3 0 0 3;");
        else if (x == 20 && y == 0)  btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 3 0 0 0, 3 3 0 0;");
        else if (x == 0  && y == 20) btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 0 0 3 0, 0 0 3 3;");
        else if (x == 20 && y == 20) btn.setStyle("-fx-background-color: #000000, #ffffff, #725242; -fx-background-insets: 0, 0 0 3 0, 0 3 3 0;");
        else if (y == 0)  btn.setStyle("-fx-background-color: #000000, #725242; -fx-background-insets: 0, 3 0 0 0;");
        else if (y == 20) btn.setStyle("-fx-background-color: #000000, #725242; -fx-background-insets: 0, 0 0 3 0;");
        else if (x == 0)  btn.setStyle("-fx-background-color: #ffffff, #725242; -fx-background-insets: 0, 0 0 0 3;");
        else if (x == 20) btn.setStyle("-fx-background-color: #ffffff, #725242; -fx-background-insets: 0, 0 3 0 0;");

        btn.setOnAction(e -> onClick.onClick(x, y, btn));
        board.add(btn, x, y);
        buttonMap.put(x + "," + y, btn);
    }

    // Binds a button's size to a fraction of the board height so tiles scale with the window.
    private static void bindSize(Button btn, StackPane sizeRef, double divisor) {
        btn.prefWidthProperty().bind(sizeRef.heightProperty().divide(divisor));
        btn.prefHeightProperty().bind(sizeRef.heightProperty().divide(divisor));
        btn.minWidthProperty().bind(btn.prefWidthProperty());
        btn.minHeightProperty().bind(btn.prefHeightProperty());
        btn.setFocusTraversable(false);
    }
}
