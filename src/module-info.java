module com.exemplo.projeto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;

    exports com.ufrpe.parkadventure.javafx_parkadventure;
    opens com.ufrpe.parkadventure.javafx_parkadventure to javafx.fxml;

    exports park_adventure;
    exports park_adventure.controller;
    opens park_adventure.controller to javafx.fxml;
    exports park_adventure.view;
    opens park_adventure.view to javafx.fxml;
    exports park_adventure.repository;

    exports park_adventure.model.api;
    opens park_adventure.model.api to com.fasterxml.jackson.databind;
    exports park_adventure.model.atracao;
    exports park_adventure.model.ingresso;
    exports park_adventure.model.pessoa;
}