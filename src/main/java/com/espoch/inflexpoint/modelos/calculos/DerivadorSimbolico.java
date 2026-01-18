package com.espoch.inflexpoint.modelos.calculos;

import java.util.*;

public class DerivadorSimbolico {

    public static String derivar(String expresion) {
        if (expresion == null || expresion.trim().isEmpty())
            return "0";
        try {
            String expresionLimpia = normalizar(expresion);
            Analizador analizador = new Analizador(expresionLimpia);
            Nodo ast = analizador.analizar();
            Nodo derivado = ast.derivar();
            Nodo simplificado = derivado.simplificar();
            return simplificado.toMathExpression();
        } catch (Exception e) {
            return "d/dx[" + expresion + "]";
        }
    }

    public static String derivarSegunda(String expresion) {
        if (expresion == null || expresion.trim().isEmpty())
            return "0";
        try {
            String expresionLimpia = normalizar(expresion);
            Analizador analizador = new Analizador(expresionLimpia);
            Nodo ast = analizador.analizar();
            Nodo primeraDerivada = ast.derivar().simplificar();
            Nodo segundaDerivada = primeraDerivada.derivar().simplificar();
            return segundaDerivada.toMathExpression();
        } catch (Exception e) {
            return "d^2/dx^2[" + expresion + "]";
        }
    }

    public static String toLaTeX(String expresion) {
        if (expresion == null || expresion.trim().isEmpty())
            return "0";
        try {
            String expresionLimpia = normalizar(expresion);
            Analizador analizador = new Analizador(expresionLimpia);
            Nodo ast = analizador.analizar();
            return ast.simplificar().toLaTeX();
        } catch (Exception e) {
            return expresion;
        }
    }

    private static String normalizar(String expresion) {
        return expresion.toLowerCase().replaceAll("\\s+", "")
                .replace("arcsen", "asin")
                .replace("arccos", "acos")
                .replace("arctan", "atan")
                .replace("sen", "sin")
                .replace("raiz", "sqrt");
    }

    public interface Nodo {
        Nodo derivar();

        Nodo simplificar();

        String toLaTeX();

        String toMathExpression();

        double getPrioridad();
    }

    static class NodoConstante implements Nodo {
        double valor;

        NodoConstante(double v) {
            this.valor = v;
        }

        public Nodo derivar() {
            return new NodoConstante(0);
        }

        public Nodo simplificar() {
            return this;
        }

        public String toMathExpression() {
            if (valor == (long) valor)
                return String.valueOf((long) valor);
            return String.format(Locale.US, "%.4f", valor).replaceAll("0+$", "").replaceAll("\\.$", "");
        }

        public String toLaTeX() {
            return toMathExpression();
        }

        public double getPrioridad() {
            return 10;
        }
    }

    static class NodoVariable implements Nodo {
        public Nodo derivar() {
            return new NodoConstante(1);
        }

        public Nodo simplificar() {
            return this;
        }

        public String toMathExpression() {
            return "x";
        }

        public String toLaTeX() {
            return "x";
        }

        public double getPrioridad() {
            return 10;
        }
    }

    static class NodoSuma implements Nodo {
        Nodo izquierda, derecha;

        NodoSuma(Nodo l, Nodo r) {
            izquierda = l;
            derecha = r;
        }

        public Nodo derivar() {
            return new NodoSuma(izquierda.derivar(), derecha.derivar());
        }

        public Nodo simplificar() {
            Nodo sl = izquierda.simplificar();
            Nodo sr = derecha.simplificar();

            if (sl instanceof NodoConstante && ((NodoConstante) sl).valor == 0)
                return sr;
            if (sr instanceof NodoConstante && ((NodoConstante) sr).valor == 0)
                return sl;

            if (sl instanceof NodoConstante && sr instanceof NodoConstante) {
                return new NodoConstante(((NodoConstante) sl).valor + ((NodoConstante) sr).valor);
            }

            // Simplificación de términos idénticos (x + x -> 2*x)
            if (sl.toMathExpression().equals(sr.toMathExpression())) {
                return new NodoMultiplicacion(new NodoConstante(2), sl).simplificar();
            }

            return new NodoSuma(sl, sr);
        }

        public String toMathExpression() {
            return "(" + izquierda.toMathExpression() + "+" + derecha.toMathExpression() + ")";
        }

