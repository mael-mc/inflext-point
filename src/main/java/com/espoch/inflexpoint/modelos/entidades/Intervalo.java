package com.espoch.inflexpoint.modelos.entidades;

import com.espoch.inflexpoint.modelos.enumeraciones.TipoIntervalo;

public class Intervalo {
    // Atributos
    private Double inicio;
    private Double fin;
    private TipoIntervalo  tipoIntervalo;

    // Constructor
    public Intervalo(Double inicio, Double fin, TipoIntervalo tipoIntervalo) {
        this.inicio = inicio;
        this.fin = fin;
        this.tipoIntervalo = tipoIntervalo;
    }

    // Getters y Setters
    public Double getInicio() {
        return inicio;
    }

    public void setInicio(Double inicio) {
        this.inicio = inicio;
    }

    public Double getFin() {
        return fin;
    }

    public void setFin(Double fin) {
        this.fin = fin;
    }

    public TipoIntervalo getTipoIntervalo() {
        return tipoIntervalo;
    }

    public void setTipoIntervalo(TipoIntervalo tipoIntervalo) {
        this.tipoIntervalo = tipoIntervalo;
    }

}
