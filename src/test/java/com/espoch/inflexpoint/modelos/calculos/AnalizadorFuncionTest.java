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
}
