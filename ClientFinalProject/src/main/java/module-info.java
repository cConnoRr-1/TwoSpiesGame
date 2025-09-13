module com.example.clientfinalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;


    opens com.example.clientfinalproject to javafx.fxml;
    exports com.example.clientfinalproject;
}