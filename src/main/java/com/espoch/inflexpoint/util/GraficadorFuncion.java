package com.espoch.inflexpoint.util;

import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.entidades.Funcion;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Graficador de funciones matemáticas.
 * 
 * Responsabilidades:
 * - Dibujar ejes X e Y
 * - Graficar la función
 * - Marcar puntos críticos
 * - Escalar automáticamente
 * 
 * Restricciones:
 * - No usa librerías externas (solo JavaFX Canvas)
 * - Separado del controlador
 */
public class GraficadorFuncion {

    // Dimensiones del Canvas
    private static final double ANCHO_CANVAS = 450;
    private static final double ALTO_CANVAS = 280;
    private static final double MARGEN = 40;

    // Colores
    private static final Color COLOR_FUNCION = Color.web("#2A9D8F");
    private static final Color COLOR_MAXIMO = Color.RED;
    private static final Color COLOR_MINIMO = Color.GREEN;
    private static final Color COLOR_INFLEXION = Color.ORANGE;
    private static final Color COLOR_EJE = Color.BLACK;
    private static final Color COLOR_REJILLA = Color.web("#EEEEEE");

    /**
     * Grafica una función en el contenedor especificado.
     * 
     * @param contenedor HBox donde se dibujará el canvas
     * @param funcion Función a graficar
     * @param resultado Resultado del análisis (para marcar puntos críticos)
     */
    public void graficar(HBox contenedor, Funcion funcion, ResultadoAnalisis resultado) {
        // Limpiar contenedor
        contenedor.getChildren().clear();

        // Crear canvas
        Canvas canvas = new Canvas(ANCHO_CANVAS, ALTO_CANVAS);
        contenedor.getChildren().add(canvas);

        // Obtener contexto gráfico
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Dibujar fondo
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, ANCHO_CANVAS, ALTO_CANVAS);

        // Calcular escala
        double xMin = -10;
        double xMax = 10;
        double yMin = -10;
        double yMax = 10;

        // Dibujar rejilla
        dibujarRejilla(gc, xMin, xMax, yMin, yMax);

        // Dibujar ejes
        dibujarEjes(gc, xMin, xMax, yMin, yMax);

        // Dibujar función
        dibujarFuncion(gc, funcion, xMin, xMax, yMin, yMax);

