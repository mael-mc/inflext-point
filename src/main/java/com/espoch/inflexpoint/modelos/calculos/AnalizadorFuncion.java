package com.espoch.inflexpoint.modelos.calculos;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorFuncion {

    private static final double H = 0.0001; // Paso para derivadas
    private static final double MIN_X = -10.0;
    private static final double MAX_X = 10.0;
    private static final double STEP = 0.1;

    public ResultadoAnalisis analizar(String expresion,
            boolean calcPuntosCriticos,
            boolean calcIntervalos,
            boolean calcMaxMin,
            boolean calcInflexion,
            boolean calcConcavidad) {

        Evaluador evaluador = new Evaluador(expresion);
        StringBuilder resumen = new StringBuilder();
        resumen.append("Análisis de la función: f(x) = ").append(expresion).append("\n\n");

        List<Double> criticalPoints = new ArrayList<>();
        List<Double> inflectionPoints = new ArrayList<>();

        // Escaneo para encontrar puntos
        double prevDeriv = derivada(evaluador, MIN_X);
        double prevSecondDeriv = segundaDerivada(evaluador, MIN_X);

        for (double x = MIN_X + STEP; x <= MAX_X; x += STEP) {
            double currentDeriv = derivada(evaluador, x);
            double currentSecondDeriv = segundaDerivada(evaluador, x);

            // Detectar cambio de signo en primera derivada (Puntos Críticos / Max / Min)
            if (Math.signum(currentDeriv) != Math.signum(prevDeriv)) {
                // Refinar búsqueda (bisección simple)
                double root = biseccionDerivada(evaluador, x - STEP, x);
                if (!Double.isNaN(root)) {
                    criticalPoints.add(root);
                }
            }

            // Detectar cambio de signo en segunda derivada (Puntos Inflexión)
            if (Math.signum(currentSecondDeriv) != Math.signum(prevSecondDeriv)) {
                double root = biseccionSegundaDerivada(evaluador, x - STEP, x);
                if (!Double.isNaN(root)) {
                    inflectionPoints.add(root);
                }
            }

            prevDeriv = currentDeriv;
            prevSecondDeriv = currentSecondDeriv;
        }

        // Generar Reporte
        try {
            if (calcPuntosCriticos) {
                resumen.append("--- Puntos Críticos (f'(x)=0) ---\n");
                if (criticalPoints.isEmpty())
                    resumen.append("No se encontraron en el rango [-10, 10].\n");
                for (Double p : criticalPoints) {
                    resumen.append(String.format("x ≈ %.4f, y ≈ %.4f\n", p, evaluador.evaluar(p)));
                }
                resumen.append("\n");
            }

            if (calcMaxMin) {
                resumen.append("--- Máximos y Mínimos ---\n");
                if (criticalPoints.isEmpty())
                    resumen.append("No se encontraron en el rango [-10, 10].\n");
                for (Double p : criticalPoints) {
                    double second = segundaDerivada(evaluador, p);
                    String tipo = (second > 0) ? "Mínimo" : (second < 0) ? "Máximo" : "Punto silla/Indeterminado";
                    resumen.append(String.format("x ≈ %.4f -> %s\n", p, tipo));
                }
                resumen.append("\n");
            }

            if (calcIntervalos) {
                resumen.append("--- Intervalos de Monotonía ---\n");
                // Simplificado: Evaluar en puntos medios entre críticos
                List<Double> points = new ArrayList<>();
                points.add(MIN_X);
                points.addAll(criticalPoints);
                points.add(MAX_X);
                for (int i = 0; i < points.size() - 1; i++) {
                    double mid = (points.get(i) + points.get(i + 1)) / 2;
                    double d = derivada(evaluador, mid);
                    String estado = (d > 0) ? "Creciente" : "Decreciente";
                    resumen.append(String.format("(%.2f, %.2f) -> %s\n", points.get(i), points.get(i + 1), estado));
                }
                resumen.append("\n");
            }

            if (calcInflexion) {
                resumen.append("--- Puntos de Inflexión (f''(x)=0) ---\n");
                if (inflectionPoints.isEmpty())
                    resumen.append("No se encontraron en el rango [-10, 10].\n");
                for (Double p : inflectionPoints) {
                    resumen.append(String.format("x ≈ %.4f, y ≈ %.4f\n", p, evaluador.evaluar(p)));
                }
                resumen.append("\n");
            }

            if (calcConcavidad) {
                resumen.append("--- Concavidad ---\n");
                List<Double> points = new ArrayList<>();
                points.add(MIN_X);
                points.addAll(inflectionPoints);
                points.add(MAX_X);
                for (int i = 0; i < points.size() - 1; i++) {
                    double mid = (points.get(i) + points.get(i + 1)) / 2;
                    double sd = segundaDerivada(evaluador, mid);
                    String concavidad = (sd > 0) ? "Cóncava Hacia Arriba (Convexa)" : "Cóncava Hacia Abajo";
                    resumen.append(String.format("(%.2f, %.2f) -> %s\n", points.get(i), points.get(i + 1), concavidad));
                }
                resumen.append("\n");
            }

        } catch (Exception e) {
            resumen.append("\nError durante el análisis numérico: ").append(e.getMessage());
        }

        return new ResultadoAnalisis(resumen.toString());
    }

    // Métodos Auxiliares Numéricos
    private double derivada(Evaluador f, double x) {
        return (f.evaluar(x + H) - f.evaluar(x - H)) / (2 * H);
    }

    private double segundaDerivada(Evaluador f, double x) {
        return (f.evaluar(x + H) - 2 * f.evaluar(x) + f.evaluar(x - H)) / (H * H);
    }

    private double biseccionDerivada(Evaluador f, double a, double b) {
        double fa = derivada(f, a);
        double fb = derivada(f, b);
        if (fa * fb >= 0)
            return Double.NaN;

        double c = a;
        for (int i = 0; i < 50; i++) {
            c = (a + b) / 2;
            double fc = derivada(f, c);
            if (Math.abs(fc) < 1e-6)
                return c;
            if (fa * fc < 0)
                b = c;
            else
                a = c;
        }
        return c;
    }

    private double biseccionSegundaDerivada(Evaluador f, double a, double b) {
        double fa = segundaDerivada(f, a);
        double fb = segundaDerivada(f, b);
        if (fa * fb >= 0)
            return Double.NaN;

        double c = a;
        for (int i = 0; i < 50; i++) {
            c = (a + b) / 2;
            double fc = segundaDerivada(f, c);
            if (Math.abs(fc) < 1e-6)
                return c;
            if (fa * fc < 0)
                b = c;
            else
                a = c;
        }
        return c;
    }
}
