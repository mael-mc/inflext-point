package com.espoch.inflexpoint.test;

import com.espoch.inflexpoint.modelos.calculos.Evaluador;
import com.espoch.inflexpoint.modelos.excepciones.ExpresionInvalidaException;

public class TestLog {
    public static void main(String[] args) {
        try {
            Evaluador evalLn = new Evaluador("ln(x)");
            System.out.println("ln(1) = " + evalLn.evaluar(1.0));
            System.out.println("ln(e) = " + evalLn.evaluar(Math.E));

            Evaluador evalLog = new Evaluador("log(x)");
            System.out.println("log(10) = " + evalLog.evaluar(10.0));

            // Probando con rangos negativos
            System.out.println("ln(-1) = " + evalLn.evaluar(-1.0));

        } catch (ExpresionInvalidaException e) {
            e.printStackTrace();
        }
    }
}