        public String toLaTeX() {
            return izquierda.toLaTeX() + " + " + derecha.toLaTeX();
        }

        public double getPrioridad() {
            return 1;
        }
    }

    static class NodoResta implements Nodo {
        Nodo izquierda, derecha;

        NodoResta(Nodo l, Nodo r) {
            izquierda = l;
            derecha = r;
        }

        public Nodo derivar() {
            return new NodoResta(izquierda.derivar(), derecha.derivar());
        }

        public Nodo simplificar() {
            Nodo sl = izquierda.simplificar();
            Nodo sr = derecha.simplificar();

            if (sr instanceof NodoConstante && ((NodoConstante) sr).valor == 0)
                return sl;

            if (sl instanceof NodoConstante && ((NodoConstante) sl).valor == 0) {
                return new NodoMultiplicacion(new NodoConstante(-1), sr).simplificar();
            }

            if (sl instanceof NodoConstante && sr instanceof NodoConstante) {
                return new NodoConstante(((NodoConstante) sl).valor - ((NodoConstante) sr).valor);
            }

            if (sl.toMathExpression().equals(sr.toMathExpression())) {
                return new NodoConstante(0);
            }

            return new NodoResta(sl, sr);
        }

        public String toMathExpression() {
            return "(" + izquierda.toMathExpression() + "-" + derecha.toMathExpression() + ")";
        }

        public String toLaTeX() {
            return izquierda.toLaTeX() + " - " + derecha.toLaTeX();
        }

        public double getPrioridad() {
            return 1;
        }
    }

    static class NodoMultiplicacion implements Nodo {
        Nodo izquierda, derecha;

        NodoMultiplicacion(Nodo l, Nodo r) {
            izquierda = l;
            derecha = r;
        }

        public Nodo derivar() {
            return new NodoSuma(
                    new NodoMultiplicacion(izquierda.derivar(), derecha),
                    new NodoMultiplicacion(izquierda, derecha.derivar()));
        }

        public Nodo simplificar() {
            Nodo sl = izquierda.simplificar();
            Nodo sr = derecha.simplificar();

            if (sl instanceof NodoConstante) {
                double v = ((NodoConstante) sl).valor;
                if (v == 0)
                    return new NodoConstante(0);
                if (v == 1)
                    return sr;
                if (v == -1) {
                    if (sr instanceof NodoMultiplicacion
                            && ((NodoMultiplicacion) sr).izquierda instanceof NodoConstante) {
                        double valRel = ((NodoConstante) ((NodoMultiplicacion) sr).izquierda).valor;
                        return new NodoMultiplicacion(new NodoConstante(-valRel), ((NodoMultiplicacion) sr).derecha)
                                .simplificar();
                    }
                }
            }
            if (sr instanceof NodoConstante) {
                double v = ((NodoConstante) sr).valor;
                if (v == 0)
                    return new NodoConstante(0);
                if (v == 1)
                    return sl;
                return new NodoMultiplicacion(sr, sl).simplificar(); // Mover constante a la izquierda
            }

            if (sl instanceof NodoConstante && sr instanceof NodoConstante) {
                return new NodoConstante(((NodoConstante) sl).valor * ((NodoConstante) sr).valor);
            }

            // Constante * (Constante * X) -> (C1*C2) * X
            if (sl instanceof NodoConstante && sr instanceof NodoMultiplicacion
                    && ((NodoMultiplicacion) sr).izquierda instanceof NodoConstante) {
                double c1 = ((NodoConstante) sl).valor;
                double c2 = ((NodoConstante) ((NodoMultiplicacion) sr).izquierda).valor;
                return new NodoMultiplicacion(new NodoConstante(c1 * c2), ((NodoMultiplicacion) sr).derecha)
                        .simplificar();
            }

            return new NodoMultiplicacion(sl, sr);
        }

        public String toMathExpression() {
            String l = izquierda.toMathExpression();
            String r = derecha.toMathExpression();
            if (izquierda.getPrioridad() < getPrioridad())
                l = "(" + l + ")";
            if (derecha.getPrioridad() < getPrioridad())
                r = "(" + r + ")";
            return l + "*" + r;
        }

