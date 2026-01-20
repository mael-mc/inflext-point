package com.espoch.inflexpoint.modelos.calculos;

import com.espoch.inflexpoint.modelos.entidades.Intervalo;
import com.espoch.inflexpoint.modelos.entidades.PuntoCritico;
import com.espoch.inflexpoint.modelos.enumeraciones.TipoIntervalo;
import com.espoch.inflexpoint.modelos.enumeraciones.TipoPuntoCritico;
import com.espoch.inflexpoint.modelos.excepciones.CalculoNumericoException;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;
import com.espoch.inflexpoint.util.ValidadorExpresion;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de análisis matemático de funciones.
 * Responsabilidades:
 * - Analizar funciones para encontrar puntos críticos
 * - Calcular puntos de inflexión
 * - Determinar intervalos de monotonía y concavidad
 * - Retornar resultados usando entidades del dominio
 * Este servicio NO maneja UI ni presentación, solo lógica de negocio.
 */
public class AnalizadorFuncion {

    // Constantes para métodos numéricos
    private static final double PASO_DERIVADA = 0.0001;
    private static final double TOLERANCIA_BISECCION = 1e-6;
    private static final int MAX_ITERACIONES_BISECCION = 50;
    private static final double TOLERANCIA_CERO = 1e-5;

    // Rango de análisis por defecto
    private static final double MIN_X_DEFECTO = -10.0;
    private static final double MAX_X_DEFECTO = 10.0;
    private static final double PASO_DEFECTO = 0.1;

    /**
     * Interfaz funcional para derivadas genéricas.
     */
    @FunctionalInterface
    private interface FuncionDerivada {
        double calcular(double x);
    }

    /**
     * Analiza una función matemática y calcula los elementos solicitados.
     * 
     * @param expresion          La expresión matemática a analizar
     * @param calcPuntosCriticos true para calcular puntos críticos
     * @param calcIntervalos     true para calcular intervalos de monotonía
     * @param calcMaxMin         true para clasificar máximos y mínimos
     * @param calcInflexion      true para calcular puntos de inflexión
     * @param calcConcavidad     true para calcular intervalos de concavidad
     * @return ResultadoAnalisis con las entidades calculadas
     * @throws ExpresionInvalidaException si la expresión no puede ser parseada
     * @throws CalculoNumericoException   si ocurre un error durante los cálculos
     */
    public ResultadoAnalisis analizar(
            String expresion,
            boolean calcPuntosCriticos,
            boolean calcIntervalos,
            boolean calcMaxMin,
            boolean calcInflexion,
            boolean calcConcavidad)
            throws ExpresionInvalidaException, CalculoNumericoException {

        return analizarEnRango(expresion, MIN_X_DEFECTO, MAX_X_DEFECTO, PASO_DEFECTO,
                calcPuntosCriticos, calcIntervalos, calcMaxMin, calcInflexion, calcConcavidad);
    }

