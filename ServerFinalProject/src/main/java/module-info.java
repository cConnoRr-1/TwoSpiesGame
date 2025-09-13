module com.example.serverfinalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;


    opens com.example.serverfinalproject to javafx.fxml;
    exports com.example.serverfinalproject;
}