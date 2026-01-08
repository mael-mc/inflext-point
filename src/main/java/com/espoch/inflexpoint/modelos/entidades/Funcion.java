package com.espoch.inflexpoint.modelos.entidades;

import com.espoch.inflexpoint.modelos.enumeraciones.TipoFuncion;

public class Funcion {
    // Atributos
    private int idFuncion;
    private String expresion;
    private String variable;
    private TipoFuncion tipoFuncion;
    private Dominio dominioFuncion;

    // Constructor
    public Funcion(int idFuncion, String expresion, String variable, TipoFuncion tipoFuncion, Dominio dominioFuncion) {
        this.idFuncion = idFuncion;
        this.expresion = expresion;
        this.variable = variable;
        this.tipoFuncion = tipoFuncion;
        this.dominioFuncion = dominioFuncion;
    }

    // Getters y Setters
    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public String getExpresion() {
        return expresion;
    }

    public void setExpresion(String expresion) {
        this.expresion = expresion;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public TipoFuncion getTipoFuncion() {
        return tipoFuncion;
    }

    public void setTipoFuncion(TipoFuncion tipoFuncion) {
        this.tipoFuncion = tipoFuncion;
    }

    public Dominio getDominioFuncion() {
        return dominioFuncion;
    }

    public void setDominioFuncion(Dominio dominioFuncion) {
        this.dominioFuncion = dominioFuncion;
    }

}