    public ResultadoAnalisis analizarEnRango(
            String expresion,
            double minX,
            double maxX,
            double step,
            boolean calcPuntosCriticos,
            boolean calcIntervalos,
            boolean calcMaxMin,
            boolean calcInflexion,
            boolean calcConcavidad)
            throws ExpresionInvalidaException, CalculoNumericoException {

        // Validar expresión
        ValidadorExpresion.validar(expresion);

        // Crear evaluador
        Evaluador evaluador;
        try {
            evaluador = new Evaluador(expresion);
        } catch (Exception e) {
            throw new ExpresionInvalidaException("Error al parsear la expresión", e);
        }

        // Colecciones para resultados
        PuntoCritico[] puntosCriticos = new PuntoCritico[0];
        PuntoCritico[] puntosInflexion = new PuntoCritico[0];
        Intervalo[] intervalosCrecimiento = new Intervalo[0];
        Intervalo[] intervalosDecrecimiento = new Intervalo[0];
        Intervalo[] intervalosConcavidad = new Intervalo[0];

        try {
            // Encontrar puntos críticos si se solicita
            if (calcPuntosCriticos || calcMaxMin) {
                List<Double> raicesPrimeraDerivada = encontrarRaices(
                        x -> derivada(evaluador, x), minX, maxX, step);

                if (calcMaxMin) {
                    // Clasificar como máximos o mínimos
                    puntosCriticos = clasificarPuntosCriticos(evaluador, raicesPrimeraDerivada);
                } else {
                    // Solo puntos críticos sin clasificar
                    puntosCriticos = crearPuntosCriticos(evaluador, raicesPrimeraDerivada, null);
                }
            }

            // Encontrar puntos de inflexión si se solicita
            // Encontrar puntos de inflexión si se solicita
            if (calcInflexion) {
                List<Double> raicesSegundaDerivada = encontrarRaices(
                        x -> segundaDerivada(evaluador, x), minX, maxX, step);

                // Filtrar raíces para asegurar que hay un cambio de signo real (concavidad
                // cambia)
                List<Double> raicesValidadas = new ArrayList<>();
                for (double raiz : raicesSegundaDerivada) {
                    if (verificarCambioSigno(x -> segundaDerivada(evaluador, x), raiz, step / 10.0)) {
                        raicesValidadas.add(raiz);
                    }
                }

                puntosInflexion = crearPuntosCriticos(
                        evaluador, raicesValidadas, TipoPuntoCritico.INFLEXION);
            }

            // Calcular intervalos de monotonía si se solicita
            if (calcIntervalos) {
                Intervalo[] intervalosMonotonia = calcularIntervalosMonotonia(
                        evaluador, puntosCriticos, minX, maxX);

                // Separar en crecientes y decrecientes
                List<Intervalo> crecientes = new ArrayList<>();
                List<Intervalo> decrecientes = new ArrayList<>();

                for (Intervalo intervalo : intervalosMonotonia) {
                    if (intervalo.getTipoIntervalo() == TipoIntervalo.CRECIENTE) {
                        crecientes.add(intervalo);
                    } else {
                        decrecientes.add(intervalo);
                    }
                }

                intervalosCrecimiento = crecientes.toArray(new Intervalo[0]);
                intervalosDecrecimiento = decrecientes.toArray(new Intervalo[0]);
            }

            // Calcular intervalos de concavidad si se solicita
            if (calcConcavidad) {
                intervalosConcavidad = calcularIntervalosConcavidad(
                        evaluador, puntosInflexion, minX, maxX);
            }

        } catch (Exception e) {
            throw new CalculoNumericoException(
                    "Error durante el análisis numérico: " + e.getMessage(), e);
        }

        // Calcular derivadas simbólicas
        String d1 = DerivadorSimbolico.derivar(expresion);
        String d2 = DerivadorSimbolico.derivarSegunda(expresion);

        // Crear y retornar resultado
        ResultadoAnalisis resultado = new ResultadoAnalisis(
                puntosCriticos,
                puntosInflexion,
                intervalosCrecimiento,
                intervalosDecrecimiento,
                intervalosConcavidad,
                d1,
                d2);

        // --- LÓGICA DE ACCESIBILIDAD ---
        // Validar si la función es constante o lineal en el rango para informar al
        // usuario
        // Validar si la función es constante, lineal o cuadrática en el rango
        boolean siempreDerivadaCero = true;
        boolean siempreSegundaDerivadaCero = true;
        double valorReferenciaD2 = Double.NaN;
        boolean siempreSegundaDerivadaConstante = true;

        for (double x = minX; x <= maxX; x += step) {
            double valD1 = derivada(evaluador, x);
            double valD2 = segundaDerivada(evaluador, x);

            if (Double.isFinite(valD1)) {
                if (Math.abs(valD1) >= TOLERANCIA_CERO) {
                    siempreDerivadaCero = false;
                }
            }
            if (Double.isFinite(valD2)) {
                if (Math.abs(valD2) >= TOLERANCIA_CERO) {
                    siempreSegundaDerivadaCero = false;
                }

                if (Double.isNaN(valorReferenciaD2)) {
                    valorReferenciaD2 = valD2;
                } else if (Math.abs(valD2 - valorReferenciaD2) >= 1e-3) {
                    // Relaxamos la tolerancia para la segunda derivada ya que es más ruidosa
                    siempreSegundaDerivadaConstante = false;
                }
            }
        }

        // Si nunca encontramos un valor finito para D2, no podemos decir que sea
        // constante
        if (Double.isNaN(valorReferenciaD2)) {
            siempreSegundaDerivadaConstante = false;
        }

        // Detectar y procesar singularidades (Asíntotas, NaN, etc.)
        List<Singularidad> singularidades = identificarSingularidades(evaluador, minX, maxX);
        procesarSingularidades(singularidades, resultado);

        if (siempreDerivadaCero) {
            resultado.setPuntosCriticos(new PuntoCritico[0]);
            resultado.setIntervalosCrecimiento(new Intervalo[0]);
            resultado.setIntervalosDecrecimiento(new Intervalo[0]);
            resultado.setPuntosInflexion(new PuntoCritico[0]);
            resultado.setIntervalosConcavidad(new Intervalo[0]);
            resultado.agregarMensajeAccesibilidad(
                    "Esta es una función constante. No tiene puntos críticos, extremos ni intervalos de crecimiento/decrecimiento.");
            return resultado; // Si es constante, no evaluamos más
        } else if (siempreSegundaDerivadaCero) {
            resultado.setPuntosInflexion(new PuntoCritico[0]);
            resultado.setIntervalosConcavidad(new Intervalo[0]);
            resultado.setPuntosCriticos(new PuntoCritico[0]);
            resultado.agregarMensajeAccesibilidad(
                    "Esta es una función lineal. No tiene puntos de inflexión, críticos ni concavidad definida.");
            return resultado; // Si es lineal, no evaluamos más
        }

        // --- DETECCIÓN DE FUNCIONES TRIGONOMÉTRICAS ---
        if (esTrigonometrica(expresion)) {
            resultado.agregarMensajeAccesibilidad("Esta es una función trigonométrica.");

            if (expresion.contains("sin") || expresion.contains("cos") || expresion.contains("sen")) {
                resultado.agregarMensajeAccesibilidad(
                        "Las funciones seno y coseno son periódicas con un periodo de 2π (aprox. 6.28).");
                resultado.agregarMensajeAccesibilidad(
                        "Debido a su periodicidad, los puntos críticos y de inflexión se repiten infinitamente.");
            }

            if (expresion.contains("tan") || expresion.contains("sec")) {
                resultado.agregarMensajeAccesibilidad(
                        "Las funciones tangente y secante tienen asíntotas verticales en (2k+1)π/2.");
                resultado.agregarMensajeAccesibilidad("El dominio está restringido y presenta saltos infinitos.");
            }

            if (expresion.contains("cot") || expresion.contains("csc")) {
                resultado.agregarMensajeAccesibilidad(
                        "Las funciones cotangente y cosecante tienen asíntotas verticales en kπ.");
                resultado.agregarMensajeAccesibilidad("El dominio está restringido en múltiplos de π.");
            }

            if (expresion.contains("tan") || expresion.contains("cot")) {
                resultado.agregarMensajeAccesibilidad(
                        "El periodo de las funciones tangente y cotangente es π (aprox. 3.14).");
            }
        }

        // --- DETECCIÓN DE FUNCIONES LOGARÍTMICAS ---
        if (esLogaritmica(expresion)) {
            resultado.agregarMensajeAccesibilidad("Esta es una función logarítmica.");
            resultado.agregarMensajeAccesibilidad(
                    "El dominio está restringido: el argumento del logaritmo debe ser estrictamente mayor que cero.");
            resultado.agregarMensajeAccesibilidad(
                    "Presenta una asíntota vertical en el valor donde el argumento es igual a cero.");
        }

        if (esIrracional(expresion)) {
            // Detectar dominio y rango en el intervalo de análisis
            boolean tieneNaN = false;
            double primerX = Double.NaN, ultimoX = Double.NaN;
            double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

            for (double x = minX; x <= maxX; x += step) {
                try {
                    double val = evaluador.evaluar(x);
                    if (Double.isNaN(val)) {
                        tieneNaN = true;
                    } else if (Double.isFinite(val)) {
                        if (Double.isNaN(primerX))
                            primerX = x;
                        ultimoX = x;
                        if (val < minY)
                            minY = val;
                        if (val > maxY)
                            maxY = val;
                    }
                } catch (Exception e) {
                    tieneNaN = true;
                }
            }

            if (tieneNaN && !Double.isNaN(primerX)) {
                String inicio = (Math.abs(primerX - minX) < step * 1.5) ? "-∞" : String.format("%.2f", primerX);
                String fin = (Math.abs(ultimoX - maxX) < step * 1.5) ? "+∞" : String.format("%.2f", ultimoX);
                resultado.agregarMensajeAccesibilidad(String.format(
                        "Función irracional. Dominio restringido: [%s, %s].", inicio, fin));
            } else if (!tieneNaN) {
                resultado.agregarMensajeAccesibilidad("Función irracional de dominio continuo (posible raíz impar).");
            }
        }

        if (esRacional(expresion)) {
            resultado.agregarMensajeAccesibilidad(
                    "Esta es una función racional. Puede presentar discontinuidades o asíntotas verticales.");
        }

        if (siempreSegundaDerivadaConstante && !esRacional(expresion) && !esIrracional(expresion)
                && !esTrigonometrica(expresion) && !esLogaritmica(expresion)) {
            resultado.setPuntosInflexion(new PuntoCritico[0]);
            resultado.agregarMensajeAccesibilidad(
                    "Esta es una función cuadrática (parábola). No tiene puntos de inflexión.");
        } else if (esPolinomio(expresion)) {
            int grado = detectarGradoProbable(evaluador, minX, maxX);
            String msg = "Esta es una función polinómica";
            if (grado > 2)
                msg += " de grado " + grado;
            msg += ". Es continua y derivable en todo su dominio.";
            resultado.agregarMensajeAccesibilidad(msg);

            if (grado > 0) {
                if (grado % 2 == 0) {
                    resultado.agregarMensajeAccesibilidad("Al ser de grado par, posee al menos un extremo absoluto.");
                } else if (grado >= 3) {
                    resultado.agregarMensajeAccesibilidad("Al ser de grado impar, posee al menos una raíz real.");
                }
            }
        }

        return resultado;
    }

