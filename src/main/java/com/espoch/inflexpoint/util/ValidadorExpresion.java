package com.espoch.inflexpoint.util;

import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

/**
 * Utilidad para validar expresiones matemáticas antes de su evaluación.
 * Responsabilidades:
 * - Verificar paréntesis balanceados
 * - Validar caracteres permitidos
 * - Detectar sintaxis básica incorrecta
 */
public class ValidadorExpresion {

    // Caracteres permitidos en expresiones
    private static final String CARACTERES_PERMITIDOS = "0123456789+-*/^().abcdefghijklmnopqrstuvwxyz ";

    /**
     * Valida una expresión matemática.
     * 
     * @param expresion La expresión a validar
     * @throws ExpresionInvalidaException si la expresión es inválida
     */
    public static void validar(String expresion) throws ExpresionInvalidaException {
        if (expresion == null || expresion.trim().isEmpty()) { // Eliminar espacios en blanco al inicio y final
            throw new ExpresionInvalidaException("La expresión no puede estar vacía");
        }

        validarCaracteres(expresion);
        validarParentesis(expresion);
        validarSintaxisBasica(expresion);
        validarIndeterminacionGlobal(expresion);
    }

    /**
     * Verifica si la expresión es una indeterminación global (ej. 1/0, x/(x-x)).
     */
    private static void validarIndeterminacionGlobal(String expresion) throws ExpresionInvalidaException {
        try {
            com.espoch.inflexpoint.modelos.calculos.Evaluador eval = new com.espoch.inflexpoint.modelos.calculos.Evaluador(
                    expresion);
            double[] puntosPrueba = { 0.0, 1.0, -1.0, 0.5, Math.PI };
            boolean todosIndefinidos = true;
            boolean algunInfinito = false;
            boolean algunNaN = false;

            for (double x : puntosPrueba) {
                double val = eval.evaluar(x);
                if (Double.isFinite(val)) {
                    todosIndefinidos = false;
                    break;
                }
                if (Double.isInfinite(val))
                    algunInfinito = true;
                if (Double.isNaN(val))
                    algunNaN = true;
            }

            if (todosIndefinidos) {
                if (algunInfinito && !algunNaN) {
                    throw new ExpresionInvalidaException(
                            "Indeterminación: La expresión resulta en infinito para todo el dominio (posible división por cero)");
                } else {
                    throw new ExpresionInvalidaException(
                            "Indeterminación: La expresión no está definida en ningún punto (ej. división 0/0 o raíz negativa global)");
                }
            }
        } catch (ExpresionInvalidaException e) {
            // Si es un error de sintaxis que el evaluador ya captó, lo dejamos pasar para
            // que lo maneje el flujo normal
            if (e.getMessage().contains("Indeterminación"))
                throw e;
        } catch (Exception e) {
            // Ignorar errores durante la prueba numérica, el flujo principal los captará
        }
    }

    /**
     * Verifica que todos los caracteres sean permitidos.
     */
    private static void validarCaracteres(String expresion) throws ExpresionInvalidaException {
        String expr = expresion.toLowerCase().replace(" ", "");

        for (int i = 0; i < expr.length(); i++) {
            char caracter = expr.charAt(i);
            if (CARACTERES_PERMITIDOS.indexOf(caracter) == -1) { // Busca el caracter en el string
                throw new ExpresionInvalidaException("Carácter no permitido: '" + caracter + "' en posición " + i);
            }
        }
    }

    /**
     * Verifica que los paréntesis estén balanceados.
     */
    private static void validarParentesis(String expresion) throws ExpresionInvalidaException {
        int balanceParentesis = 0;

        for (int i = 0; i < expresion.length(); i++) {
            char caracter = expresion.charAt(i);
            if (caracter == '(') {
                balanceParentesis++;
            } else if (caracter == ')') {
                balanceParentesis--;
            }

            // Si balanceParentesis es negativo, hay más ')' que '('
            if (balanceParentesis < 0) {
                throw new ExpresionInvalidaException("Paréntesis de cierre sin apertura en posición " + i);
            }
        }

        // Muestra la cantidad de paréntesis faltantes
        if (balanceParentesis > 0) {
            throw new ExpresionInvalidaException(
                    "Faltan " + balanceParentesis + " paréntesis de cierre");
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
                if (!((actual == '+' || actual == '-') && (siguiente == '+' || siguiente == '-'))) {
                    // Permitir unarios al inicio (i=0) o después de otro operador si es +/-
                    if (i > 0 || !((actual == '+' || actual == '-'))) {
                        throw new ExpresionInvalidaException(
                                "Operadores consecutivos inválidos: '" + actual + siguiente + "' en posición " + i);
                    }
                }
            }
        }

        // Verificar que no termine con operador binario
        if (!expr.isEmpty()) {
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
