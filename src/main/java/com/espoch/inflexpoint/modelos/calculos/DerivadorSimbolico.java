package com.espoch.inflexpoint.modelos.calculos;

public class DerivadorSimbolico {

    public static String derivar(String expresion) {
        if (expresion == null || expresion.trim().isEmpty())
            return "0";
        try {
            String expresionLimpia = normalizar(expresion);
            Analizador analizador = new Analizador(expresionLimpia);
            Nodo ast = analizador.analizar();
            Nodo derivado = ast.derivar();
            return derivado.simplificar().toString();
        } catch (Exception e) {
            return "d/dx[" + expresion + "]";
        }
    }

    private static String normalizar(String expresion) {
        return expresion.toLowerCase().replaceAll("\\s+", "")
                .replace("sen", "sin")
                .replace("raiz", "sqrt");
    }

    interface Nodo {
        Nodo derivar();

        Nodo simplificar();

        String toString();
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

        public String toString() {
            if (valor == (long) valor)
                return String.valueOf((long) valor);
            return String.format("%.2f", valor);
        }
    }

    static class NodoVariable implements Nodo {
        public Nodo derivar() {
            return new NodoConstante(1);
        }

        public Nodo simplificar() {
            return this;
        }

        public String toString() {
            return "x";
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
            if (sl instanceof NodoConstante && sr instanceof NodoConstante)
                return new NodoConstante(((NodoConstante) sl).valor + ((NodoConstante) sr).valor);
            return new NodoSuma(sl, sr);
        }

        public String toString() {
            return "(" + izquierda + " + " + derecha + ")";
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
            if (sl instanceof NodoConstante && sr instanceof NodoConstante)
                return new NodoConstante(((NodoConstante) sl).valor - ((NodoConstante) sr).valor);
            return new NodoResta(sl, sr);
        }

        public String toString() {
            return "(" + izquierda + " - " + derecha + ")";
        }
    }

    static class NodoMultiplicacion implements Nodo {
        Nodo izquierda, derecha;

        NodoMultiplicacion(Nodo l, Nodo r) {
            izquierda = l;
            derecha = r;
        }

        public Nodo derivar() {
            return new NodoSuma(new NodoMultiplicacion(izquierda.derivar(), derecha),
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
            }
            if (sr instanceof NodoConstante) {
                double v = ((NodoConstante) sr).valor;
                if (v == 0)
                    return new NodoConstante(0);
                if (v == 1)
                    return sl;
            }
            if (sl instanceof NodoConstante && sr instanceof NodoConstante)
                return new NodoConstante(((NodoConstante) sl).valor * ((NodoConstante) sr).valor);
            return new NodoMultiplicacion(sl, sr);
        }

        public String toString() {
            return izquierda + "*" + derecha;
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
            if (sr instanceof NodoConstante && ((NodoConstante) sr).valor == 1)
                return sl;
            return new NodoDivision(sl, sr);
        }

        public String toString() {
            return "(" + izquierda + "/" + derecha + ")";
        }
    }

    static class NodoPotencia implements Nodo {
        Nodo base, exponente;

        NodoPotencia(Nodo b, Nodo e) {
            base = b;
            exponente = e;
        }

        public Nodo derivar() {
            Nodo se = exponente.simplificar();
            if (se instanceof NodoConstante) {
                double n = ((NodoConstante) se).valor;
                return new NodoMultiplicacion(
                        new NodoMultiplicacion(new NodoConstante(n), new NodoPotencia(base, new NodoConstante(n - 1))),
                        base.derivar());
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
            return new NodoPotencia(sb, se);
        }

        public String toString() {
            return "(" + base + "^" + exponente + ")";
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
                default:
                    return new NodoConstante(0);
            }
            return new NodoMultiplicacion(derivadoExterno, derivadoInterno);
        }

        public Nodo simplificar() {
            Nodo sa = argumento.simplificar();
            return new NodoFuncion(nombre, sa);
        }

        public String toString() {
            return nombre + "(" + argumento + ")";
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
                    nodo = new NodoSuma(nodo, analizarMultiDiv());
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
