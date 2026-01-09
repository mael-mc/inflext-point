package com.espoch.inflexpoint.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

/**
 * Teclado virtual flotante para ingreso de expresiones matemáticas.
 */
public class TecladoVirtual {

    private final Popup popup;
    private TextField targetField;

    public TecladoVirtual() {
        this.popup = new Popup();
        this.popup.setAutoHide(true);
        inicializarUI();
    }

    private void inicializarUI() {
        VBox root = new VBox();
        root.setStyle(
                "-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;");
        root.setSpacing(10);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        // Definición de teclas
        // Fila 0: Funciones
        agregarBoton(grid, "sin", "sin(", 0, 0);
        agregarBoton(grid, "cos", "cos(", 1, 0);
        agregarBoton(grid, "tan", "tan(", 2, 0);
        agregarBoton(grid, "log", "log(", 3, 0);
        agregarBoton(grid, "ln", "ln(", 4, 0);
        agregarBoton(grid, "√", "sqrt(", 5, 0);

        // Fila 1: Números 7-9 y operadores
        agregarBoton(grid, "(", "(", 0, 1);
        agregarBoton(grid, ")", ")", 1, 1);
        agregarBoton(grid, "7", "7", 2, 1);
        agregarBoton(grid, "8", "8", 3, 1);
        agregarBoton(grid, "9", "9", 4, 1);
        agregarBoton(grid, "/", "/", 5, 1);

        // Fila 2: Números 4-6 y operadores
        agregarBoton(grid, "x", "x", 0, 2);
        agregarBoton(grid, "^", "^", 1, 2);
        agregarBoton(grid, "4", "4", 2, 2);
        agregarBoton(grid, "5", "5", 3, 2);
        agregarBoton(grid, "6", "6", 4, 2);
        agregarBoton(grid, "*", "*", 5, 2);

        // Fila 3: Números 1-3 y operadores
        agregarBoton(grid, "π", "pi", 0, 3);
        agregarBoton(grid, "e", "e", 1, 3);
        agregarBoton(grid, "1", "1", 2, 3);
        agregarBoton(grid, "2", "2", 3, 3);
        agregarBoton(grid, "3", "3", 4, 3);
        agregarBoton(grid, "-", "-", 5, 3);

        // Fila 4: 0, punto, borrar
        Button btnAC = createStyledButton("AC", "#e74c3c");
        btnAC.setOnAction(e -> {
            if (targetField != null)
                targetField.clear();
        });
        grid.add(btnAC, 0, 4);

        Button btnDel = createStyledButton("DEL", "#e74c3c");
        btnDel.setOnAction(e -> borrarCaracter());
        grid.add(btnDel, 1, 4);

        agregarBoton(grid, "0", "0", 2, 4);
        agregarBoton(grid, ".", ".", 3, 4);

        Button btnOk = createStyledButton("OK", "#2ecc71");
        btnOk.setOnAction(e -> popup.hide());
        grid.add(btnOk, 4, 4, 2, 1); // Span 2 columnas
        btnOk.setMaxWidth(Double.MAX_VALUE);

        // Estilos extra para botones específicos de números para diferenciarlos
        // (Opcional, ya que createStyledButton usa un estilo base)

        root.getChildren().add(grid);
        popup.getContent().add(root);
    }

    private void agregarBoton(GridPane grid, String label, String value, int col, int row) {
        Button btn = createStyledButton(label, "#ffffff"); // Blanco por defecto
        btn.setOnAction(e -> insertarTexto(value));
        grid.add(btn, col, row);
    }

    private Button createStyledButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setPrefWidth(45);
        btn.setPrefHeight(40);

        String textColor = (colorHex.equalsIgnoreCase("#e74c3c") || colorHex.equalsIgnoreCase("#2ecc71"))
                ? "white"
                : "#333333";

        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-radius: 3;",
                colorHex, textColor));

        // Efecto hover
        btn.setOnMouseEntered(e -> btn.setStyle(String.format(
                "-fx-background-color: derive(%s, -10%%); -fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-radius: 3;",
                colorHex, textColor)));
        btn.setOnMouseExited(e -> btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-color: #bdc3c7; -fx-border-radius: 3; -fx-background-radius: 3;",
                colorHex, textColor)));

        return btn;
    }

    private void insertarTexto(String texto) {
        if (targetField == null)
            return;

        int caretPos = targetField.getCaretPosition();
        String currentText = targetField.getText();

        // Si no hay texto o cursor al final
        if (caretPos >= currentText.length()) {
            targetField.appendText(texto);
        } else {
            // Insertar en la posición del cursor
            String newText = currentText.substring(0, caretPos) + texto + currentText.substring(caretPos);
            targetField.setText(newText);
            targetField.positionCaret(caretPos + texto.length());
        }
        targetField.requestFocus();
    }

    private void borrarCaracter() {
        if (targetField == null)
            return;

        int caretPos = targetField.getCaretPosition();
        if (caretPos > 0) {
            String currentText = targetField.getText();
            String newText = currentText.substring(0, caretPos - 1) + currentText.substring(caretPos);
            targetField.setText(newText);
            targetField.positionCaret(caretPos - 1);
        }
        targetField.requestFocus();
    }

    /**
     * Muestra el teclado vinculado al campo de texto especificado.
     * 
     * @param targetField El TextField donde se escribirá.
     * @param anchorNode  El nodo visual (botón) cerca del cual aparecerá el
     *                    teclado.
     */
    public void mostrar(TextField targetField, Node anchorNode) {
        this.targetField = targetField;
        if (popup.isShowing()) {
            popup.hide();
        } else {
            // Mostrar debajo del nodo ancla
            javafx.geometry.Point2D point = anchorNode.localToScreen(0, 0);
            popup.show(anchorNode, point.getX(), point.getY() + anchorNode.getBoundsInLocal().getHeight() + 5);
        }
    }
}
