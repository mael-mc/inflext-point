module com.espoch.inflexpoint {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.espoch.inflexpoint.app to javafx.fxml;
    exports com.espoch.inflexpoint.app;
    exports com.espoch.inflexpoint.controladores.vistaprincipal;
    // exports com.espoch.inflexpoint.controladores.paneles;
}
