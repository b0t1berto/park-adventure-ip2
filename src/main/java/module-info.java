module com.exemplo.projeto {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ufrpe.parkadventure to javafx.fxml;
    exports com.ufrpe.parkadventure;
}