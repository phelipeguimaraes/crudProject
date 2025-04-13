module com.example.crudproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.crudproject to javafx.fxml;
    exports com.example.crudproject;
    exports com.example.crudproject.model;
    opens com.example.crudproject.model to javafx.fxml;
    exports com.example.crudproject.controller;
    opens com.example.crudproject.controller to javafx.fxml;
}