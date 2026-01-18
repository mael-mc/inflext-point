package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.AnalizadorFuncion;
import com.espoch.inflexpoint.modelos.calculos.ResultadoAnalisis;

public class ReproduceBug {
    public static void main(String[] args) {
        AnalizadorFuncion analizador = new AnalizadorFuncion();
        String[] exprs = { "x^3", "x^4", "x^5 - 5*x", "sin(x) + x^2" };
        for (String expr : exprs) {
            System.out.println("Testing: " + expr);
            try {
                ResultadoAnalisis res = analizador.analizarEnRango(expr, -10, 10, 0.1, true, true, true, true, true);
                System.out.println("  Expr: " + expr);
                System.out.println("  Crit Pts: " + res.getPuntosCriticos().length);
                System.out.println("  Inflex Pts: " + res.getPuntosInflexion().length);
                for (var msg : res.getMensajesAccesibilidad()) {
                    System.out.println("  MSG: " + msg);
                }
            } catch (Exception e) {
                System.out.println("  FAILED: " + e.getMessage());
                // e.printStackTrace();
            }
            System.out.println("--------------------");
        }
    }
}