    /**
     * Encuentra raíces de una función derivada en un rango.
     * Método genérico que elimina duplicación de código.
     */
    private List<Double> encontrarRaices(
            FuncionDerivada funcion, double minX, double maxX, double step) {

        List<Double> raices = new ArrayList<>();
        double prevValor = funcion.calcular(minX);

        for (double x = minX + step; x <= maxX; x += step) {
            double valorActual = funcion.calcular(x);

            // Detectar cambio de signo, pero solo si no son NaN
            // (para evitar saltos en bordes de dominio, pero permitir infinitos como en
            // x^(1/3))
            if (!Double.isNaN(valorActual) && !Double.isNaN(prevValor) &&
                    Math.signum(valorActual) != Math.signum(prevValor)) {
                if (Math.abs(valorActual) > TOLERANCIA_CERO || Math.abs(prevValor) > TOLERANCIA_CERO) {
                    double raiz = biseccion(funcion, x - step, x);
                    if (!Double.isNaN(raiz)) {
                        // Evitar duplicados (especialmente en fronteras de intervalos)
                        if (raices.isEmpty() || Math.abs(raiz - raices.getLast()) > step / 2.0) {
                            raices.add(raiz);
                        }
                    }
                }
            }

            prevValor = valorActual;
        }

        return raices;
    }

