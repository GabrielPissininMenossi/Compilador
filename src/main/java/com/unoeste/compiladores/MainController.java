package com.unoeste.compiladores;

import com.unoeste.compiladores.entities.*;
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
import java.util.*;

public class MainController implements Initializable {

    public TableView<Token> tableView;
    public TableColumn<Token, String> colLexema;
    public TableColumn<Token, String> colToken;
    public TableColumn<Token, Integer> colLinha;
    public TableColumn<Token, Integer> colColuna;
    public TableColumn<Token, String> colTipo;
    public TableColumn<Token, String> colValor;
    @FXML
    public TextArea logErro;
    @FXML
    private StackPane editor;

    private CodeArea codeArea;

    private ObservableList<Token> sucessos = FXCollections.observableArrayList();

    private Lexica lexica;
    private boolean claro = true;

    List<Token> tokensColoracao = new ArrayList<>();
    private List<Erro> erroList = new ArrayList<>();

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

        lexica = new Lexica(sucessos, erroList);

        tableView.setPlaceholder(new Label(""));
        colToken.setCellValueFactory(new PropertyValueFactory<>("token"));
        colLexema.setCellValueFactory(new PropertyValueFactory<>("lexema"));
        colLinha.setCellValueFactory(new PropertyValueFactory<>("linha"));
        colColuna.setCellValueFactory(new PropertyValueFactory<>("coluna"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        //tableView.setItems(sucessos);

        //chama função a cada alteração no codeArea
        codeArea.multiPlainChanges().subscribe(change -> colorirEnquantoDigita());
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
        logErro.clear();

        int tamanhoTexto = codeArea.getParagraphs().size();
        int i = 0;
        int posLinha = 1, posColuna = 1;
        while(i < tamanhoTexto)
        {
            String linha = codeArea.getParagraph(i).getText();
            if (!linha.isEmpty())
            {
                posColuna = lexica.separarCadeias(linha, i + 1, sucessos);
                posLinha = i + 1;
            }
            i++;
        }

        Token ultimoToken = new Token("$", "EOF", posLinha, posColuna);
        lexica.getTokens().add(ultimoToken);
        coloracaoSintatica(sucessos);

        tableView.setItems(sucessos);// conecta tabela aos tokens

        Sintatico sintatico = new Sintatico(lexica, erroList);
        sintatico.analisarSintatico();

        Semantica semantica = new Semantica(lexica, erroList);
        if (erroList.size() == 1)
            semantica.analisarSemantico();
        limparCores();
        exibirLogErro(codeArea);
    }
    public void exibirLogErro(CodeArea codeArea)
    {
        int i = 0;
        boolean flag = false;
        while (i < erroList.size())
        {
            if (!flag)
            {
                if (erroList.get(i).getLinha() > 0)
                    codeArea.setParagraphStyle(erroList.get(i).getLinha() - 1,  Collections.singleton("erro-linha"));
                codeArea.multiPlainChanges().subscribe(change -> {

                    if (!erroList.isEmpty()) {

                        int linhaErro = erroList.get(0).getLinha() - 1;
                        int linhaAtual = codeArea.getCurrentParagraph();

                        if (linhaErro == linhaAtual) {
                            codeArea.setParagraphStyle(linhaErro, Collections.emptyList());
                        }
                    }
                });
                flag = true;
            }
            logErro.appendText(erroList.get(i).getMensagem());
            i++;
        }
    }
    private void limparCores()
    {
        int i = 0;
        while (i < codeArea.getParagraphs().size())
        {
            codeArea.setParagraphStyle(i, Collections.emptyList());
            i++;
        }

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