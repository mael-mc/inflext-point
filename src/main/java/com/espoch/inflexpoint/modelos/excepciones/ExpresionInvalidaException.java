package com.espoch.inflexpoint.modelos.excepciones;

/**
 * Excepción lanzada cuando una expresión matemática no puede ser parseada o
 * evaluada.
 * 
 * Ejemplos de casos:
 * - Paréntesis desbalanceados: "sin(x"
 * - Caracteres inválidos: "x + @ 2"
 * - Sintaxis incorrecta: "x ++ 2"
 */
public class ExpresionInvalidaException extends Exception {

    /**
     * Crea una nueva excepción con el mensaje especificado.
     * 
     * @param mensaje Descripción del error en la expresión
     */
    public ExpresionInvalidaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una nueva excepción con mensaje y causa raíz.
     * 
     * @param mensaje Descripción del error
     * @param causa   Excepción original que causó este error
     */
    public ExpresionInvalidaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
