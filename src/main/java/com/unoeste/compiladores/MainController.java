package com.unoeste.compiladores;

import com.unoeste.compiladores.entities.Lexica;
import com.unoeste.compiladores.entities.Token;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    public TableView<Token> tableView;
    public TableColumn<Token, String> colLexema;
    public TableColumn<Token, String> colToken;
    public TableColumn<Token, Integer> colLinha;
    public TableColumn<Token, Integer> colColuna;
    @FXML
    public TextArea logErro;
    @FXML
    private StackPane editor;

    private CodeArea codeArea;

    private ObservableList<Token> sucessos = FXCollections.observableArrayList();

    private Lexica lexica;

    private boolean claro = true;

    List<Token> tokensColoracao = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        //codeArea.setStyle("-fx-font-family: 'Courier New';" + "-fx-font-size: 16px;");
        codeArea.getStyleClass().add("editor");
        codeArea.getStyleClass().add("styled-text-area");

        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
        editor.getChildren().add(scrollPane);

        logErro.setStyle("-fx-text-fill: red;" + "-fx-font-size: 12px;");

        lexica = new Lexica(sucessos, logErro);

        tableView.setPlaceholder(new Label(""));
        colToken.setCellValueFactory(new PropertyValueFactory<>("token"));
        colLexema.setCellValueFactory(new PropertyValueFactory<>("lexema"));
        colLinha.setCellValueFactory(new PropertyValueFactory<>("linha"));
        colColuna.setCellValueFactory(new PropertyValueFactory<>("coluna"));
        //tableView.setItems(sucessos);

        //chama função a cada alteração no codeArea
        codeArea.multiPlainChanges()
                .subscribe(change -> colorirEnquantoDigita());
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
            try
            {
                //lê conteudo do arquivo
                String conteudo = Files.readString(file.toPath());

                codeArea.replaceText(conteudo);
            }
            catch (IOException e)
            {
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
            try
            {
                // Pega o texto atual do editor e grava no arquivo selecionado
                Files.writeString(file.toPath(), codeArea.getText());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onAnalisarLexico(ActionEvent actionEvent)
    {
        sucessos.clear();
        lexica.limparListas();

        int tamanhoTexto = codeArea.getParagraphs().size();
        int i = 0;
        while(i < tamanhoTexto)
        {
            String linha = codeArea.getParagraph(i).getText();
            if (!linha.isEmpty())
                lexica.separarCadeias(linha, i + 1, sucessos);
            i++;
        }
        coloracaoSintatica(sucessos);

        tableView.setItems(sucessos);// conecta tabela aos tokens

        lexica.exibirLogErro(codeArea);
    }

    public void colorirEnquantoDigita()
    {
        tokensColoracao.clear();

        int tamanhoTexto = codeArea.getParagraphs().size();

        for(int i = 0; i < tamanhoTexto; i++)
        {
            String linha = codeArea.getParagraph(i).getText();

            if(!linha.isEmpty())
                lexica.separarCadeias(linha, i+1, tokensColoracao);
        }

        coloracaoSintatica(tokensColoracao);
    }


    public void coloracaoSintatica(List<Token> list_tokens)
    {
        int i = 0;
        Token token;
        String categoria;

        while (i < list_tokens.size())
        {
            token = list_tokens.get(i);

            categoria = token.verificarColoracaoToken();
            token.colorirToken(categoria, codeArea);

            i++;
        }
    }

    public void onClaro(ActionEvent actionEvent) {
        Scene scene = editor.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/claro.css")).toExternalForm());

        // Mudar o tableView
        tableView.setStyle("-fx-text-background-color: black;");
        claro = true;
        mudarTema();
    }

    public void onEscuro(ActionEvent actionEvent) {
        Scene scene = editor.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/escuro.css")).toExternalForm());

        // Mudar o tableView
        tableView.setStyle("-fx-text-background-color: white;");
        claro = false;
        mudarTema();
    }

    public void mudarTema(){
        // Mudar o plano de fundo do editor de código
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        //codeArea.getStyleClass().add("code-area");
        coloracaoSintatica(lexica.getTokens());
    }
}