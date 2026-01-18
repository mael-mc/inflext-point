package com.espoch.inflexpoint.util;

import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

public class FormulaRenderer {

    public static WebView render(String latex) {
        WebView webView = new WebView();
        // Altura mínima mayor para evitar cortes y asegurar visibilidad
        webView.setPrefHeight(90);
        webView.setMinHeight(85);
        webView.setMaxHeight(200);
        webView.setStyle("-fx-background-color: transparent;");

        WebEngine engine = webView.getEngine();

        // Escapar comillas simples en el latex para el JS
        String safeLatex = latex.replace("'", "\\'");

        String html = "<html>" +
                "<head>" +
                "<script type=\"text/javascript\">" +
                "  window.MathJax = {" +
                "    tex2jax: {inlineMath: [['$','$'], ['\\\\(','\\\\)']]}," +
                "    displayAlign: 'left'," +
                "    displayIndent: '0em'," +
                "    AuthorInit: function () {" +
                "      MathJax.Hub.Register.StartupHook('End', function () {" +
                "        document.getElementById('fallback').style.display = 'none';" +
                "        document.getElementById('content').style.visibility = 'visible';" +
                "      });" +
                "    }" +
                "  };" +
                "</script>" +
                "<script type=\"text/javascript\" async src=\"https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML\"></script>"
                +
                "<style>" +
                "  body { background-color: transparent; color: #1c6760; font-family: 'Segoe UI', sans-serif; margin: 0; padding: 10px; overflow: hidden; }"
                +
                "  #content { visibility: hidden; }" +
                "  #fallback { " +
                "    display: block; " +
                "    font-family: 'Consolas', monospace; " +
                "    font-size: 14px; " +
                "    color: #1d6861; " +
                "    background: #f2fbf9; " +
                "    padding: 8px; " +
                "    border-radius: 4px; " +
                "    border: 1px solid #d3f4ed; " +
                "  }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "  <div id=\"fallback\">" + latex + "</div>" +
                "  <div id=\"content\">" +
                "    $$ " + latex + " $$" +
                "  </div>" +
                "  <script>" +
                "    // Si MathJax no carga en 3 segundos, mostrar fallback confirmadamente" +
                "    setTimeout(function() {" +
                "      if (typeof MathJax === 'undefined' || !MathJax.isReady) {" +
                "        document.getElementById('fallback').style.display = 'block';" +
                "        document.getElementById('content').style.display = 'none';" +
                "      }" +
                "    }, 3000);" +
                "  </script>" +
                "</body>" +
                "</html>";

        engine.loadContent(html);
        return webView;
    }
}
