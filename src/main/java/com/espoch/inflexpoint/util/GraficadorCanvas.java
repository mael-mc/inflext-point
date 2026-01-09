package com.espoch.inflexpoint.util;

import com.espoch.inflexpoint.modelos.calculos.Evaluador;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.entidades.PuntoCritico;
import com.espoch.inflexpoint.modelos.enumeraciones.TipoPuntoCritico;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Graficador interactivo usando Canvas.
 * 
 * Características:
 * - Zoom con rueda del mouse
 * - Pan (arrastre) con mouse
 * - Mostrar coordenadas al hacer hover
 * - Grafica cualquier función evaluable
 * - Marca puntos críticos visualmente
 */
public class GraficadorCanvas {

    private Canvas canvas;
    private GraphicsContext gc;

    // Rango visible actual
    private double minX = -10.0;
    private double maxX = 10.0;
    private double minY = -10.0;
    private double maxY = 10.0;

    // Para pan (arrastre)
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;

    // Evaluador actual
    private Evaluador evaluador;
    private ResultadoAnalisis resultado;
    private String expresion;

    /**
     * Crea un graficador con Canvas de tamaño específico.
     */
    public GraficadorCanvas(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        configurarEventos();
    }

    /**
     * Configura eventos de mouse para interactividad.
     */
    private void configurarEventos() {
        // Zoom con rueda del mouse
        canvas.setOnScroll(this::handleScroll);

        // Pan con arrastre
        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);

