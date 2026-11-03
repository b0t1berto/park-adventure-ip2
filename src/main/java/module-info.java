module com.exemplo.projeto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.google.zxing; requires com.google.zxing.javase; requires java.desktop;

    exports com.ufrpe.parkadventure;
    opens com.ufrpe.parkadventure to javafx.fxml;

    exports com.ufrpe.parkadventure.controller;
    opens com.ufrpe.parkadventure.controller to javafx.fxml;
    exports com.ufrpe.parkadventure.view;
    opens com.ufrpe.parkadventure.view to javafx.fxml;
    exports com.ufrpe.parkadventure.repository;

    exports com.ufrpe.parkadventure.model.api;
    opens com.ufrpe.parkadventure.model.api to com.fasterxml.jackson.databind;
    exports com.ufrpe.parkadventure.model.atracao;
    exports com.ufrpe.parkadventure.model.ingresso;
    exports com.ufrpe.parkadventure.model.pessoa;
    exports com.ufrpe.parkadventure.controller.controles;
    opens com.ufrpe.parkadventure.controller.controles to javafx.fxml;
}