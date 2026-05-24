package com.unoeste.compiladores.entities;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SalvadorDeCodigo {

    public boolean salvarCodigo(List<String> codigoAssembly) {
        // Cria a janela de diálogo para o usuário escolher onde salvar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar código Assembly como...");

        // Define o filtro para mostrar apenas (ou focar em) arquivos .asm
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Arquivos ASM (*.ASM)", "asm");
        fileChooser.setFileFilter(filtro);

        // Exibe a janela de "Salvar" e captura a resposta do usuário
        int escolhaUsuario = fileChooser.showSaveDialog(null);

        // Se o usuário clicou em "Salvar"
        if (escolhaUsuario == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            String caminhoArquivo = arquivoSelecionado.getAbsolutePath();

            // Garante que o arquivo salvo terá a extensão .asm
            if (!caminhoArquivo.toLowerCase().endsWith(".asm")) {
                arquivoSelecionado = new File(caminhoArquivo + ".asm");
            }

            // Inicia a escrita do arquivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoSelecionado))) {
                for (String linha : codigoAssembly) {
                    // Usa apenas write(), pois você informou que a string já tem o final de linha (\n ou \r\n)
                    writer.write(linha);
                }
                return true; // Salvo com sucesso

            } catch (IOException e) {
                System.err.println("Erro ao tentar escrever no arquivo: " + e.getMessage());
                return false; // Erro de I/O (permissão, disco cheio, etc)
            }
        }

        // Retorna false se o usuário cancelar a operação fechando a janela
        return false;
    }
}
