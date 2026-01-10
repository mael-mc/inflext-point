package com.espoch.inflexpoint.modelos.dao.implementaciones;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.dao.interfaces.IFuncion;
import com.espoch.inflexpoint.modelos.entidades.Funcion;
import com.espoch.inflexpoint.modelos.excepciones.CalculoNumericoException;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

/**
 * Implementación del DAO para funciones matemáticas.
 * RESPONSABILIDADES:
 * 1. Validar entidad Funcion antes de procesarla
 * 2. Delegar análisis al servicio AnalizadorFuncion
 * 3. Manejar excepciones del servicio de manera apropiada
 * 4. Actuar como intermediario entre controlador y servicio
 */
public class FuncionImpl implements IFuncion {

    private final AnalizadorFuncion analizador;

    // Constructor
    public FuncionImpl() {
        this.analizador = new AnalizadorFuncion();
    }


    /**
     * Analiza una función matemática.
     *
     * @param funcion            La función a analizar
     * @param calcPuntosCriticos true para calcular puntos críticos
     * @param calcIntervalos     true para calcular intervalos
     * @param calcMaxMin         true para calcular máximos y mínimos
     * @param calcInflexion      true para calcular puntos de inflexión
     * @param calcConcavidad     true para calcular concavidad
     * @return ResultadoAnalisis con los cálculos
     * @throws ExpresionInvalidaException si la expresión de la función es inválida
     * @throws CalculoNumericoException   si hay errores en los cálculos
     */
    // Función analizar
    @Override
    public ResultadoAnalisis analizar(
            Funcion funcion,
            boolean calcPuntosCriticos,
            boolean calcIntervalos,
            boolean calcMaxMin,
            boolean calcInflexion,
            boolean calcConcavidad)
            throws ExpresionInvalidaException, CalculoNumericoException {

        // Validar que la función no sea nula
        if (funcion == null) {
            throw new IllegalArgumentException("La función no puede ser nula");
        }

        // Validar que tenga expresión
        if (funcion.getExpresion() == null || funcion.getExpresion().trim().isEmpty()) {
            throw new ExpresionInvalidaException("La función debe tener una expresión válida");
        }

        // Validar dominio si existe
        if (funcion.getDominioFuncion() != null) {
            double inicio = funcion.getDominioFuncion().getDesde();
            double fin = funcion.getDominioFuncion().getHasta();

            if (inicio >= fin) {
                throw new IllegalArgumentException("El dominio es inválido: inicio debe ser menor que fin");
            }

            // Analizar en el dominio especificado
            return analizador.analizarEnRango(
                    funcion.getExpresion(),
                    inicio,
                    fin,
                    0.1, // step por defecto
                    calcPuntosCriticos,
                    calcIntervalos,
                    calcMaxMin,
                    calcInflexion,
                    calcConcavidad);
        } else {
            // Analizar con dominio por defecto [-10, 10]
            return analizador.analizar(
                    funcion.getExpresion(),
                    calcPuntosCriticos,
                    calcIntervalos,
                    calcMaxMin,
                    calcInflexion,
                    calcConcavidad);
        }
    }
}
