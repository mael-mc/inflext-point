package com.espoch.inflexpoint.modelos.entidades;

import com.espoch.inflexpoint.modelos.enumeraciones.TipoPuntoCritico;

public class PuntoCritico extends Punto {
    // Atributos
    private final TipoPuntoCritico tipoPuntoCritico;

    // Constructor
    public PuntoCritico(double x, double y, TipoPuntoCritico tipoPuntoCritico) {
        super(x, y);
        this.tipoPuntoCritico = tipoPuntoCritico;
    }

    // Getter
    public TipoPuntoCritico getTipoPuntoCritico() {
        return tipoPuntoCritico;
    }

    @Override
    public String toString() {
        return "PuntoCritico{" +
                "tipoPuntoCritico=" + tipoPuntoCritico +
                '}';
    }
}
