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

        // 1. Limpieza inicial y conversión de español a inglés
        // IMPORTANTE: Reemplazar nombres más largos primero
        String norm = expr.trim().toLowerCase()
                .replace(" ", "")
                .replace("²", "^2")
                .replace("³", "^3")
                .replace("arcsen(", "asin(")
                .replace("arccos(", "acos(")
                .replace("arctan(", "atan(")
                .replace("sen(", "sin(") // sen despues de arcsen
                .replace("raiz(", "sqrt(");

        // 2. Protección de tokens (funciones y constantes)
        // Usamos marcadores ASCII "TK" + Letra para evitar conflictos con dígitos en
        // mult. implícita
        // ORDEN IMPORTANTE: Tokens más largos primero
        String[] tokens = {
                "asin", "acos", "atan", "sqrt", // 4 chars
                "sin", "cos", "tan", "csc", "sec", "cot", "log", "exp", "abs", // 3 chars
                "ln", "pi", // 2 chars
                "e" // 1 char
        };

        // Placeholders: TKA ... TKP (letras evitan detección como dígito)
        String[] placeholders = {
                "TKA", "TKB", "TKC", "TKD",
                "TKE", "TKF", "TKG", "TKH", "TKI", "TKJ", "TKK", "TKL", "TKM",
                "TKN", "TKO",
                "TKP"
        };

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

        // 3d. Constantes 'e' y 'pi' (ahora son TK14 y TK15) seguidas de (Digito, x, (,
        // T)
        // Los Tokens terminan en digito 0-9.
        // Ej: pi(x) -> TK14(x). No queremos TK14*(x) si es funcion, pero pi es
        // constante.
        // Diferenciemos funciones de constantes si es posible.
        // sqrt(TK0) no debe llevar * después.
        // pi(TK14) y e(TK15) SÍ pueden llevar.
        // Pero en los placeholders, ambos son TK#.

        // Vamos a ser más específicos con las constantes.
        // pi -> TK14, e -> TK15.
        // 3d. Constantes 'pi'(TKO) y 'e'(TKP) seguidas de (Digito, x, (, T)
        norm = norm.replaceAll("(?<=TK[OP])(?=[\\dx\\(T])", "*");

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

            // Primero verificar si es una constante
            if (func.equals("e")) {
                v = Math.E;
            } else if (func.equals("pi")) {
                v = Math.PI;
            } else {
                // Para funciones, DEBE haber paréntesis
                if (!eat('(')) {
                    throw new ExpresionInvalidaException(
                            "La función '" + func + "' requiere paréntesis: " + func + "(...)");
                }

                // Evaluar el argumento de la función
                v = parseExpression(x);
                eat(')');

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

        if (eat('^'))
            v = Math.pow(v, parseFactor(x)); // exponenciación

        return v;
    }
}
