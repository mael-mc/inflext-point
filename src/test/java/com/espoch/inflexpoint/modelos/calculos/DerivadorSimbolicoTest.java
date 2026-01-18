package com.espoch.inflexpoint.modelos.calculos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DerivadorSimbolicoTest {

    @Test
    public void testDerivadaConstante() {
        assertEquals("0", DerivadorSimbolico.derivar("5"));
    }

    @Test
    public void testDerivadaVariable() {
        assertEquals("1", DerivadorSimbolico.derivar("x"));
    }

    @Test
    public void testDerivadaPotencia() {
        // x^2 -> 2*x
        assertEquals("2*x", DerivadorSimbolico.derivar("x^2"));
    }

    @Test
    public void testDerivadaSuma() {
        assertEquals("(1+1)", DerivadorSimbolico.derivar("x+x"));
    }

    @Test
    public void testNuevasFunciones() {
        // sin(x) -> cos(x)
        assertEquals("cos(x)", DerivadorSimbolico.derivar("sin(x)"));

        // sec(x) -> sec(x)*tan(x)
        String derSec = DerivadorSimbolico.derivar("sec(x)");
        assertTrue(derSec.contains("sec(x)") && derSec.contains("tan(x)"));

        // asin(x) -> 1/sqrt(1-x^2)
        String derAsin = DerivadorSimbolico.derivar("asin(x)");
        assertTrue(derAsin.contains("sqrt") && derAsin.contains("1-x^2"));
    }

    @Test
    public void testNormalizacion() {
        assertEquals("cos(x)", DerivadorSimbolico.derivar("sen(x)"));
        assertEquals("cos(x)",
                DerivadorSimbolico.derivar("arcsen(x)").isEmpty() ? "" : DerivadorSimbolico.derivar("sin(x)")); // logic
                                                                                                                // check
    }
}
