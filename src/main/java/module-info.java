module com.espoch.inflexpoint {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    opens com.espoch.inflexpoint.app to javafx.fxml;
    opens com.espoch.inflexpoint.controladores.vistaprincipal to javafx.fxml;
    opens com.espoch.inflexpoint.controladores.paneles to javafx.fxml;

    exports com.espoch.inflexpoint.app;
    exports com.espoch.inflexpoint.controladores.vistaprincipal;
    exports com.espoch.inflexpoint.controladores.paneles;
    exports com.espoch.inflexpoint.modelos.entidades;
    exports com.espoch.inflexpoint.modelos.calculos;
    exports com.espoch.inflexpoint.modelos.enumeraciones;
    exports com.espoch.inflexpoint.modelos.dao.interfaces;
    exports com.espoch.inflexpoint.modelos.dao.implementaciones;
    exports com.espoch.inflexpoint.modelos.excepciones;
}
