package com.ufrpe.parkadventure.repository;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.ingresso.Ingresso;
import com.ufrpe.parkadventure.model.ingresso.TipoIngresso;
import com.ufrpe.parkadventure.model.pessoa.Operador;
import com.ufrpe.parkadventure.model.pessoa.Responsavel;
import com.ufrpe.parkadventure.model.pessoa.Visitante;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class Persistencia {
    private Persistencia() { }

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String SEPARADOR = "\t";
    private static Path arquivo = localizarArquivo();

    private static Path localizarArquivo() {
        Path atual = Paths.get("").toAbsolutePath();
        for (Path pasta = atual; pasta != null; pasta = pasta.getParent()) {
            if (Files.exists(pasta.resolve("pom.xml"))) {
                return pasta.resolve("dados").resolve("dados.txt");
            }
        }
        return atual.resolve("dados").resolve("dados.txt");
    }

    public static void definirArquivo(Path novoArquivo) { arquivo = novoArquivo; }

    public static Path getArquivo() { return arquivo; }

    public static synchronized void carregar() {
        if (!Files.exists(arquivo)) {
            System.out.println("[Persistencia] Nenhum arquivo para carregar em: " + arquivo);
            return;
        }
        List<String> linhas;
        try {
            linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Não foi possível ler " + arquivo + ": " + e.getMessage());
            return;
        }
        for (String linha : linhas) {
            lerLinha(linha, 'V');
        }
        for (String linha : linhas) {
            lerLinha(linha, 'O');
        }
        for (String linha : linhas) {
            lerLinha(linha, 'I');
        }
    }

    private static void lerLinha(String linha, char tipo) {
        if (linha.isBlank() || linha.charAt(0) != tipo) {
            return;
        }
        String[] c = linha.split(SEPARADOR, -1);
        try {
            switch (tipo) {
                case 'V' -> lerVisitante(c);
                case 'O' -> lerOperador(c);
                case 'I' -> lerIngresso(c);
                default -> { }
            }
        } catch (RuntimeException e) {
            System.err.println("Linha ignorada no arquivo de dados: " + linha);
        }
    }

    private static void lerVisitante(String[] c) {
        Visitante visitante = new Visitante(c[1], c[2], vazioParaNulo(c[3]), Integer.parseInt(c[4]));
        if (!c[5].isEmpty()) {
            LocalDate nascimentoResp = c[6].isEmpty() ? null : LocalDate.parse(c[6], DATA);
            Responsavel responsavel = new Responsavel(c[5], nascimentoResp, vazioParaNulo(c[7]));
            if (c.length > 8) {
                responsavel.setContatoEmergencia(vazioParaNulo(c[8]));
            }
            visitante.setResponsavel(responsavel);
        }
        if (c.length > 9) {
            visitante.setEmail(vazioParaNulo(c[9]));
        }
        if (c.length > 10) {
            visitante.setTelefone(vazioParaNulo(c[10]));
        }

        Visitante existente = ListaVisitante.buscar(visitante.getNome());
        if (existente == null) {
            ListaVisitante.cadastrar(visitante);
            return;
        }
        if (existente.getEmail() == null) existente.setEmail(visitante.getEmail());
        if (existente.getTelefone() == null) existente.setTelefone(visitante.getTelefone());
        if (existente.getCpf() == null) existente.setCpf(visitante.getCpf());
        if (!existente.temResponsavel()) existente.setResponsavel(visitante.getResponsavel());
    }

    private static void lerOperador(String[] c) {
        Atracao atracao = ListaAtracao.getInstancia().buscar(c[4]);
        if (atracao != null && !atracao.temOperador()) {
            atracao.setOperador(new Operador(c[1], c[2], vazioParaNulo(c[3]), c[4]));
        }
    }

    private static void lerIngresso(String[] c) {
        Visitante visitante = ListaVisitante.buscar(c[1]);
        if (visitante == null) {
            return;
        }
        TipoIngresso tipo = TipoIngresso.valueOf(c[2]);
        LocalDate compra = LocalDate.parse(c[3], DATA);
        LocalDate validade = LocalDate.parse(c[4], DATA);
        ListaIngresso ingressos = ListaIngresso.getInstancia();
        for (Ingresso existente : ingressos.listarTodos()) {
            if (existente.getVisitante() == visitante && existente.getTipoIngresso() == tipo
                    && existente.getDataCompra().equals(compra)
                    && existente.getDataValidade().equals(validade)) {
                if (existente.getFormaPagamento() == null && c.length > 6) {
                    existente.setFormaPagamento(vazioParaNulo(c[6]));
                }
                return;
            }
        }
        Ingresso novo = new Ingresso(visitante, tipo, compra, validade, Double.parseDouble(c[5]));
        if (c.length > 6) {
            novo.setFormaPagamento(vazioParaNulo(c[6]));
        }
        ingressos.cadastrar(novo);
    }

    public static synchronized void salvar() {
        List<String> linhas = new ArrayList<>();

        for (Visitante v : ListaVisitante.getInstancia().listarTodos()) {
            Responsavel r = v.getResponsavel();
            linhas.add(String.join(SEPARADOR, "V",
                    texto(v.getNome()), data(v.getDataNascimento()), texto(v.getCpf()),
                    String.valueOf(v.getAltura()),
                    r == null ? "" : texto(r.getNome()),
                    r == null ? "" : data(r.getDataNascimento()),
                    r == null ? "" : texto(r.getCpf()),
                    r == null ? "" : texto(r.getContatoEmergencia()),
                    texto(v.getEmail()), texto(v.getTelefone())));
        }

        for (Atracao a : ListaAtracao.getInstancia().listarTodas()) {
            Operador o = a.getOperador();
            if (o != null) {
                linhas.add(String.join(SEPARADOR, "O",
                        texto(o.getNome()), data(o.getDataNascimento()), texto(o.getCpf()),
                        texto(a.getNome())));
            }
        }

        for (Ingresso i : ListaIngresso.getInstancia().listarTodos()) {
            if (i.getVisitante() == null) {
                continue;
            }
            linhas.add(String.join(SEPARADOR, "I",
                    texto(i.getVisitante().getNome()), i.getTipoIngresso().name(),
                    data(i.getDataCompra()), data(i.getDataValidade()),
                    String.valueOf(i.getValor()), texto(i.getFormaPagamento())));
        }

        try {
            Files.createDirectories(arquivo.getParent());
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
            System.out.println("[Persistencia] Dados salvos em: " + arquivo);
        } catch (IOException e) {
            System.err.println("Não foi possível salvar " + arquivo + ": " + e.getMessage());
        }
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    private static String data(LocalDate valor) {
        return valor == null ? "" : valor.format(DATA);
    }

    private static String vazioParaNulo(String valor) {
        return valor.isEmpty() ? null : valor;
    }
}