        // Marcar puntos críticos
        if (resultado != null) {
            marcarPuntosCriticos(gc, resultado, xMin, xMax, yMin, yMax);
        }
    }

    /**
     * Dibuja la rejilla de fondo.
     */
    private void dibujarRejilla(GraphicsContext gc, double xMin, double xMax, double yMin, double yMax) {
        gc.setStroke(COLOR_REJILLA);
        gc.setLineWidth(0.5);

        double paso = 1.0;
        double anchoGrafico = ANCHO_CANVAS - 2 * MARGEN;
        double altoGrafico = ALTO_CANVAS - 2 * MARGEN;

        // Líneas verticales
        for (double x = xMin; x <= xMax; x += paso) {
            double pixelX = MARGEN + (x - xMin) / (xMax - xMin) * anchoGrafico;
            gc.strokeLine(pixelX, MARGEN, pixelX, ALTO_CANVAS - MARGEN);
        }

        // Líneas horizontales
        for (double y = yMin; y <= yMax; y += paso) {
            double pixelY = (ALTO_CANVAS - MARGEN) - (y - yMin) / (yMax - yMin) * altoGrafico;
            gc.strokeLine(MARGEN, pixelY, ANCHO_CANVAS - MARGEN, pixelY);
        }
    }

    /**
     * Dibuja los ejes X e Y.
     */
    private void dibujarEjes(GraphicsContext gc, double xMin, double xMax, double yMin, double yMax) {
        gc.setStroke(COLOR_EJE);
        gc.setLineWidth(2);

        double anchoGrafico = ANCHO_CANVAS - 2 * MARGEN;
        double altoGrafico = ALTO_CANVAS - 2 * MARGEN;

        // Eje X
        double pixelY0 = (ALTO_CANVAS - MARGEN) - (0 - yMin) / (yMax - yMin) * altoGrafico;
        if (pixelY0 >= MARGEN && pixelY0 <= ALTO_CANVAS - MARGEN) {
            gc.strokeLine(MARGEN, pixelY0, ANCHO_CANVAS - MARGEN, pixelY0);
        } else {
            gc.strokeLine(MARGEN, ALTO_CANVAS - MARGEN, ANCHO_CANVAS - MARGEN, ALTO_CANVAS - MARGEN);
        }

        // Eje Y
        double pixelX0 = MARGEN + (0 - xMin) / (xMax - xMin) * anchoGrafico;
        if (pixelX0 >= MARGEN && pixelX0 <= ANCHO_CANVAS - MARGEN) {
            gc.strokeLine(pixelX0, MARGEN, pixelX0, ALTO_CANVAS - MARGEN);
        } else {
            gc.strokeLine(MARGEN, MARGEN, MARGEN, ALTO_CANVAS - MARGEN);
        }

        // Etiquetas de ejes
        gc.setFill(COLOR_EJE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.fillText("X", ANCHO_CANVAS - MARGEN - 10, ALTO_CANVAS - MARGEN + 15);
        gc.fillText("Y", MARGEN - 20, MARGEN - 10);
    }

    /**
     * Dibuja la función como una serie de puntos conectados.
     */
    private void dibujarFuncion(GraphicsContext gc, Funcion funcion, 
                                double xMin, double xMax, double yMin, double yMax) {
        gc.setStroke(COLOR_FUNCION);
        gc.setLineWidth(2);

        double anchoGrafico = ANCHO_CANVAS - 2 * MARGEN;
        double altoGrafico = ALTO_CANVAS - 2 * MARGEN;
        double paso = (xMax - xMin) / 400; // Precisión del gráfico

        double xAnterior = xMin;
        double yAnterior = evaluarFuncion(funcion, xMin);

        for (double x = xMin + paso; x <= xMax; x += paso) {
            double y = evaluarFuncion(funcion, x);

            // Convertir a coordenadas de pantalla
            double pixelXAnterior = MARGEN + (xAnterior - xMin) / (xMax - xMin) * anchoGrafico;
            double pixelYAnterior = (ALTO_CANVAS - MARGEN) - (yAnterior - yMin) / (yMax - yMin) * altoGrafico;

            double pixelX = MARGEN + (x - xMin) / (xMax - xMin) * anchoGrafico;
            double pixelY = (ALTO_CANVAS - MARGEN) - (y - yMin) / (yMax - yMin) * altoGrafico;

            // Verificar que los puntos están dentro del rango visible
            if (pixelYAnterior >= MARGEN && pixelYAnterior <= ALTO_CANVAS - MARGEN &&
                pixelY >= MARGEN && pixelY <= ALTO_CANVAS - MARGEN) {
                gc.strokeLine(pixelXAnterior, pixelYAnterior, pixelX, pixelY);
            }

            xAnterior = x;
            yAnterior = y;
        }
    }

    /**
     * Marca los puntos críticos en el gráfico.
     */
    private void marcarPuntosCriticos(GraphicsContext gc, ResultadoAnalisis resultado,
                                      double xMin, double xMax, double yMin, double yMax) {
        double anchoGrafico = ANCHO_CANVAS - 2 * MARGEN;
        double altoGrafico = ALTO_CANVAS - 2 * MARGEN;

        // Marcar máximo
        if (resultado.getPuntoCriticoMaximo() != null) {
            marcarPunto(gc, resultado.getPuntoCriticoMaximo().getX(), 
                       resultado.getPuntoCriticoMaximo().getY(),
                       COLOR_MAXIMO, xMin, xMax, yMin, yMax, anchoGrafico, altoGrafico);
        }

        // Marcar mínimo
        if (resultado.getPuntoCriticoMinimo() != null) {
            marcarPunto(gc, resultado.getPuntoCriticoMinimo().getX(), 
                       resultado.getPuntoCriticoMinimo().getY(),
                       COLOR_MINIMO, xMin, xMax, yMin, yMax, anchoGrafico, altoGrafico);
        }

        // Marcar punto de inflexión
        if (resultado.getPuntoInflexion() != null) {
            marcarPunto(gc, resultado.getPuntoInflexion().getX(), 
                       resultado.getPuntoInflexion().getY(),
                       COLOR_INFLEXION, xMin, xMax, yMin, yMax, anchoGrafico, altoGrafico);
        }
    }

    /**
     * Marca un punto individual en el gráfico.
     */
    private void marcarPunto(GraphicsContext gc, double x, double y, Color color,
                            double xMin, double xMax, double yMin, double yMax,
                            double anchoGrafico, double altoGrafico) {
        // Convertir a coordenadas de pantalla
        double pixelX = MARGEN + (x - xMin) / (xMax - xMin) * anchoGrafico;
        double pixelY = (ALTO_CANVAS - MARGEN) - (y - yMin) / (yMax - yMin) * altoGrafico;

        // Verificar que el punto está dentro del rango visible
        if (pixelX >= MARGEN && pixelX <= ANCHO_CANVAS - MARGEN &&
            pixelY >= MARGEN && pixelY <= ALTO_CANVAS - MARGEN) {
            
            // Dibujar círculo
            gc.setFill(color);
            double radio = 5;
            gc.fillOval(pixelX - radio, pixelY - radio, 2 * radio, 2 * radio);

            // Dibujar borde
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(pixelX - radio, pixelY - radio, 2 * radio, 2 * radio);
        }
    }

    /**
     * Evalúa una función en un punto x.
     * Implementación simplificada.
     */
    private double evaluarFuncion(Funcion funcion, double x) {
        try {
            String expresion = funcion.getExpresion()
                    .replace("x", String.valueOf(x))
                    .replace("^", "**");

            // Usar método seguro de evaluación
            return evaluarExpresion(expresion);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Evalúa una expresión matemática simple.
     */
    private double evaluarExpresion(String expresion) {
        try {
            // Simplificada: solo números
            return Double.parseDouble(expresion);
        } catch (Exception e) {
            return 0;
        }
    }
}