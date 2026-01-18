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
            if (calcInflexion) {
                List<Double> raicesSegundaDerivada = encontrarRaices(
                        x -> segundaDerivada(evaluador, x), minX, maxX, step);
                puntosInflexion = crearPuntosCriticos(
                        evaluador, raicesSegundaDerivada, TipoPuntoCritico.INFLEXION);
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
        boolean siempreDerivadaCero = true;
        boolean siempreSegundaDerivadaCero = true;

        for (double x = minX; x <= maxX; x += step) {
            if (Math.abs(derivada(evaluador, x)) >= TOLERANCIA_CERO) {
                siempreDerivadaCero = false;
            }
            if (Math.abs(segundaDerivada(evaluador, x)) >= TOLERANCIA_CERO) {
                siempreSegundaDerivadaCero = false;
            }
        }

        if (siempreDerivadaCero) {
            resultado.setPuntosCriticos(new PuntoCritico[0]);
            resultado.setIntervalosCrecimiento(new Intervalo[0]);
            resultado.setIntervalosDecrecimiento(new Intervalo[0]);
            resultado.setPuntosInflexion(new PuntoCritico[0]);
            resultado.setIntervalosConcavidad(new Intervalo[0]);
            resultado.agregarMensajeAccesibilidad(
                    "Esta es una función constante. No tiene puntos críticos, extremos ni intervalos de crecimiento/decrecimiento.");
        } else if (siempreSegundaDerivadaCero) {
            resultado.setPuntosInflexion(new PuntoCritico[0]);
            resultado.setIntervalosConcavidad(new Intervalo[0]);
            // Una lineal tampoco tiene puntos críticos (máximos/mínimos locales)
            resultado.setPuntosCriticos(new PuntoCritico[0]);
            resultado.agregarMensajeAccesibilidad(
                    "Esta es una función lineal. No tiene puntos de inflexión, críticos ni concavidad definida.");
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

            // Detectar cambio de signo, pero solo si no son ambos ruidosos (cercanos a
            // cero)
            if (Math.signum(valorActual) != Math.signum(prevValor)) {
                if (Math.abs(valorActual) > TOLERANCIA_CERO || Math.abs(prevValor) > TOLERANCIA_CERO) {
                    double raiz = biseccion(funcion, x - step, x);
                    if (!Double.isNaN(raiz)) {
                        raices.add(raiz);
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

        // Verificar que hay cambio de signo
        if (fa * fb >= 0) {
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

    private PuntoCritico[] clasificarPuntosCriticos(Evaluador evaluador, List<Double> raices) {
        if (raices.isEmpty()) {
            return new PuntoCritico[0];
        }

        PuntoCritico[] puntos = new PuntoCritico[raices.size()];

        for (int i = 0; i < raices.size(); i++) {
            double x = raices.get(i);
            double y;
            try {
                y = evaluador.evaluar(x);
            } catch (ExpresionInvalidaException e) {
                y = Double.NaN;
            }
            double segundaDerivada = segundaDerivada(evaluador, x);

            TipoPuntoCritico tipo;
            if (segundaDerivada > 0) {
                tipo = TipoPuntoCritico.MINIMO;
            } else if (segundaDerivada < 0) {
                tipo = TipoPuntoCritico.MAXIMO;
            } else {
                // Indeterminado, marcar como punto crítico genérico
                tipo = null; // Se podría tener un tipo INDETERMINADO
            }

            puntos[i] = new PuntoCritico(x, y, tipo);
        }

        return puntos;
    }

    // Crea puntos críticos con un tipo específico (o null)
    private PuntoCritico[] crearPuntosCriticos(
            Evaluador evaluador, List<Double> raices, TipoPuntoCritico tipo) {

        if (raices.isEmpty()) {
            return new PuntoCritico[0];
        }

        PuntoCritico[] puntos = new PuntoCritico[raices.size()];

        for (int i = 0; i < raices.size(); i++) {
            double x = raices.get(i);
            double y;
            try {
                y = evaluador.evaluar(x);
            } catch (ExpresionInvalidaException e) {
                y = Double.NaN;
            }
            puntos[i] = new PuntoCritico(x, y, tipo);
        }

        return puntos;
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

            if (Math.abs(derivadaEnMedio) < TOLERANCIA_CERO) {
                continue; // Ignorar tramos constantes
            }

            TipoIntervalo tipo = (derivadaEnMedio > 0) ? TipoIntervalo.CRECIENTE : TipoIntervalo.DECRECIENTE;

            intervalos.add(new Intervalo(
                    i == 0 ? null : inicio, // null para -∞
                    i == divisiones.size() - 2 ? null : fin, // null para +∞
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

            if (Math.abs(segundaDerivadaEnMedio) < TOLERANCIA_CERO) {
                continue; // Ignorar tramos lineales o constantes
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
}
