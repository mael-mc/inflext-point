package com.espoch.inflexpoint.modelos.excepciones;

/**
 * Excepción lanzada cuando ocurre un error durante el análisis numérico.
 * Ejemplos de casos:
 * - División por cero durante evaluación
 * - Logaritmo de número negativo
 * - Raíz cuadrada de número negativo
 */
public class CalculoNumericoException extends Exception {
    /**
     * Crea una nueva excepción con el mensaje especificado.
     * 
     * @param mensaje Descripción del error numérico
     */
    // Constructor
    public CalculoNumericoException(String mensaje) {
        super(mensaje);
    }

    public CalculoNumericoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
