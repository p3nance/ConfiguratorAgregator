module com.example.authapp {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.net.http;
    requires java.desktop;
    requires com.google.gson;

    opens com.example.authapp to javafx.fxml;
    opens com.example.authapp.models to javafx.fxml, com.google.gson;
    opens com.example.authapp.dto to com.google.gson;
    opens controllers to javafx.fxml;

    exports com.example.authapp;
    exports controllers;
}