        public String toLaTeX() {
            String l = izquierda.toLaTeX();
            String r = derecha.toLaTeX();

            if (izquierda instanceof NodoConstante && ((NodoConstante) izquierda).valor == -1) {
                return "-" + r;
            }

            if (izquierda.getPrioridad() < getPrioridad())
                l = "(" + l + ")";
            if (derecha.getPrioridad() < getPrioridad())
                r = "(" + r + ")";

            // Si es Constante * Variable, no poner \cdot
            if (izquierda instanceof NodoConstante && (derecha instanceof NodoVariable || derecha instanceof NodoFuncion
                    || derecha instanceof NodoPotencia)) {
                return l + r;
            }

            return l + " \\cdot " + r;
        }

        public double getPrioridad() {
            return 2;
        }
    }

    static class NodoDivision implements Nodo {
        Nodo izquierda, derecha;

        NodoDivision(Nodo l, Nodo r) {
            izquierda = l;
            derecha = r;
        }

        public Nodo derivar() {
            return new NodoDivision(
                    new NodoResta(new NodoMultiplicacion(izquierda.derivar(), derecha),
                            new NodoMultiplicacion(izquierda, derecha.derivar())),
                    new NodoPotencia(derecha, new NodoConstante(2)));
        }

        public Nodo simplificar() {
            Nodo sl = izquierda.simplificar();
            Nodo sr = derecha.simplificar();

            if (sl instanceof NodoConstante && ((NodoConstante) sl).valor == 0)
                return new NodoConstante(0);
            if (sr instanceof NodoConstante) {
                double v = ((NodoConstante) sr).valor;
                if (v == 1)
                    return sl;
                if (v == -1)
                    return new NodoMultiplicacion(new NodoConstante(-1), sl).simplificar();
            }

            // (A/B)/C -> A/(B*C)
            if (sl instanceof NodoDivision) {
                return new NodoDivision(((NodoDivision) sl).izquierda,
                        new NodoMultiplicacion(((NodoDivision) sl).derecha, sr)).simplificar();
            }

            // Simplificación básica x^n / x^m -> x^(n-m)
            if (sl instanceof NodoPotencia && sr instanceof NodoPotencia && ((NodoPotencia) sl).base.toMathExpression()
                    .equals(((NodoPotencia) sr).base.toMathExpression())) {
                Nodo base = ((NodoPotencia) sl).base;
                return new NodoPotencia(base,
                        new NodoResta(((NodoPotencia) sl).exponente, ((NodoPotencia) sr).exponente)).simplificar();
            }

            // Constante / Constante
            if (sl instanceof NodoConstante && sr instanceof NodoConstante) {
                double v1 = ((NodoConstante) sl).valor;
                double v2 = ((NodoConstante) sr).valor;
                if (v1 % v2 == 0)
                    return new NodoConstante(v1 / v2);
            }

            return new NodoDivision(sl, sr);
        }

        public String toMathExpression() {
            return "(" + izquierda.toMathExpression() + "/" + derecha.toMathExpression() + ")";
        }

        public String toLaTeX() {
            return "\\frac{" + izquierda.toLaTeX() + "}{" + derecha.toLaTeX() + "}";
        }

        public double getPrioridad() {
            return 2;
        }
    }

    static class NodoPotencia implements Nodo {
        Nodo base, exponente;

        NodoPotencia(Nodo b, Nodo e) {
            base = b;
            exponente = e;
        }

        public Nodo derivar() {
            Nodo sb = base.simplificar();
            Nodo se = exponente.simplificar();

            if (se instanceof NodoConstante) {
                double n = ((NodoConstante) se).valor;
                // n * x^(n-1) * x'
                return new NodoMultiplicacion(
                        new NodoMultiplicacion(new NodoConstante(n), new NodoPotencia(base, new NodoConstante(n - 1))),
                        base.derivar());
            }
            // (a^u)' = a^u * ln(a) * u'
            if (sb instanceof NodoConstante) {
                return new NodoMultiplicacion(
                        new NodoMultiplicacion(this, new NodoFuncion("ln", sb)),
                        exponente.derivar());
            }

            return new NodoConstante(0);
        }

