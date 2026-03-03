package com.unoeste.compiladores;

import com.unoeste.compiladores.entities.Lexica;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane editor;

    private CodeArea codeArea;

    private Lexica lexica = new Lexica();
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setStyle("-fx-font-family: 'Consolas';" + "-fx-font-size: 16px;");
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
        editor.getChildren().add(scrollPane);
    }

    public void onAbrir(ActionEvent actionEvent)
    {
        //seletor de arquivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir arquivo de texto");

        //filtrar arquivos permitidos
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivo de Texto", "*.txt","*.java","*.c")
        );

        //abre janela
        Window stage = editor.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if(file != null)
        {
            try{
                //lê conteudo do arquivo
                String conteudo = Files.readString(file.toPath());

                codeArea.replaceText(conteudo);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void onSalvar(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo");
        File file = fileChooser.showSaveDialog(editor.getScene().getWindow());

        if (file != null) {
            try {
                // Pega o texto atual do editor e grava no arquivo selecionado
                Files.writeString(file.toPath(), codeArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void onAnalisarLexico(ActionEvent actionEvent)
    {
        int tamanhoTexto = ((java.util.List<?>) codeArea.getParagraphs()).size();
        int i = 0;
        String linha;
        while(i < tamanhoTexto)
        {

            linha = codeArea.getParagraph(i).getText();
            if (!linha.isEmpty())
                lexica.separaTokens(linha);
            i++;

        }
    }
}