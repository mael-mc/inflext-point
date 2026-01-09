package com.espoch.inflexpoint.modelos.dao.interfaces;

import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;
import com.espoch.inflexpoint.modelos.entidades.Funcion;
import com.espoch.inflexpoint.modelos.excepciones.CalculoNumericoException;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

/**
 * Interfaz DAO para operaciones con funciones matemáticas.
 * 
 * Define el contrato para el análisis de funciones.
 */
public interface IFuncion {

    /**
     * Analiza una función matemática calculando los elementos solicitados.
     * 
     * @param funcion            La función a analizar
     * @param calcPuntosCriticos true para calcular puntos críticos
     * @param calcIntervalos     true para calcular intervalos de monotonía
     * @param calcMaxMin         true para clasificar máximos y mínimos
     * @param calcInflexion      true para calcular puntos de inflexión
     * @param calcConcavidad     true para calcular intervalos de concavidad
     * @return ResultadoAnalisis con las entidades calculadas
     * @throws ExpresionInvalidaException si la expresión de la función es inválida
     * @throws CalculoNumericoException   si ocurre un error durante los cálculos
     *                                    numéricos
     */
    ResultadoAnalisis analizar(
            Funcion funcion,
            boolean calcPuntosCriticos,
            boolean calcIntervalos,
            boolean calcMaxMin,
            boolean calcInflexion,
            boolean calcConcavidad)
            throws ExpresionInvalidaException, CalculoNumericoException;
}
