package com.ufrpe.parkadventure.javafx_parkadventure.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Entrada e saída do console, compartilhadas por todas as views
public class ConsoleView {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Scanner scanner = new Scanner(System.in);

    public void pularLinha() {
        System.out.println();
    }

    public void exibir(String mensagem) {
        System.out.println(mensagem);
    }

    public void exibirTitulo(String titulo) {
        System.out.println("\n=== " + titulo + " ===");
    }

    public String formatar(LocalDate data) {
        return data.format(FORMATO_DATA);
    }

    public String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("O valor não pode ficar vazio.");
        }
    }

    public String lerTextoOpcional(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    // Devolve a data como texto dd/MM/aaaa, já validada
    public String lerData(String mensagem) {
        while (true) {
            String valor = lerTexto(mensagem);
            try {
                LocalDate.parse(valor, FORMATO_DATA);
                return valor;
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use dd/MM/aaaa.");
            }
        }
    }

    // Enter em branco usa a data padrão
    public LocalDate lerDataOpcional(String mensagem, LocalDate padrao) {
        while (true) {
            String valor = lerTextoOpcional(mensagem + " [" + formatar(padrao) + "]: ");
            if (valor.isEmpty()) return padrao;
            try {
                return LocalDate.parse(valor, FORMATO_DATA);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use dd/MM/aaaa.");
            }
        }
    }

    public int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    public int lerInteiroPositivo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor > 0) return valor;
            System.out.println("O valor deve ser maior que zero.");
        }
    }

    public int lerInteiroNaoNegativo(String mensagem) {
        while (true) {
            int valor = lerInteiro(mensagem);
            if (valor >= 0) return valor;
            System.out.println("O valor não pode ser negativo.");
        }
    }

    public boolean lerSimNao(String mensagem) {
        while (true) {
            String resposta = lerTexto(mensagem);
            if (resposta.equalsIgnoreCase("s")) return true;
            if (resposta.equalsIgnoreCase("n")) return false;
            System.out.println("Responda com 's' ou 'n'.");
        }
    }

    public String vazioParaNull(String valor) {
        return valor == null || valor.isBlank() ? null : valor;
    }
}
