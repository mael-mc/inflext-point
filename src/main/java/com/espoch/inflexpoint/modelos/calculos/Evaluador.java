package com.espoch.inflexpoint.modelos.calculos;

import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

public class Evaluador {

    private String expresion;
    private int pos = -1, ch;

    public Evaluador(String expresion) throws ExpresionInvalidaException {
        if (expresion == null || expresion.trim().isEmpty()) {
            throw new ExpresionInvalidaException("La expresión no puede estar vacía");
        }
        this.expresion = normalizar(expresion);
    }

    public double evaluar(double x) throws ExpresionInvalidaException {
        this.pos = -1;
        siguienteCaracter();
        double res = analizarExpresion(x);
        if (pos < expresion.length()) {
            throw new ExpresionInvalidaException("Carácter inesperado: " + (char) ch);
        }
        return res;
    }

    private String normalizar(String expr) {
        if (expr == null)
            return "";

        String norm = expr.trim().toLowerCase()
                .replace(" ", "")
                .replace("²", "^2")
                .replace("³", "^3")
                .replace("arcsen(", "asin(")
                .replace("arccos(", "acos(")
                .replace("arctan(", "atan(")
                .replace("sen(", "sin(") // sen despues de arcsen
                .replace("raiz(", "sqrt(");

        String[] tokens = {
                "asin", "acos", "atan", "sqrt", // 4 chars
                "sin", "cos", "tan", "csc", "sec", "cot", "log", "exp", "abs", // 3 chars
                "ln", "pi", // 2 chars
                "e" // 1 char
        };

        String[] placeholders = {
                "TKA", "TKB", "TKC", "TKD",
                "TKE", "TKF", "TKG", "TKH", "TKI", "TKJ", "TKK", "TKL", "TKM",
                "TKN", "TKO",
                "TKP"
        };

        for (int i = 0; i < tokens.length; i++) {
            norm = norm.replace(tokens[i], placeholders[i]);
        }

        // 3a. Digito seguido de (x, (, T)
        norm = norm.replaceAll("(?<=\\d)(?=[x\\(T])", "*");

        // 3b. ')' seguido de (Digito, x, (, T)
        norm = norm.replaceAll("(?<=\\))(?=[\\dx\\(T])", "*");

        // 3c. 'x' seguido de (Digito, x, (, T)
        norm = norm.replaceAll("(?<=x)(?=[\\dx\\(T])", "*");

        norm = norm.replaceAll("(?<=TK[OP])(?=[\\dx\\(T])", "*");

        // 4. Restauración de tokens
        for (int i = 0; i < tokens.length; i++) {
            norm = norm.replace(placeholders[i], tokens[i]);
        }

        return norm;
    }

    private void siguienteCaracter() {
        ch = (++pos < expresion.length()) ? expresion.charAt(pos) : -1;
    }

    private boolean consumir(int charToEat) {
        while (ch == ' ')
            siguienteCaracter();
        if (ch == charToEat) {
            siguienteCaracter();
            return true;
        }
        return false;
    }

    private double analizarExpresion(double x) throws ExpresionInvalidaException {
        double v = analizarTermino(x);
        for (;;) {
            if (consumir('+'))
                v += analizarTermino(x); // suma
            else if (consumir('-'))
                v -= analizarTermino(x); // resta
            else
                return v;
        }
    }

    private double analizarTermino(double x) throws ExpresionInvalidaException {
        double v = analizarFactor(x);
        for (;;) {
            if (consumir('*'))
                v *= analizarFactor(x); // multiplicación
            else if (consumir('/'))
                v /= analizarFactor(x); // división
            else
                return v;
        }
    }

    private double analizarFactor(double x) throws ExpresionInvalidaException {
        if (consumir('+'))
            return analizarFactor(x); // unario más
        if (consumir('-'))
            return -analizarFactor(x); // unario menos

        double v;
        int startPos = this.pos;
        if (consumir('(')) { // paréntesis
            v = analizarExpresion(x);
            consumir(')');
        } else if (ch == 'x' || ch == 'X') { // variable literal
            siguienteCaracter();
            v = x;
        } else if ((ch >= '0' && ch <= '9') || ch == '.') { // números
            while ((ch >= '0' && ch <= '9') || ch == '.')
                siguienteCaracter();
            v = Double.parseDouble(expresion.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') { // funciones
            while (ch >= 'a' && ch <= 'z')
                siguienteCaracter();
            String func = expresion.substring(startPos, this.pos);

            // Primero verificar si es una constante
            if (func.equals("e")) {
                v = Math.E;
            } else if (func.equals("pi")) {
                v = Math.PI;
            } else {
                // Para funciones, DEBE haber paréntesis
                if (!consumir('(')) {
                    throw new ExpresionInvalidaException(
                            "La función '" + func + "' requiere paréntesis: " + func + "(...)");
                }

                // Evaluar el argumento de la función
                v = analizarExpresion(x);
                consumir(')');

                // Aplicar la función correspondiente
                if (func.equals("sqrt"))
                    v = Math.sqrt(v);
                else if (func.equals("sin"))
                    v = Math.sin(v);
                else if (func.equals("cos"))
                    v = Math.cos(v);
                else if (func.equals("tan"))
                    v = Math.tan(v);
                else if (func.equals("csc"))
                    v = 1.0 / Math.sin(v);
                else if (func.equals("sec"))
                    v = 1.0 / Math.cos(v);
                else if (func.equals("cot"))
                    v = 1.0 / Math.tan(v);
                else if (func.equals("asin"))
                    v = Math.asin(v);
                else if (func.equals("acos"))
                    v = Math.acos(v);
                else if (func.equals("atan"))
                    v = Math.atan(v);
                else if (func.equals("log"))
                    v = Math.log10(v);
                else if (func.equals("ln"))
                    v = Math.log(v);
                else if (func.equals("abs"))
                    v = Math.abs(v);
                else if (func.equals("exp"))
                    v = Math.exp(v);
                else {
                    throw new ExpresionInvalidaException("Función desconocida: " + func);
                }
            }
        } else {
            throw new ExpresionInvalidaException("Carácter inesperado: " + (char) ch);
        }

        if (consumir('^'))
            v = Math.pow(v, analizarFactor(x)); // exponenciación

        return v;
    }
}
