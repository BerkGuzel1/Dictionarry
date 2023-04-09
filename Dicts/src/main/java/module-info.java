module com.dicts {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.xml;


    opens com.dicts to javafx.fxml;
    exports com.dicts;
}