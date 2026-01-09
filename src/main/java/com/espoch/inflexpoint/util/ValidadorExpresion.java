package com.espoch.inflexpoint.util;

import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

/**
 * Utilidad para validar expresiones matemáticas antes de su evaluación.
 * 
 * Responsabilidades:
 * - Verificar paréntesis balanceados
 * - Validar caracteres permitidos
 * - Detectar sintaxis básica incorrecta
 */
public class ValidadorExpresion {

    // Caracteres permitidos en expresiones
    private static final String CARACTERES_PERMITIDOS = "0123456789+-*/^().xsincogtanlreqpabdfu ";

    /**
     * Valida una expresión matemática.
     * 
     * @param expresion La expresión a validar
     * @throws ExpresionInvalidaException si la expresión es inválida
     */
    public static void validar(String expresion) throws ExpresionInvalidaException {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new ExpresionInvalidaException("La expresión no puede estar vacía");
        }

        validarCaracteres(expresion);
        validarParentesis(expresion);
        validarSintaxisBasica(expresion);
    }

    /**
     * Verifica que todos los caracteres sean permitidos.
     */
    private static void validarCaracteres(String expresion) throws ExpresionInvalidaException {
        String expr = expresion.toLowerCase().replace(" ", "");

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (CARACTERES_PERMITIDOS.indexOf(c) == -1) {
                throw new ExpresionInvalidaException(
                        "Carácter no permitido: '" + c + "' en posición " + i);
            }
        }
    }

    /**
     * Verifica que los paréntesis estén balanceados.
     */
    private static void validarParentesis(String expresion) throws ExpresionInvalidaException {
        int balance = 0;

        for (int i = 0; i < expresion.length(); i++) {
            char c = expresion.charAt(i);
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }

            // Si balance es negativo, hay más ')' que '('
            if (balance < 0) {
                throw new ExpresionInvalidaException(
                        "Paréntesis de cierre sin apertura en posición " + i);
            }
        }

        if (balance > 0) {
            throw new ExpresionInvalidaException(
                    "Faltan " + balance + " paréntesis de cierre");
        }
    }

    /**
     * Valida sintaxis básica (operadores consecutivos, etc).
     */
    private static void validarSintaxisBasica(String expresion) throws ExpresionInvalidaException {
        String expr = expresion.replace(" ", "");

        // Verificar operadores consecutivos (excepto +- y -+)
        for (int i = 0; i < expr.length() - 1; i++) {
            char actual = expr.charAt(i);
            char siguiente = expr.charAt(i + 1);

            // Operadores binarios consecutivos inválidos
            if (esOperadorBinario(actual) && esOperadorBinario(siguiente)) {
                // Permitir casos como "+-" o "-+" que son válidos
                if (!((actual == '+' || actual == '-') && (siguiente == '+' || siguiente == '−'))) {
                    // Excluir casos donde uno es unario
                    if (i > 0 || !((actual == '+' || actual == '-'))) {
                        throw new ExpresionInvalidaException(
                                "Operadores consecutivos inválidos: '" + actual + siguiente +
                                        "' en posición " + i);
                    }
                }
            }
        }

        // Verificar que no termine con operador binario
        if (expr.length() > 0) {
            char ultimo = expr.charAt(expr.length() - 1);
            if (esOperadorBinario(ultimo) && ultimo != ')') {
                throw new ExpresionInvalidaException(
                        "La expresión no puede terminar con operador: '" + ultimo + "'");
            }
        }
    }

    /**
     * Verifica si un carácter es un operador binario.
     */
    private static boolean esOperadorBinario(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }
}
