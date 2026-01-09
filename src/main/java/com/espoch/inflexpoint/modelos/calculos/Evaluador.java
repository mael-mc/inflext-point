package com.espoch.inflexpoint.modelos.calculos;

/**
 * Clase auxiliar para evaluar expresiones matemáticas.
 * Implementa un parser recursivo simple.
 */
public class Evaluador {

    private String expression;
    private int pos = -1, ch;

    public Evaluador(String expression) {
        this.expression = expression;
    }

    public double evaluar(double x) {
        this.pos = -1;
        this.expression = this.expression.replaceAll(" ", ""); // Eliminar espacios
        nextChar();
        double res = parseExpression(x);
        if (pos < expression.length())
            throw new RuntimeException("Carácter inesperado: " + (char) ch);
        return res;
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

    private double parseExpression(double x) {
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

    private double parseTerm(double x) {
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

    private double parseFactor(double x) {
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
                throw new RuntimeException("Función desconocida: " + func);
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
            throw new RuntimeException("Carácter inesperado: " + (char) ch);
        }

        if (eat('^'))
            v = Math.pow(v, parseFactor(x)); // exponenciación

        return v;
    }
}
