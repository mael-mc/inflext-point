package com.espoch.inflexpoint.util;

import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

public class FormulaRenderer {

    public static WebView render(String latex) {
        WebView webView = new WebView();
        webView.setPrefHeight(60);
        webView.setMinHeight(60);
        webView.setMaxHeight(100);
        webView.setStyle("-fx-background-color: transparent;");

        WebEngine engine = webView.getEngine();

        String html = "<html>" +
                "<head>" +
                "<script type=\"text/javascript\" async src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML\"></script>"
                +
                "<style>" +
                "body { background-color: transparent; color: #1c6760; font-family: 'Segoe UI', sans-serif; margin: 0; padding: 5px; display: flex; align-items: center; overflow: hidden; }"
                +
                ".mjx-chtml { font-size: 110% !important; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "$$ " + latex + " $$" +
                "</body>" +
                "</html>";

        engine.loadContent(html);
        return webView;
    }
}