    private double biseccion(FuncionDerivada funcion, double a, double b) {
        double fa = funcion.calcular(a);
        double fb = funcion.calcular(b);

        // Si ya es casi cero en los extremos, retornar el extremo
        if (Math.abs(fa) < TOLERANCIA_BISECCION)
            return a;
        if (Math.abs(fb) < TOLERANCIA_BISECCION)
            return b;

        // Verificar que hay cambio de signo y no son NaN
        if (Double.isNaN(fa) || Double.isNaN(fb) || Math.signum(fa) == Math.signum(fb)) {
            return Double.NaN;
        }

        double c = a;
        for (int i = 0; i < MAX_ITERACIONES_BISECCION; i++) {
            c = (a + b) / 2;
            double fc = funcion.calcular(c);

            // Converged
            if (Math.abs(fc) < TOLERANCIA_BISECCION) {
                return c;
            }

            // Actualizar intervalo
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }

        return c;
    }

    /**
     * Verifica si una función realmente cambia de signo alrededor de un punto.
     * Útil para validar puntos de inflexión.
     */
    private boolean verificarCambioSigno(FuncionDerivada f, double x, double h) {
        double v1 = f.calcular(x - h);
        double v2 = f.calcular(x + h);

        if (Double.isNaN(v1) || Double.isNaN(v2)) {
            return false;
        }

        // Para inflexión necesitamos un cambio de signo real (concavidad cambia)
        return Math.signum(v1) * Math.signum(v2) < 0;
    }

