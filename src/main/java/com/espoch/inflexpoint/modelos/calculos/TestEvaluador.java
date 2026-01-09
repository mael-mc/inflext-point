package com.espoch.inflexpoint.modelos.calculos;

public class TestEvaluador {
    public static void main(String[] args) {
        String[] testCases = {
                "sin(x)",
                "2sin(x)",
                "sin^2(x)",
                "cos(x)",
                "tan(x)",
                "2x",
                "x^2",
                "log(x)",
                "ln(x)"
        };

        for (String expr : testCases) {
            try {
                System.out.println("Testing: " + expr);
                Evaluador eval = new Evaluador(expr);
                double result = eval.evaluar(1.0);
                System.out.println("Result (x=1): " + result);
                System.out.println("SUCCESS");
            } catch (Exception e) {
                System.out.println("FAILED: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("-------------------");
        }
    }
}
