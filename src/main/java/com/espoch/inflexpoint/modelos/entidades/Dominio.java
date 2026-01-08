package com.espoch.inflexpoint.modelos.entidades;

public class Dominio {
    // Atributos
    private Double desde;
    private Double hasta;

    // Constructor
    public Dominio(Double desde, Double hasta) {
        this.desde = desde;
        this.hasta = hasta;
    }

    // Getters y Setters
    public Double getDesde() {
        return desde;
    }

    public void setDesde(Double desde) {
        this.desde = desde;
    }

    public Double getHasta() {
        return hasta;
    }

    public void setHasta(Double hasta) {
        this.hasta = hasta;
    }

}