    private PuntoCritico[] clasificarPuntosCriticos(Evaluador evaluador, List<Double> raices) {
        if (raices == null || raices.isEmpty()) {
            return new PuntoCritico[0];
        }

        List<PuntoCritico> puntosList = new ArrayList<>();

        for (int i = 0; i < raices.size(); i++) {
            double x = raices.get(i);
            double y;
            try {
                y = evaluador.evaluar(x);
            } catch (ExpresionInvalidaException e) {
                y = Double.NaN;
            }
            // Validar que el valor sea finito para evitar reportar falsos positivos en
            // bordes de dominio
            if (!Double.isFinite(y)) {
                continue;
            }

            double segundaDerivada = segundaDerivada(evaluador, x);

            TipoPuntoCritico tipo;
            if (segundaDerivada > 1e-5) {
                tipo = TipoPuntoCritico.MINIMO;
            } else if (segundaDerivada < -1e-5) {
                tipo = TipoPuntoCritico.MAXIMO;
            } else {
                // Indeterminado, marcar como punto crítico genérico
                tipo = null;
            }

            puntosList.add(new PuntoCritico(x, y, tipo));
        }

        return puntosList.toArray(new PuntoCritico[0]);
    }

    // Crea puntos críticos con un tipo específico (o null)
    private PuntoCritico[] crearPuntosCriticos(
            Evaluador evaluador, List<Double> raices, TipoPuntoCritico tipo) {

        if (raices.isEmpty()) {
            return new PuntoCritico[0];
        }

        List<PuntoCritico> puntosList = new ArrayList<>();

        for (int i = 0; i < raices.size(); i++) {
            double x = raices.get(i);
            double y;
            try {
                y = evaluador.evaluar(x);
            } catch (ExpresionInvalidaException e) {
                y = Double.NaN;
            }

            // Validar que el valor sea finito para evitar reportar falsos positivos
            if (Double.isFinite(y)) {
                puntosList.add(new PuntoCritico(x, y, tipo));
            }
        }

        return puntosList.toArray(new PuntoCritico[0]);
    }