        public Nodo simplificar() {
            Nodo sb = base.simplificar();
            Nodo se = exponente.simplificar();

            if (se instanceof NodoConstante) {
                double v = ((NodoConstante) se).valor;
                if (v == 0)
                    return new NodoConstante(1);
                if (v == 1)
                    return sb;
            }

            if (sb instanceof NodoPotencia) {
                // (x^a)^b -> x^(a*b)
                return new NodoPotencia(((NodoPotencia) sb).base,
                        new NodoMultiplicacion(((NodoPotencia) sb).exponente, se)).simplificar();
            }

            if (sb instanceof NodoConstante && ((NodoConstante) sb).valor == 0)
                return new NodoConstante(0);
            if (sb instanceof NodoConstante && ((NodoConstante) sb).valor == 1)
                return new NodoConstante(1);

            return new NodoPotencia(sb, se);
        }

        public String toMathExpression() {
            String b = base.toMathExpression();
            if (base.getPrioridad() <= getPrioridad())
                b = "(" + b + ")";
            return b + "^" + exponente.toMathExpression();
        }

        public String toLaTeX() {
            String b = base.toLaTeX();
            if (base instanceof NodoVariable || base instanceof NodoConstante) {
                return b + "^{" + exponente.toLaTeX() + "}";
            }
            return "(" + b + ")^{" + exponente.toLaTeX() + "}";
        }

        public double getPrioridad() {
            return 3;
        }
    }

    static class NodoFuncion implements Nodo {
        String nombre;
        Nodo argumento;

        NodoFuncion(String n, Nodo a) {
            nombre = n;
            argumento = a;
        }

        public Nodo derivar() {
            Nodo derivadoInterno = argumento.derivar();
            Nodo derivadoExterno;
            switch (nombre) {
                case "sin":
                    derivadoExterno = new NodoFuncion("cos", argumento);
                    break;
                case "cos":
                    derivadoExterno = new NodoMultiplicacion(new NodoConstante(-1), new NodoFuncion("sin", argumento));
                    break;
                case "tan":
                    derivadoExterno = new NodoPotencia(new NodoFuncion("sec", argumento), new NodoConstante(2));
                    break;
                case "sec":
                    derivadoExterno = new NodoMultiplicacion(new NodoFuncion("sec", argumento),
                            new NodoFuncion("tan", argumento));
                    break;
                case "csc":
                    derivadoExterno = new NodoMultiplicacion(new NodoConstante(-1),
                            new NodoMultiplicacion(new NodoFuncion("csc", argumento),
                                    new NodoFuncion("cot", argumento)));
                    break;
                case "cot":
                    derivadoExterno = new NodoMultiplicacion(new NodoConstante(-1),
                            new NodoPotencia(new NodoFuncion("csc", argumento), new NodoConstante(2)));
                    break;
                case "asin":
                    derivadoExterno = new NodoDivision(new NodoConstante(1),
                            new NodoFuncion("sqrt", new NodoResta(new NodoConstante(1),
                                    new NodoPotencia(argumento, new NodoConstante(2)))));
                    break;
                case "acos":
                    derivadoExterno = new NodoMultiplicacion(new NodoConstante(-1),
                            new NodoDivision(new NodoConstante(1),
                                    new NodoFuncion("sqrt", new NodoResta(new NodoConstante(1),
                                            new NodoPotencia(argumento, new NodoConstante(2))))));
                    break;
                case "atan":
                    derivadoExterno = new NodoDivision(new NodoConstante(1),
                            new NodoSuma(new NodoConstante(1), new NodoPotencia(argumento, new NodoConstante(2))));
                    break;
                case "log":
                    // log10(u)' = 1/(u*ln(10)) * u'
                    derivadoExterno = new NodoDivision(new NodoConstante(1),
                            new NodoMultiplicacion(argumento, new NodoFuncion("ln", new NodoConstante(10))));
                    break;
                case "ln":
                    derivadoExterno = new NodoDivision(new NodoConstante(1), argumento);
                    break;
                case "exp":
                    derivadoExterno = new NodoFuncion("exp", argumento);
                    break;
                case "sqrt":
                    derivadoExterno = new NodoDivision(new NodoConstante(1),
                            new NodoMultiplicacion(new NodoConstante(2), new NodoFuncion("sqrt", argumento)));
                    break;
                case "abs":
                    // abs(u)' = u/abs(u) * u'
                    derivadoExterno = new NodoDivision(argumento, new NodoFuncion("abs", argumento));
                    break;
                default:
                    return new NodoConstante(0);
            }
            return new NodoMultiplicacion(derivadoExterno, derivadoInterno);
        }

