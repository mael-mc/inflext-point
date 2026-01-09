package com.espoch.inflexpoint.modelos.calculos;

import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

/**
 * Clase auxiliar para evaluar expresiones matemáticas.
 * Implementa un parser recursivo simple.
 */
public class Evaluador {

    private String expression;
    private int pos = -1, ch;

    public Evaluador(String expression) throws ExpresionInvalidaException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new ExpresionInvalidaException("La expresión no puede estar vacía");
        }
        this.expression = normalize(expression);
    }

    public double evaluar(double x) throws ExpresionInvalidaException {
        this.pos = -1;
        nextChar();
        double res = parseExpression(x);
        if (pos < expression.length()) {
            throw new ExpresionInvalidaException("Carácter inesperado: " + (char) ch);
        }
        return res;
    }

    private String normalize(String expr) {
        if (expr == null)
            return "";

        // 1. Limpieza inicial
        String norm = expr.trim().toLowerCase()
                .replace(" ", "")
                .replace("²", "^2")
                .replace("³", "^3");

        // 2. Protección de tokens (funciones y constantes)
        // Usamos marcadores ASCII "TK#"
        String[] tokens = { "sqrt", "sin", "cos", "tan", "log", "ln", "abs", "exp", "pi", "e" };
        String[] placeholders = { "TK0", "TK1", "TK2", "TK3", "TK4", "TK5", "TK6", "TK7", "TK8", "TK9" };

        for (int i = 0; i < tokens.length; i++) {
            norm = norm.replace(tokens[i], placeholders[i]);
        }

        // 3. Inserción de multiplicación implícita (*)
        // Reglas:
        // - Digito seguido de Letra, '(', o Token
        // - Letra (x) seguido de Digito, Letra, '(', o Token (excluyendo x seguido de
        // token, pero x es variable única aqui y tokens están protegidos)
        // - ')' seguido de Digito, Letra, '(', o Token

        // Helpers regex:
        // Digitos: \\d
        // Letra (variable x): x
        // Token Start: T (de TK#)
        // Abre Parentesis: \\(

        // 3a. Digito seguido de (x, (, T)
        norm = norm.replaceAll("(?<=\\d)(?=[x\\(T])", "*");

        // 3b. ')' seguido de (Digito, x, (, T)
        norm = norm.replaceAll("(?<=\\))(?=[\\dx\\(T])", "*");

        // 3c. 'x' seguido de (Digito, x, (, T)
        norm = norm.replaceAll("(?<=x)(?=[\\dx\\(T])", "*");

        // 3d. Constantes 'e' y 'pi' (ahora son TK8 y TK9) seguidas de (Digito, x, (, T)
        // Los Tokens terminan en digito 0-9.
        // Ej: pi(x) -> TK8(x). No queremos TK8*(x) si es funcion, pero pi es constante.
        // Diferenciemos funciones de constantes si es posible.
        // sqrt(TK0) no debe llevar * después.
        // pi(TK8) y e(TK9) SÍ pueden llevar.
        // Pero en los placeholders, ambos son TK#.

        // Vamos a ser más específicos con las constantes.
        // pi -> TK8, e -> TK9.
        norm = norm.replaceAll("(?<=TK[89])(?=[\\dx\\(T])", "*");

        // 4. Restauración de tokens
        for (int i = 0; i < tokens.length; i++) {
            norm = norm.replace(placeholders[i], tokens[i]);
        }

        return norm;
    }

    private void nextChar() {
        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ')
            nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private double parseExpression(double x) throws ExpresionInvalidaException {
        double v = parseTerm(x);
        for (;;) {
            if (eat('+'))
                v += parseTerm(x); // suma
            else if (eat('-'))
                v -= parseTerm(x); // resta
            else
                return v;
        }
    }

    private double parseTerm(double x) throws ExpresionInvalidaException {
        double v = parseFactor(x);
        for (;;) {
            if (eat('*'))
                v *= parseFactor(x); // multiplicación
            else if (eat('/'))
                v /= parseFactor(x); // división
            else
                return v;
        }
    }

    private double parseFactor(double x) throws ExpresionInvalidaException {
        if (eat('+'))
            return parseFactor(x); // unario más
        if (eat('-'))
            return -parseFactor(x); // unario menos

        double v;
        int startPos = this.pos;
        if (eat('(')) { // paréntesis
            v = parseExpression(x);
            eat(')');
        } else if (ch == 'x' || ch == 'X') { // variable literal
            nextChar();
            v = x;
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // números
            while ((ch >= '0' && ch <= '9') || ch == '.')
                nextChar();
            v = Double.parseDouble(expression.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') { // funciones
            while (ch >= 'a' && ch <= 'z')
                nextChar();
            String func = expression.substring(startPos, this.pos);
            if (eat('(')) {
                v = parseExpression(x);
                eat(')');
            } else if (func.equals("e")) {
                v = Math.E;
            } else if (func.equals("pi")) {
                v = Math.PI;
            } else {
                // Si no hay paréntesis, asumimos que es una variable si es 'x', pero ya lo
                // manejamos arriba.
                // Si llegamos aquí es una funcion mal formada o variable desconocida.
                throw new ExpresionInvalidaException("Función desconocida: " + func);
            }

            if (func.equals("sqrt"))
                v = Math.sqrt(v);
            else if (func.equals("sin"))
                v = Math.sin(v);
            else if (func.equals("cos"))
                v = Math.cos(v);
            else if (func.equals("tan"))
                v = Math.tan(v);
            else if (func.equals("log"))
                v = Math.log10(v);
            else if (func.equals("ln"))
                v = Math.log(v);
            else if (func.equals("abs"))
                v = Math.abs(v);
            else if (func.equals("exp"))
                v = Math.exp(v);
        } else {
            throw new ExpresionInvalidaException("Carácter inesperado: " + (char) ch);
        }

        if (eat('^'))
            v = Math.pow(v, parseFactor(x)); // exponenciación

        return v;
    }
}