    // Calcula intervalos de monotonía (crecimiento/decrecimiento)
    private Intervalo[] calcularIntervalosMonotonia(Evaluador evaluador, PuntoCritico[] puntosCriticos, double minX,
            double maxX) {

        // Crear puntos de división (límites + puntos críticos)
        List<Double> divisiones = new ArrayList<>();
        divisiones.add(minX);

        for (PuntoCritico pc : puntosCriticos) {
            divisiones.add(pc.getX());
        }

        divisiones.add(maxX);

        // Calcular intervalos
        List<Intervalo> intervalos = new ArrayList<>();

        for (int i = 0; i < divisiones.size() - 1; i++) {
            double inicio = divisiones.get(i);
            double fin = divisiones.get(i + 1);
            double puntoMedio = (inicio + fin) / 2;
            double derivadaEnMedio = derivada(evaluador, puntoMedio);

            // Si es NaN, intentar buscar un punto válido en el intervalo (para funciones
            // con dominio restringido)
            if (Double.isNaN(derivadaEnMedio)) {
                for (int j = 1; j <= 4; j++) {
                    double testX = inicio + (fin - inicio) * (j / 5.0);
                    double d = derivada(evaluador, testX);
                    if (!Double.isNaN(d)) {
                        derivadaEnMedio = d;
                        break;
                    }
                }
            }

            if (Double.isNaN(derivadaEnMedio) || Math.abs(derivadaEnMedio) < TOLERANCIA_CERO) {
                continue; // Ignorar tramos constantes o fuera del dominio
            }

            TipoIntervalo tipo = (derivadaEnMedio > 0) ? TipoIntervalo.CRECIENTE : TipoIntervalo.DECRECIENTE;

            intervalos.add(new Intervalo(
                    i == 0 ? null : inicio,
                    i == divisiones.size() - 2 ? null : fin,
                    tipo));
        }

        return intervalos.toArray(new Intervalo[0]);
    }

    // Calcula intervalos de concavidad
    private Intervalo[] calcularIntervalosConcavidad(Evaluador evaluador, PuntoCritico[] puntosInflexion, double minX,
            double maxX) {

        List<Double> divisiones = new ArrayList<>();
        divisiones.add(minX);

        for (PuntoCritico pi : puntosInflexion) {
            divisiones.add(pi.getX());
        }

        divisiones.add(maxX);

        // Calcular intervalos
        List<Intervalo> intervalos = new ArrayList<>();

        for (int i = 0; i < divisiones.size() - 1; i++) {
            double inicio = divisiones.get(i);
            double fin = divisiones.get(i + 1);
            double puntoMedio = (inicio + fin) / 2;
            double segundaDerivadaEnMedio = segundaDerivada(evaluador, puntoMedio);

            // Intentar buscar punto válido si es NaN
            if (Double.isNaN(segundaDerivadaEnMedio)) {
                for (int j = 1; j <= 4; j++) {
                    double testX = inicio + (fin - inicio) * (j / 5.0);
                    double d2 = segundaDerivada(evaluador, testX);
                    if (!Double.isNaN(d2)) {
                        segundaDerivadaEnMedio = d2;
                        break;
                    }
                }
            }

            if (Double.isNaN(segundaDerivadaEnMedio) || Math.abs(segundaDerivadaEnMedio) < TOLERANCIA_CERO) {
                continue; // Ignorar tramos lineales, constantes o fuera del dominio
            }

            TipoIntervalo tipo = (segundaDerivadaEnMedio > 0) ? TipoIntervalo.CONCAVIDAD_POSITIVA
                    : TipoIntervalo.CONCAVIDAD_NEGATIVA;

            intervalos.add(new Intervalo(
                    i == 0 ? null : inicio,
                    i == divisiones.size() - 2 ? null : fin,
                    tipo));
        }

        return intervalos.toArray(new Intervalo[0]);
    }

    private double derivada(Evaluador f, double x) {
        try {
            return (f.evaluar(x + PASO_DERIVADA) - f.evaluar(x - PASO_DERIVADA)) / (2 * PASO_DERIVADA);
        } catch (ExpresionInvalidaException e) {
            return Double.NaN;
        }
    }

