package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.DerivadorSimbolico;
import com.espoch.inflexpoint.modelos.calculos.Evaluador;
import com.espoch.inflexpoint.util.ValidadorExpresion;

public class ReproduceBug {
    public static void main(String[] args) {
        String[] exprs = { "-2*x", "-2*x+x" };
        for (String expr : exprs) {
            System.out.println("Testing: " + expr);
            try {
                ValidadorExpresion.validar(expr);
                System.out.println("  Validation: OK");

                String d1 = DerivadorSimbolico.derivar(expr);
                System.out.println("  Symbolic D1: " + d1);

                Evaluador eval = new Evaluador(expr);
                double val = eval.evaluar(1.0);
                System.out.println("  Numeric Eval at x=1: " + val);

            } catch (Exception e) {
                System.out.println("  FAILED: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
