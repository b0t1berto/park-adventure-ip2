module com.exemplo.projeto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.google.zxing; requires com.google.zxing.javase; requires java.desktop;

    exports com.ufrpe.parkadventure.javafx_parkadventure;
    opens com.ufrpe.parkadventure.javafx_parkadventure to javafx.fxml;

    exports com.ufrpe.parkadventure.javafx_parkadventure.controller;
    opens com.ufrpe.parkadventure.javafx_parkadventure.controller to javafx.fxml;
    exports com.ufrpe.parkadventure.javafx_parkadventure.view;
    opens com.ufrpe.parkadventure.javafx_parkadventure.view to javafx.fxml;
    exports com.ufrpe.parkadventure.javafx_parkadventure.repository;

    exports com.ufrpe.parkadventure.javafx_parkadventure.model.api;
    opens com.ufrpe.parkadventure.javafx_parkadventure.model.api to com.fasterxml.jackson.databind;
    exports com.ufrpe.parkadventure.javafx_parkadventure.model.atracao;
    exports com.ufrpe.parkadventure.javafx_parkadventure.model.ingresso;
    exports com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa;
}