    private double segundaDerivada(Evaluador f, double x) {
        try {
            return (f.evaluar(x + PASO_DERIVADA) - 2 * f.evaluar(x) + f.evaluar(x - PASO_DERIVADA))
                    / (PASO_DERIVADA * PASO_DERIVADA);
        } catch (ExpresionInvalidaException e) {
            return Double.NaN;
        }
    }

    private boolean esPolinomio(String expr) {
        // Heurística simple: no tiene división por x
        String lower = expr.toLowerCase();
        if (lower.contains("/"))
            return false;

        String[] funciones = { "sin", "cos", "tan", "log", "ln", "sqrt", "asin", "acos", "atan", "abs", "exp" };
        for (String func : funciones) {
            if (lower.contains(func))
                return false;
        }
        return lower.contains("x");
    }

    private boolean esRacional(String expr) {
        return expr.contains("/") && expr.toLowerCase().contains("x");
    }

    private boolean esIrracional(String expr) {
        String lower = expr.toLowerCase();
        if (lower.contains("sqrt"))
            return true;

        // Buscar potencias no enteras (ej: ^0.5, ^(1/3), ^.2)
        // Evitamos marcar ^2, ^3 como irracionales
        if (lower.contains("^")) {
            int idx = lower.indexOf("^");
            String resto = lower.substring(idx + 1).trim();
            if (resto.startsWith("0.") || resto.startsWith(".") || resto.startsWith("(")) {
                // Heurística: si hay paréntesis después de ^, es muy probable que sea una
                // fracción
                // o una expresión compleja que solemos usar para raíces x^(1/3)
                return true;
            }
        }
        return false;
    }

    private boolean esTrigonometrica(String expr) {
        String lower = expr.toLowerCase();
        String[] funciones = { "sin", "cos", "tan", "cot", "sec", "csc", "sen" };
        for (String func : funciones) {
            if (lower.contains(func))
                return true;
        }
        return false;
    }

    private boolean esLogaritmica(String expr) {
        String lower = expr.toLowerCase();
        return lower.contains("ln") || lower.contains("log");
    }

    private enum TipoSingularidad {
        ASINTOTA, INDEFINIDO
    }

    private record Singularidad(double x, TipoSingularidad tipo) {
    }

    private void procesarSingularidades(List<Singularidad> singularidades, ResultadoAnalisis resultado) {
        if (singularidades.isEmpty())
            return;

        List<Double> asintotas = singularidades.stream()
                .filter(s -> s.tipo == TipoSingularidad.ASINTOTA)
                .map(s -> s.x).toList();
        List<Double> indefinidos = singularidades.stream()
                .filter(s -> s.tipo == TipoSingularidad.INDEFINIDO)
                .map(s -> s.x).toList();

        if (!asintotas.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    "Asíntotas verticales o divisiones por cero detectadas cerca de x = {");
            for (int i = 0; i < asintotas.size(); i++) {
                double val = asintotas.get(i);
                if (Math.abs(val) < 0.01)
                    val = 0.0;
                sb.append(String.format("%.2f", val));
                if (i < asintotas.size() - 1)
                    sb.append(", ");
            }
            sb.append("}.");
            resultado.agregarMensajeAccesibilidad(sb.toString());
        }

