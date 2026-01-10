package com.espoch.inflexpoint.modelos.calculos;

import com.espoch.inflexpoint.modelos.entidades.Intervalo;
import com.espoch.inflexpoint.modelos.entidades.PuntoCritico;

public class ResultadoAnalisis {

    // Puntos críticos (máximos y mínimos)
    private PuntoCritico[] puntosCriticos;

    // Puntos de inflexión
    private PuntoCritico[] puntosInflexion;

    // Intervalos de crecimiento
    private Intervalo[] intervalosCrecimiento;

    // Intervalos de decrecimiento
    private Intervalo[] intervalosDecrecimiento;

    // Intervalos de concavidad
    private Intervalo[] intervalosConcavidad;

    // Derivadas (representación textual)
    private String primeraDerivada;
    private String segundaDerivada;

    // Constructor completo.

    public ResultadoAnalisis(
            PuntoCritico[] puntosCriticos,
            PuntoCritico[] puntosInflexion,
            Intervalo[] intervalosCrecimiento,
            Intervalo[] intervalosDecrecimiento,
            Intervalo[] intervalosConcavidad,
            String primeraDerivada,
            String segundaDerivada) {

        this.puntosCriticos = puntosCriticos;
        this.puntosInflexion = puntosInflexion;
        this.intervalosCrecimiento = intervalosCrecimiento;
        this.intervalosDecrecimiento = intervalosDecrecimiento;
        this.intervalosConcavidad = intervalosConcavidad;
        this.primeraDerivada = primeraDerivada;
        this.segundaDerivada = segundaDerivada;
    }

    /**
     * Constructor simplificado para retrocompatibilidad.
     * Crea un resultado vacío con solo el resumen.
     * 
     * @deprecated Usar el constructor completo con entidades
     */
    @Deprecated
    public ResultadoAnalisis(String resumen) {
        this.puntosCriticos = new PuntoCritico[0];
        this.puntosInflexion = new PuntoCritico[0];
        this.intervalosCrecimiento = new Intervalo[0];
        this.intervalosDecrecimiento = new Intervalo[0];
        this.intervalosConcavidad = new Intervalo[0];
        this.primeraDerivada = "";
        this.segundaDerivada = "";
    }

    // Getters

    public PuntoCritico[] getPuntosCriticos() {
        return puntosCriticos;
    }

    public PuntoCritico[] getPuntosInflexion() {
        return puntosInflexion;
    }

    public Intervalo[] getIntervalosCrecimiento() {
        return intervalosCrecimiento;
    }

    public Intervalo[] getIntervalosDecrecimiento() {
        return intervalosDecrecimiento;
    }

    public Intervalo[] intervalosConcavidad() {
        return intervalosConcavidad;
    }

    public String getPrimeraDerivada() {
        return primeraDerivada;
    }

    public String getSegundaDerivada() {
        return segundaDerivada;
    }

    // Setters

    public void setPuntosCriticos(PuntoCritico[] puntosCriticos) {
        this.puntosCriticos = puntosCriticos;
    }

    public void setPuntosInflexion(PuntoCritico[] puntosInflexion) {
        this.puntosInflexion = puntosInflexion;
    }

    public void setIntervalosCrecimiento(Intervalo[] intervalosCrecimiento) {
        this.intervalosCrecimiento = intervalosCrecimiento;
    }

    public void setIntervalosDecrecimiento(Intervalo[] intervalosDecrecimiento) {
        this.intervalosDecrecimiento = intervalosDecrecimiento;
    }

    public void setIntervalosConcavidad(Intervalo[] intervalosConcavidad) {
        this.intervalosConcavidad = intervalosConcavidad;
    }

    public void setPrimeraDerivada(String primeraDerivada) {
        this.primeraDerivada = primeraDerivada;
    }

    public void setSegundaDerivada(String segundaDerivada) {
        this.segundaDerivada = segundaDerivada;
    }

    // Genera un resumen textual completo del análisis.
    public String generarResumen() {
        StringBuilder resumen = new StringBuilder();

        // Puntos críticos
        if (puntosCriticos != null && puntosCriticos.length > 0) {
            resumen.append("--- Puntos Críticos ---\n");
            for (PuntoCritico pc : puntosCriticos) {
                resumen.append(String.format("  %s en (%.4f, %.4f)\n",
                        pc.getTipoPuntoCritico(), pc.getX(), pc.getY()));
            }
            resumen.append("\n");
        }

        // Puntos de inflexión
        if (puntosInflexion != null && puntosInflexion.length > 0) {
            resumen.append("--- Puntos de Inflexión ---\n");
            for (PuntoCritico pi : puntosInflexion) {
                resumen.append(String.format("  (%.4f, %.4f)\n",
                        pi.getX(), pi.getY()));
            }
            resumen.append("\n");
        }

        // Intervalos de crecimiento
        if (intervalosCrecimiento != null && intervalosCrecimiento.length > 0) {
            resumen.append("--- Intervalos de Crecimiento ---\n");
            for (Intervalo intervalo : intervalosCrecimiento) {
                resumen.append(formatearIntervalo(intervalo)).append("\n");
            }
            resumen.append("\n");
        }

        // Intervalos de decrecimiento
        if (intervalosDecrecimiento != null && intervalosDecrecimiento.length > 0) {
            resumen.append("--- Intervalos de Decrecimiento ---\n");
            for (Intervalo intervalo : intervalosDecrecimiento) {
                resumen.append(formatearIntervalo(intervalo)).append("\n");
            }
            resumen.append("\n");
        }

        // Intervalos de concavidad
        if (intervalosConcavidad != null && intervalosConcavidad.length > 0) {
            resumen.append("--- Concavidad ---\n");
            for (Intervalo intervalo : intervalosConcavidad) {
                resumen.append(formatearIntervalo(intervalo)).append("\n");
            }
            resumen.append("\n");
        }

        // Derivadas
        if (primeraDerivada != null && !primeraDerivada.isEmpty()) {
            resumen.append("--- Derivadas ---\n");
            resumen.append("f'(x) = ").append(primeraDerivada).append("\n");
            if (segundaDerivada != null && !segundaDerivada.isEmpty()) {
                resumen.append("f''(x) = ").append(segundaDerivada).append("\n");
            }
            resumen.append("\n");
        }

        if (resumen.length() == 0) {
            return "No se encontraron resultados para mostrar.";
        }

        return resumen.toString();
    }

    /**
     * Formatea un intervalo para visualización.
     */
    private String formatearIntervalo(Intervalo intervalo) {
        String inicio = intervalo.getInicio() == null ? "-∞" : String.format("%.2f", intervalo.getInicio());
        String fin = intervalo.getFin() == null ? "∞" : String.format("%.2f", intervalo.getFin());

        return String.format("  (%s, %s) → %s", inicio, fin, intervalo.getTipoIntervalo());
    }

    // Verifica si hay algún resultado calculado.
    public boolean tieneResultados() {
        return (puntosCriticos != null && puntosCriticos.length > 0) ||
                (puntosInflexion != null && puntosInflexion.length > 0) ||
                (intervalosCrecimiento != null && intervalosCrecimiento.length > 0) ||
                (intervalosDecrecimiento != null && intervalosDecrecimiento.length > 0) ||
                (intervalosConcavidad != null && intervalosConcavidad.length > 0);
    }
}
