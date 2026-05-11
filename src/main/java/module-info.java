module com.unoeste.compiladores {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.fxmisc.flowless;
    requires java.sql;
    requires jdk.compiler;


    opens com.unoeste.compiladores to javafx.fxml;
    opens com.unoeste.compiladores.entities;
    exports com.unoeste.compiladores;
    opens com.unoeste.compiladores.stacks;
    opens com.unoeste.compiladores.phases;
    opens com.unoeste.compiladores.entities.ArvoreAST;
}