module com.unoeste.compiladores {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires java.sql;


    opens com.unoeste.compiladores to javafx.fxml;
    exports com.unoeste.compiladores;
}