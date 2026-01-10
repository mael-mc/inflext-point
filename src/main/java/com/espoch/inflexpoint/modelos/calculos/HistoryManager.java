package com.espoch.inflexpoint.modelos.calculos;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton para gestionar el historial de funciones ingresadas.
 */
public class HistoryManager {
    private static HistoryManager instance;
    private final List<String> history;

    private HistoryManager() {
        history = new ArrayList<>();
    }

    public static synchronized HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public void addExpression(String expression) {
        if (expression != null && !expression.trim().isEmpty()) {
            // No duplicar si es la última ingresada
            if (history.isEmpty() || !history.get(history.size() - 1).equals(expression)) {
                history.add(expression);
            }
        }
    }

    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
}
