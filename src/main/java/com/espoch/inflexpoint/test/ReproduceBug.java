package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;

public class ReproduceBug {
    public static void main(String[] args) {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        String[] exprs = {
                "1/sqrt(x)",
                "sqrt(1/x)",
                "x/sqrt(x^2-4)"
        };
        for (String expr : exprs) {
            System.out.println("Testing: " + expr);
            try {
                ResultadoAnalisis res = analizador.analizarEnRango(expr, -5, 5, 0.1, true, true, true, true, true);
                System.out.println("  Expr: " + expr);
                for (var msg : res.getMensajesAccesibilidad()) {
                    System.out.println("  MSG: " + msg);
                }
            } catch (Exception e) {
                System.out.println("  FAILED: " + e.getMessage());
            }
            System.out.println("--------------------");
        }
    }
}
