package com.espoch.inflexpoint.util;

import com.espoch.inflexpoint.modelos.calculos.Evaluador;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.entidades.PuntoCritico;
import com.espoch.inflexpoint.modelos.enumeraciones.TipoPuntoCritico;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class GraficadorFuncion {

    // Límite de valores para evitar gráficas distorsionadas
    private static final double MAX_Y_VALUE = 100;
    private static final double MIN_Y_VALUE = -100;

    /**
     * Crea una gráfica completa de una función con sus puntos críticos.
     *
     * @param expresion La expresión matemática a graficar
     * @param resultado Los resultados del análisis (puntos críticos, inflexión)
     * @param minX      Límite inferior del dominio
     * @param maxX      Límite superior del dominio
     * @return LineChart configurado y listo para mostrar
     * @throws ExpresionInvalidaException si la expresión no puede ser evaluada
     */
    public LineChart<Number, Number> crearGrafica(
            String expresion,
            ResultadoAnalisis resultado,
            double minX,
            double maxX) throws ExpresionInvalidaException {

        // Crear ejes
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("x");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(minX);
        xAxis.setUpperBound(maxX);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("f(x)");

        // Crear gráfica
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("f(x) = " + expresion);
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);

        // Añadir serie de datos de la función
        XYChart.Series<Number, Number> serieFuncion = crearSerieFuncion(
                expresion, minX, maxX, 0.1);
        lineChart.getData().add(serieFuncion);

        // Añadir puntos críticos si existen
        if (resultado != null) {
            addPuntosCriticos(lineChart, resultado);
        }

        return lineChart;
    }

    // Crea una serie de datos evaluando la función en un rango.
    private XYChart.Series<Number, Number> crearSerieFuncion(
            String expresion, double minX, double maxX, double step)
            throws ExpresionInvalidaException {

        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName(expresion);

        Evaluador evaluador = new Evaluador(expresion);

        for (double x = minX; x <= maxX; x += step) {
            try {
                double y = evaluador.evaluar(x);

                // Filtrar valores inválidos o muy grandes
                if (!Double.isNaN(y) && !Double.isInfinite(y) &&
                        y >= MIN_Y_VALUE && y <= MAX_Y_VALUE) {
                    serie.getData().add(new XYChart.Data<>(x, y));
                }
            } catch (ExpresionInvalidaException e) {
                // Ignorar puntos que no se pueden evaluar
            }
        }

        return serie;
    }

    // Añade marcadores visuales para puntos críticos.
    private void addPuntosCriticos(
            LineChart<Number, Number> lineChart,
            ResultadoAnalisis resultado) {

        // Series para diferentes tipos de puntos
        XYChart.Series<Number, Number> serieMaximos = new XYChart.Series<>();
        serieMaximos.setName("Máximos");

        XYChart.Series<Number, Number> serieMinimos = new XYChart.Series<>();
        serieMinimos.setName("Mínimos");

        XYChart.Series<Number, Number> serieInflexion = new XYChart.Series<>();
        serieInflexion.setName("Inflexión");

        // Añadir puntos críticos
        if (resultado.getPuntosCriticos() != null) {
            for (PuntoCritico pc : resultado.getPuntosCriticos()) {
                if (pc.getTipoPuntoCritico() == TipoPuntoCritico.MAXIMO) {
                    serieMaximos.getData().add(new XYChart.Data<>(pc.getX(), pc.getY()));
                } else if (pc.getTipoPuntoCritico() == TipoPuntoCritico.MINIMO) {
                    serieMinimos.getData().add(new XYChart.Data<>(pc.getX(), pc.getY()));
                }
            }
        }

        // Añadir puntos de inflexión
        if (resultado.getPuntosInflexion() != null) {
            for (PuntoCritico pi : resultado.getPuntosInflexion()) {
                serieInflexion.getData().add(new XYChart.Data<>(pi.getX(), pi.getY()));
            }
        }

        // Añadir series con datos
        if (!serieMaximos.getData().isEmpty()) {
            lineChart.getData().add(serieMaximos);
            aplicarEstiloPuntos(serieMaximos, "red");
        }

        if (!serieMinimos.getData().isEmpty()) {
            lineChart.getData().add(serieMinimos);
            aplicarEstiloPuntos(serieMinimos, "green");
        }

        if (!serieInflexion.getData().isEmpty()) {
            lineChart.getData().add(serieInflexion);
            aplicarEstiloPuntos(serieInflexion, "orange");
        }
    }

    private void aplicarEstiloPuntos(
            XYChart.Series<Number, Number> serie, String colorName) {

    }

    /**
     * Crea una gráfica simple sin análisis.
     *
     * @param expresion La expresión a graficar
     * @param minX      Límite inferior
     * @param maxX      Límite superior
     * @return LineChart con la función graficada
     * @throws ExpresionInvalidaException si hay error en la expresión
     */
    public LineChart<Number, Number> crearGraficaSimple(
            String expresion, double minX, double maxX)
            throws ExpresionInvalidaException {

        return crearGrafica(expresion, null, minX, maxX);
    }
}