        if (!indefinidos.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    "La función no está definida (indeterminación) en algunas zonas, ej. cerca de x = {");
            for (int i = 0; i < Math.min(indefinidos.size(), 3); i++) {
                double val = indefinidos.get(i);
                if (Math.abs(val) < 0.01)
                    val = 0.0;
                sb.append(String.format("%.2f", val));
                if (i < Math.min(indefinidos.size(), 3) - 1)
                    sb.append(", ");
            }
            if (indefinidos.size() > 3)
                sb.append(", ...");
            sb.append("}. Esto puede ocurrir en raíces de números negativos o logaritmos de números no positivos.");
            resultado.agregarMensajeAccesibilidad(sb.toString());
        }
    }

    private List<Singularidad> identificarSingularidades(Evaluador f, double minX, double maxX) {
        List<Singularidad> singularidades = new java.util.ArrayList<>();
        double step = 0.1;
        double h = 1e-4;

        for (double x = minX; x <= maxX; x += step) {
            try {
                double val = f.evaluar(x);
                if (Double.isInfinite(val)) {
                    singularidades.add(new Singularidad(x, TipoSingularidad.ASINTOTA));
                    continue;
                }
                if (Double.isNaN(val)) {
                    singularidades.add(new Singularidad(x, TipoSingularidad.INDEFINIDO));
                    continue;
                }

                // Salto brusco
                double v1 = f.evaluar(x - h);
                double v2 = f.evaluar(x + h);
                if (!Double.isNaN(v1) && !Double.isNaN(v2) &&
                        Math.signum(v1) != Math.signum(v2) && Math.abs(v1) > 10 && Math.abs(v2) > 10) {
                    singularidades.add(new Singularidad(x, TipoSingularidad.ASINTOTA));
                }
            } catch (Exception e) {
                singularidades.add(new Singularidad(x, TipoSingularidad.INDEFINIDO));
            }
        }

        // Limpieza de duplicados
        List<Singularidad> unicas = new java.util.ArrayList<>();
        for (Singularidad s : singularidades) {
            boolean existe = false;
            for (Singularidad u : unicas) {
                if (Math.abs(s.x - u.x) < 0.3 && s.tipo == u.tipo) {
                    existe = true;
                    break;
                }
            }
            if (!existe)
                unicas.add(s);
        }
        return unicas;
    }

    private int detectarGradoProbable(Evaluador f, double minX, double maxX) {
        // Grado 1 y 2 ya se manejan por flags booleanos en analizarEnRango
        if (esDerivadaConstante(f, 3, minX, maxX))
            return 3;
        if (esDerivadaConstante(f, 4, minX, maxX))
            return 4;
        if (esDerivadaConstante(f, 5, minX, maxX))
            return 5;

        return -1; // Desconocido o grado muy alto
    }

    private boolean esDerivadaConstante(Evaluador f, int orden, double minX, double maxX) {
        double step = (maxX - minX) / 10.0;
        if (step < 0.5)
            step = 0.5;

        double valorRef = calcularDerivadaOrdenN(f, orden, minX + step);

        for (double x = minX + 2 * step; x < maxX; x += step) {
            double val = calcularDerivadaOrdenN(f, orden, x);
            // Tolerancia escalada según el valor de referencia
            double tol = Math.max(1.0, Math.abs(valorRef) * 0.1);
            if (Math.abs(val - valorRef) > tol) {
                return false;
            }
        }
        return Math.abs(valorRef) > 1e-3;
    }

    private double calcularDerivadaOrdenN(Evaluador f, int orden, double x) {
        double h = 0.1; // h más grande para reducir ruido en órdenes altos
        switch (orden) {
            case 1:
                return derivada(f, x);
            case 2:
                return segundaDerivada(f, x);
            case 3:
                return (segundaDerivada(f, x + h) - segundaDerivada(f, x - h)) / (2 * h);
            case 4:
                return (segundaDerivada(f, x + h) - 2 * segundaDerivada(f, x) + segundaDerivada(f, x - h)) / (h * h);
            case 5: {
                double d4_plus = (segundaDerivada(f, x + 2 * h) - 2 * segundaDerivada(f, x + h) + segundaDerivada(f, x))
                        / (h * h);
                double d4_minus = (segundaDerivada(f, x) - 2 * segundaDerivada(f, x - h)
                        + segundaDerivada(f, x - 2 * h)) / (h * h);
                return (d4_plus - d4_minus) / (2 * h);
            }
            default:
                return 0;
        }
    }
}
