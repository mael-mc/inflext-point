package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.Evaluador;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

public class TestEvaluador {
    public static void main(String[] args) {
        test("sin(x)");
        test("sen(x)");
        test("cos(x)");
        test("tan(x)");
        test("csc(x)");
        test("x^2");
    }

    private static void test(String expr) {
        try {
            System.out.println("Probando: " + expr);
            Evaluador eval = new Evaluador(expr);
            double res = eval.evaluar(Math.PI / 2); // 90 grados
            System.out.println("  f(PI/2) = " + res);

            res = eval.evaluar(0);
            System.out.println("  f(0) = " + res);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("-------------------");
    }
}