        // Mostrar coordenadas
        canvas.setOnMouseMoved(this::handleMouseMoved);
    }

    /**
     * Grafica una función con sus puntos críticos.
     */
    public void graficar(String expresion, ResultadoAnalisis resultado)
            throws ExpresionInvalidaException {

        this.expresion = expresion;
        this.resultado = resultado;
        this.evaluador = new Evaluador(expresion);

        dibujar();
    }

    /**
     * Dibuja todo el contenido del canvas.
     */
    private void dibujar() {
        // Limpiar canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dibujar cuadrícula
        dibujarCuadricula();

        // Dibujar ejes
        dibujarEjes();

        // Dibujar función
        if (evaluador != null) {
            dibujarFuncion();
        }

        // Dibujar puntos críticos
        if (resultado != null) {
            dibujarPuntosCriticos();
        }

        // Dibujar título
        dibujarTitulo();
    }

    /**
     * Dibuja la cuadrícula de fondo.
     */
    private void dibujarCuadricula() {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Calcular espaciado de la cuadrícula
        double rangoX = maxX - minX;
        double rangoY = maxY - minY;

        // Líneas verticales
        double stepX = calcularStep(rangoX);
        for (double x = Math.ceil(minX / stepX) * stepX; x <= maxX; x += stepX) {
            double screenX = xToScreen(x);
            gc.strokeLine(screenX, 0, screenX, height);
        }

        // Líneas horizontales
        double stepY = calcularStep(rangoY);
        for (double y = Math.ceil(minY / stepY) * stepY; y <= maxY; y += stepY) {
            double screenY = yToScreen(y);
            gc.strokeLine(0, screenY, width, screenY);
        }
    }

    /**
     * Calcula el espaciado apropiado para la cuadrícula.
     */
    private double calcularStep(double rango) {
        double[] steps = { 0.1, 0.2, 0.5, 1, 2, 5, 10, 20, 50, 100 };
        double targetLines = 10;
        double targetStep = rango / targetLines;

        for (double step : steps) {
            if (step >= targetStep) {
                return step;
            }
        }
        return steps[steps.length - 1];
    }

    /**
     * Dibuja los ejes X e Y.
     */
    private void dibujarEjes() {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Eje Y (x = 0)
        if (minX <= 0 && maxX >= 0) {
            double screenX = xToScreen(0);
            gc.strokeLine(screenX, 0, screenX, height);

            // Etiqueta Y
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(12));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Y", screenX + 15, 15);
        }

        // Eje X (y = 0)
        if (minY <= 0 && maxY >= 0) {
            double screenY = yToScreen(0);
            gc.strokeLine(0, screenY, width, screenY);

            // Etiqueta X
            gc.fillText("X", width - 15, screenY - 5);
        }

        // Dibujar marcas y valores
        dibujarMarcasEjes();
    }

    /**
     * Dibuja las marcas numéricas en los ejes.
     */
    private void dibujarMarcasEjes() {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(10));
        gc.setTextAlign(TextAlignment.CENTER);

        double rangoX = maxX - minX;
        double rangoY = maxY - minY;
        double stepX = calcularStep(rangoX);
        double stepY = calcularStep(rangoY);

        // Marcas en eje X
        if (minY <= 0 && maxY >= 0) {
            double screenY = yToScreen(0);
            for (double x = Math.ceil(minX / stepX) * stepX; x <= maxX; x += stepX) {
                if (Math.abs(x) > 0.001) { // Evitar el 0
                    double screenX = xToScreen(x);
                    gc.fillText(String.format("%.1f", x), screenX, screenY + 15);
                }
            }
        }

        // Marcas en eje Y
        if (minX <= 0 && maxX >= 0) {
            double screenX = xToScreen(0);
            for (double y = Math.ceil(minY / stepY) * stepY; y <= maxY; y += stepY) {
                if (Math.abs(y) > 0.001) { // Evitar el 0
                    double screenY = yToScreen(y);
                    gc.fillText(String.format("%.1f", y), screenX - 20, screenY + 4);
                }
            }
        }
    }

    /**
     * Dibuja la función.
     */
    private void dibujarFuncion() {
        gc.setStroke(Color.web("#FF6B35")); // Naranja
        gc.setLineWidth(2.5);

        double width = canvas.getWidth();
        double step = (maxX - minX) / width; // Un punto por píxel

        Double prevScreenX = null;
        Double prevScreenY = null;

        for (double x = minX; x <= maxX; x += step) {
            try {
                double y = evaluador.evaluar(x);

                // Filtrar valores inválidos
                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    prevScreenX = null;
                    prevScreenY = null;
                    continue;
                }

                // Limitar valores extremos
                if (Math.abs(y) > 1000) {
                    prevScreenX = null;
                    prevScreenY = null;
                    continue;
                }

                double screenX = xToScreen(x);
                double screenY = yToScreen(y);

                // Verificar que esté dentro del canvas
                if (screenY < -100 || screenY > canvas.getHeight() + 100) {
                    prevScreenX = null;
                    prevScreenY = null;
                    continue;
                }

                // Dibujar línea si hay punto previo
                if (prevScreenX != null && prevScreenY != null) {
                    // Evitar líneas verticales largas (discontinuidades)
                    if (Math.abs(screenY - prevScreenY) < canvas.getHeight() / 2) {
                        gc.strokeLine(prevScreenX, prevScreenY, screenX, screenY);
                    }
                }

                prevScreenX = screenX;
                prevScreenY = screenY;

            } catch (ExpresionInvalidaException e) {
                prevScreenX = null;
                prevScreenY = null;
            }
        }
    }

    /**
     * Dibuja los puntos críticos.
     */
    private void dibujarPuntosCriticos() {
        // Dibujar puntos críticos (máximos y mínimos)
        if (resultado.getPuntosCriticos() != null) {
            for (PuntoCritico pc : resultado.getPuntosCriticos()) {
                if (pc.getTipoPuntoCritico() == TipoPuntoCritico.MAXIMO) {
                    dibujarPunto(pc.getX(), pc.getY(), Color.RED, "Máx");
                } else if (pc.getTipoPuntoCritico() == TipoPuntoCritico.MINIMO) {
                    dibujarPunto(pc.getX(), pc.getY(), Color.GREEN, "Mín");
                }
            }
        }

        // Dibujar puntos de inflexión
        if (resultado.getPuntosInflexion() != null) {
            for (PuntoCritico pi : resultado.getPuntosInflexion()) {
                dibujarPunto(pi.getX(), pi.getY(), Color.ORANGE, "Inf");
            }
        }
    }

    /**
     * Dibuja un punto marcado en la gráfica.
     */
    private void dibujarPunto(double x, double y, Color color, String etiqueta) {
        double screenX = xToScreen(x);
        double screenY = yToScreen(y);

        // Verificar que esté visible
        if (screenX < 0 || screenX > canvas.getWidth() ||
                screenY < 0 || screenY > canvas.getHeight()) {
            return;
        }

        // Círculo
        gc.setFill(color);
        gc.fillOval(screenX - 5, screenY - 5, 10, 10);

        // Borde
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(screenX - 5, screenY - 5, 10, 10);

        // Etiqueta
        gc.setFill(color);
        gc.setFont(Font.font(10));
        gc.fillText(etiqueta, screenX + 8, screenY - 8);
    }

    /**
     * Dibuja el título de la gráfica.
     */
    private void dibujarTitulo() {
        if (expresion != null) {
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(14));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("f(x) = " + expresion, 10, 20);
        }
    }

    // ===== Conversión de coordenadas =====

    private double xToScreen(double x) {
        return (x - minX) / (maxX - minX) * canvas.getWidth();
    }

    private double yToScreen(double y) {
        return canvas.getHeight() - (y - minY) / (maxY - minY) * canvas.getHeight();
    }

    private double screenToX(double screenX) {
        return minX + (screenX / canvas.getWidth()) * (maxX - minX);
    }

    private double screenToY(double screenY) {
        return minY + ((canvas.getHeight() - screenY) / canvas.getHeight()) * (maxY - minY);
    }

    // ===== Manejo de eventos =====

    private void handleScroll(ScrollEvent event) {
        double zoomFactor = event.getDeltaY() > 0 ? 0.9 : 1.1;

        // Zoom centrado en el mouse
        double mouseX = screenToX(event.getX());
        double mouseY = screenToY(event.getY());

        double rangoX = maxX - minX;
        double rangoY = maxY - minY;

        double newRangoX = rangoX * zoomFactor;
        double newRangoY = rangoY * zoomFactor;

        minX = mouseX - newRangoX * (mouseX - minX) / rangoX;
        maxX = mouseX + newRangoX * (maxX - mouseX) / rangoX;
        minY = mouseY - newRangoY * (mouseY - minY) / rangoY;
        maxY = mouseY + newRangoY * (maxY - mouseY) / rangoY;

        dibujar();
        event.consume();
    }

    private void handleMousePressed(MouseEvent event) {
        isDragging = true;
        lastMouseX = event.getX();
        lastMouseY = event.getY();
        canvas.setCursor(javafx.scene.Cursor.CLOSED_HAND);
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            double dx = event.getX() - lastMouseX;
            double dy = event.getY() - lastMouseY;

            double worldDx = -dx / canvas.getWidth() * (maxX - minX);
            double worldDy = dy / canvas.getHeight() * (maxY - minY);

            minX += worldDx;
            maxX += worldDx;
            minY += worldDy;
            maxY += worldDy;

            lastMouseX = event.getX();
            lastMouseY = event.getY();

            dibujar();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isDragging = false;
        canvas.setCursor(javafx.scene.Cursor.DEFAULT);
    }

    private void handleMouseMoved(MouseEvent event) {
        // NO redibujar todo en cada movimiento de mouse
        // Solo mostrar coordenadas sin redibujar la función
        // (Para evitar problemas de rendimiento)
    }

    /**
     * Resetea el zoom al rango original.
     */
    public void resetearZoom() {
        minX = -10.0;
        maxX = 10.0;
        minY = -10.0;
        maxY = 10.0;
        dibujar();
    }

    /**
     * Obtiene el Canvas para añadir a la UI.
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Ajusta el tamaño del canvas y redibuja.
     */
    public void setSize(double width, double height) {
        // Validar tamaños mínimos y máximos
        if (width <= 0 || height <= 0) {
            return; // Ignorar tamaños inválidos
        }

        if (width > 4000 || height > 4000) {
            return; // Evitar tamaños excesivos que puedan causar problemas de memoria
        }

        // Solo redimensionar si el cambio es significativo (> 5 píxeles)
        if (Math.abs(canvas.getWidth() - width) < 5 && Math.abs(canvas.getHeight() - height) < 5) {
            return;
        }

        canvas.setWidth(width);
        canvas.setHeight(height);

        // Solo redibujar si ya hay algo que dibujar
        if (evaluador != null) {
            dibujar();
        }
    }
}
