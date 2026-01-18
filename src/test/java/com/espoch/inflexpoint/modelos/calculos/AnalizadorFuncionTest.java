package com.espoch.inflexpoint.modelos.calculos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnalizadorFuncionTest {

    @Test
    public void testAnalisisBasico() throws Exception {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        // f(x) = x^2 - 4
        // Derivada: 2x. Raíz en x=0.
        // Mínimo en (0, -4).

        ResultadoAnalisis resultado = analizador.analizarEnRango("x^2 - 4", -5, 5, 0.1, true, false, true, false,
                false);

        assertNotNull(resultado.getPuntosCriticos());
        assertTrue(resultado.getPuntosCriticos().length > 0);

        // Verificar punto crítico cercano a 0
        boolean foundZero = false;
        for (var pc : resultado.getPuntosCriticos()) {
            if (Math.abs(pc.getX()) < 0.1) {
                foundZero = true;
                assertEquals(com.espoch.inflexpoint.modelos.enumeraciones.TipoPuntoCritico.MINIMO,
                        pc.getTipoPuntoCritico());
            }
        }
        assertTrue(foundZero, "Debe encontrar el mínimo en x=0");
    }

    @Test
    public void testRangoPersonalizado() throws Exception {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        // f(x) = (x-20)^2
        // El mínimo está en x=20, fuera del rango [-10, 10]

        ResultadoAnalisis resDefecto = analizador.analizarEnRango("(x-20)^2", -10, 10, 0.1, true, false, true, false,
                false);
        assertEquals(0, resDefecto.getPuntosCriticos().length, "No debería encontrar puntos en [-10, 10]");

        ResultadoAnalisis resExtendido = analizador.analizarEnRango("(x-20)^2", 15, 25, 0.1, true, false, true, false,
                false);
        assertTrue(resExtendido.getPuntosCriticos().length > 0, "Debería encontrar el punto en [15, 25]");
    }

    @Test
    public void testFuncionesLinealesCombinadas() throws Exception {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        // f(x) = 2*x + 5*x + 3 = 7x + 3
        ResultadoAnalisis res = analizador.analizarEnRango("2*x + 5*x + 3", -10, 10, 0.1, true, true, true, true, true);

        assertEquals(0, res.getPuntosCriticos().length, "Lineal combinada no tiene puntos críticos");
        assertEquals(0, res.getPuntosInflexion().length, "Lineal combinada no tiene inflexión");
        assertTrue(res.getIntervalosCrecimiento().length > 0, "Debe ser creciente (7x)");
        assertEquals(0, res.getIntervalosDecrecimiento().length, "No debe ser decreciente");
        assertEquals(0, res.intervalosConcavidad().length, "Lineal combinada no tiene concavidad");
    }

    @Test
    public void testFuncionesLinealesNegativas() throws Exception {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        // f(x) = -2*x
        ResultadoAnalisis res = analizador.analizarEnRango("-2*x", -10, 10, 0.1, true, true, true, true, true);

        assertEquals(0, res.getPuntosCriticos().length, "Lineal negativa no tiene puntos críticos");
        assertEquals(0, res.getPuntosInflexion().length, "Lineal negativa no tiene inflexión");
        assertEquals(0, res.getIntervalosCrecimiento().length, "No debe ser creciente");
        assertTrue(res.getIntervalosDecrecimiento().length > 0, "Debe ser decreciente");
    }

    @Test
    public void testFuncionesCuadraticas() throws Exception {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        // f(x) = x^2 + x
        // f'(x) = 2x + 1 -> Raíz en x = -0.5 (Mínimo)
        // f''(x) = 2 -> Siempre positiva (Cóncava hacia arriba, no hay inflexión)
        ResultadoAnalisis res = analizador.analizarEnRango("x^2 + x", -10, 10, 0.1, true, true, true, true, true);

        assertTrue(res.getPuntosCriticos().length > 0, "Debe encontrar el mínimo en x = -0.5");
        assertEquals(0, res.getPuntosInflexion().length, "No debe haber puntos de inflexión");
        assertTrue(res.getIntervalosCrecimiento().length > 0, "Debe haber intervalo creciente");
        assertTrue(res.getIntervalosDecrecimiento().length > 0, "Debe haber intervalo decreciente");
        assertTrue(res.intervalosConcavidad().length > 0, "Debe haber concavidad definida");
    }
}