        public Nodo simplificar() {
            Nodo sa = argumento.simplificar();
            return new NodoFuncion(nombre, sa);
        }

        public String toMathExpression() {
            return nombre + "(" + argumento.toMathExpression() + ")";
        }

        public String toLaTeX() {
            String n = nombre;
            if (n.equals("sin"))
                n = "\\sin";
            else if (n.equals("cos"))
                n = "\\cos";
            else if (n.equals("tan"))
                n = "\\tan";
            else if (n.equals("sec"))
                n = "\\sec";
            else if (n.equals("csc"))
                n = "\\csc";
            else if (n.equals("cot"))
                n = "\\cot";
            else if (n.equals("asin"))
                n = "\\arcsin";
            else if (n.equals("acos"))
                n = "\\arccos";
            else if (n.equals("atan"))
                n = "\\arctan";
            else if (n.equals("log"))
                n = "\\log_{10}";
            else if (n.equals("ln"))
                n = "\\ln";
            else if (n.equals("exp"))
                return "e^{" + argumento.toLaTeX() + "}";
            else if (n.equals("sqrt"))
                return "\\sqrt{" + argumento.toLaTeX() + "}";
            else if (n.equals("abs"))
                return "\\left| " + argumento.toLaTeX() + " \\right|";

            return n + "(" + argumento.toLaTeX() + ")";
        }

        public double getPrioridad() {
            return 10;
        }
    }

    static class Analizador {
        String entrada;
        int pos = 0;

        Analizador(String s) {
            entrada = s;
        }

        Nodo analizar() {
            return analizarSumaResta();
        }

        Nodo analizarSumaResta() {
            Nodo nodo = analizarMultiDiv();
            while (pos < entrada.length()) {
                if (entrada.charAt(pos) == '+') {
                    pos++;
                    nodo = new NodoSuma(nodo, analizarSumaResta());
                } else if (entrada.charAt(pos) == '-') {
                    pos++;
                    nodo = new NodoResta(nodo, analizarMultiDiv());
                } else
                    break;
            }
            return nodo;
        }

        Nodo analizarMultiDiv() {
            Nodo nodo = analizarPotencia();
            while (pos < entrada.length()) {
                if (entrada.charAt(pos) == '*') {
                    pos++;
                    nodo = new NodoMultiplicacion(nodo, analizarPotencia());
                } else if (entrada.charAt(pos) == '/') {
                    pos++;
                    nodo = new NodoDivision(nodo, analizarPotencia());
                } else
                    break;
            }
            return nodo;
        }

        Nodo analizarPotencia() {
            Nodo nodo = analizarFactor();
            if (pos < entrada.length() && entrada.charAt(pos) == '^') {
                pos++;
                nodo = new NodoPotencia(nodo, analizarPotencia());
            }
            return nodo;
        }

        Nodo analizarFactor() {
            if (pos >= entrada.length())
                return new NodoConstante(0);
            char c = entrada.charAt(pos);
            if (c == '(') {
                pos++;
                Nodo n = analizarSumaResta();
                if (pos < entrada.length() && entrada.charAt(pos) == ')')
                    pos++;
                return n;
            }
            if (c == 'x') {
                pos++;
                return new NodoVariable();
            }
            if (Character.isDigit(c)) {
                int start = pos;
                while (pos < entrada.length() && (Character.isDigit(entrada.charAt(pos)) || entrada.charAt(pos) == '.'))
                    pos++;
                return new NodoConstante(Double.parseDouble(entrada.substring(start, pos)));
            }
            if (Character.isLetter(c)) {
                int start = pos;
                while (pos < entrada.length() && Character.isLetter(entrada.charAt(pos)))
                    pos++;
                String nombreFunc = entrada.substring(start, pos);
                if (nombreFunc.equals("e"))
                    return new NodoConstante(Math.E);
                if (nombreFunc.equals("pi"))
                    return new NodoConstante(Math.PI);
                if (pos < entrada.length() && entrada.charAt(pos) == '(') {
                    pos++;
                    Nodo arg = analizarSumaResta();
                    if (pos < entrada.length() && entrada.charAt(pos) == ')')
                        pos++;
                    return new NodoFuncion(nombreFunc, arg);
                }
                return new NodoVariable();
            }
            return new NodoConstante(0);
        }
    }
}
