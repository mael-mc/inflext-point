package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.util.ValidadorExpresion;
import com.espoch.inflexpoint.modelos.calculos.DerivadorSimbolico;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

public class ReproduceBug {
    public static void main(String[] args) {
        String[] exprs = {
                "1/0",
                "x/0",
                "x/(1-1)",
                "x/(x-x)",
                "0^0",
                "0^-2",
                "1/x"
        };
        for (String expr : exprs) {
            System.out.println("Testing: " + expr);
            try {
                System.out.println("  Checking Validator...");
                ValidadorExpresion.validar(expr);
                System.out.println("  Validator passed.");

                System.out.println("  Checking Symbolic...");
                // DerivadorSimbolico.derivar() catches its own errors and returns d/dx[...]
                String res = DerivadorSimbolico.derivar(expr);
                if (res.startsWith("d/dx")) {
                    System.out.println("  Symbolic detected ERROR (returned d/dx)");
                } else {
                    System.out.println("  Symbolic Result: " + res);
                }
            } catch (ExpresionInvalidaException e) {
                System.out.println("  VALIDATOR CAUGHT: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("  ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
            System.out.println("--------------------");
        }
    }
}
