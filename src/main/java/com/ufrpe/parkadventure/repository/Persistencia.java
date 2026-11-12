package com.ufrpe.parkadventure.repository;

import com.ufrpe.parkadventure.controller.RepositorioAtracaoController;
import com.ufrpe.parkadventure.controller.RepositorioVisitanteController;
import com.ufrpe.parkadventure.controller.RepositorioIngressoController;
import com.ufrpe.parkadventure.controller.RepositorioEscalaTrabalhoController;
import com.ufrpe.parkadventure.model.atracao.Aquatico;
import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.atracao.EstadoReserva;
import com.ufrpe.parkadventure.model.atracao.Infantil;
import com.ufrpe.parkadventure.model.atracao.Radical;
import com.ufrpe.parkadventure.model.atracao.Reserva;
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
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class Persistencia {
    private Persistencia() { }

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String SEPARADOR = "\t";
    private static Path arquivo = localizarArquivo();
    private static boolean carregado = false;

    private static Path localizarArquivo() {
        Path atual = Paths.get("").toAbsolutePath();
        for (Path pasta = atual; pasta != null; pasta = pasta.getParent()) {
            if (Files.exists(pasta.resolve("pom.xml"))) {
                return pasta.resolve("dados").resolve("dados.txt");
            }
        }
        return atual.resolve("dados").resolve("dados.txt");
    }

    public static void definirArquivo(Path novoArquivo) {
        arquivo = novoArquivo;
        carregado = false;
    }

    public static Path getArquivo() { return arquivo; }

    public static synchronized void carregar() {
        if (carregado) {
            return;   // carregar duas vezes duplicaria visitantes, ingressos e reservas
        }
        carregado = true;
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
        // atrações primeiro: operadores e reservas dependem delas
        for (String linha : linhas) {
            lerLinha(linha, 'A');
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
        // reservas por último: dependem de visitantes e atrações
        for (String linha : linhas) {
            lerLinha(linha, 'R');
        }
        // a escala depende dos operadores, que já foram recriados
        for (String linha : linhas) {
            lerLinha(linha, 'E');
        }
    }

    private static void lerLinha(String linha, char tipo) {
        if (linha.isBlank() || linha.charAt(0) != tipo) {
            return;
        }
        String[] c = linha.split(SEPARADOR, -1);
        try {
            switch (tipo) {
                case 'A' -> lerAtracao(c);
                case 'V' -> lerVisitante(c);
                case 'O' -> lerOperador(c);
                case 'I' -> lerIngresso(c);
                case 'R' -> lerReserva(c);
                case 'E' -> lerEscala(c);
                default -> { }
            }
        } catch (RuntimeException e) {
            System.err.println("Linha ignorada no arquivo de dados: " + linha);
        }
    }

    // A <nome> <tipo> <capacidade> <altura> <idade>   (na Infantil, altura e idade são os limites máximos)
    private static void lerAtracao(String[] c) {
        String nome = c[1];
        int capacidade = Integer.parseInt(c[3]);
        int altura = Integer.parseInt(c[4]);
        int idade = Integer.parseInt(c[5]);
        Atracao atracao = switch (c[2]) {
            case "Aquatico" -> new Aquatico(nome, capacidade, null, altura, idade);
            case "Infantil" -> new Infantil(nome, capacidade, null, altura, idade);
            case "Radical" -> new Radical(nome, capacidade, null, altura, idade);
            default -> null;
        };
        if (atracao != null) {
            RepositorioAtracaoController.cadastrar(atracao);
        }
    }

    // R <visitante> <atração> <data> <horário> <estado>
    private static void lerReserva(String[] c) {
        Visitante visitante = RepositorioVisitanteController.buscarVisitante(c[1]);
        Atracao atracao = RepositorioAtracaoController.buscar(c[2]);
        if (visitante == null || atracao == null) {
            return;
        }
        atracao.getFilaVirtual().restaurar(visitante, LocalDate.parse(c[3], DATA),
                LocalTime.parse(c[4], HORA), EstadoReserva.valueOf(c[5]));
    }

    // E <data> <nome do operador> <atração em que ele trabalha>
    private static void lerEscala(String[] c) {
        Operador operador = RepositorioAtracaoController.buscarOperadorPorNome(c[2], c[3]);
        if (operador == null) {
            return;   // o operador não existe mais
        }
        RepositorioEscalaTrabalhoController.escalar(LocalDate.parse(c[1], DATA), operador);
    }

    private static void lerVisitante(String[] c) {
        Visitante visitante = new Visitante(c[1], c[2], vazioParaNulo(c[3]), Integer.parseInt(c[4]));
        if (!c[5].isEmpty()) {
            Responsavel responsavel = c[6].isEmpty()
                    ? new Responsavel(c[5], (LocalDate) null, vazioParaNulo(c[7]))
                    : new Responsavel(c[5], c[6], vazioParaNulo(c[7]));
            responsavel.setContatoEmergencia(vazioParaNulo(campo(c, 8)));
            visitante.setResponsavel(responsavel);
        }
        visitante.setEmail(vazioParaNulo(campo(c, 9)));
        visitante.setTelefone(vazioParaNulo(campo(c, 10)));
        RepositorioVisitanteController.cadastrarVisitante(visitante);
    }

    private static void lerOperador(String[] c) {
        Atracao atracao = RepositorioAtracaoController.buscar(c[4]);
        if (atracao != null && !atracao.temOperador()) {
            atracao.setOperador(new Operador(c[1], c[2], vazioParaNulo(c[3]), c[4]));
        }
    }

    private static void lerIngresso(String[] c) {
        Visitante visitante = RepositorioVisitanteController.buscarVisitante(c[1]);
        if (visitante == null) {
            return;
        }
        TipoIngresso tipo = TipoIngresso.valueOf(c[2]);
        LocalDate compra = LocalDate.parse(c[3], DATA);
        LocalDate validade = LocalDate.parse(c[4], DATA);
        Ingresso novo = new Ingresso(visitante, tipo, compra, validade, Double.parseDouble(c[5]));
        novo.setFormaPagamento(vazioParaNulo(campo(c, 6)));
        RepositorioIngressoController.cadastrarIngresso(novo);
    }

    public static synchronized void salvar() {
        List<String> linhas = new ArrayList<>();

        for (Visitante v : RepositorioVisitanteController.listarTodos()) {
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

        for (Atracao a : RepositorioAtracaoController.listarTodas()) {
            boolean infantil = a instanceof Infantil;
            linhas.add(String.join(SEPARADOR, "A", texto(a.getNome()), a.getTipo(),
                    String.valueOf(a.getCapacidadeMaxima()),
                    String.valueOf(infantil ? ((Infantil) a).getAlturaMaxima() : a.getAlturaMinima()),
                    String.valueOf(infantil ? ((Infantil) a).getIdadeMaxima() : a.getIdadeMinima())));
        }

        for (Atracao a : RepositorioAtracaoController.listarTodas()) {
            Operador o = a.getOperador();
            if (o != null) {
                linhas.add(String.join(SEPARADOR, "O",
                        texto(o.getNome()), data(o.getDataNascimento()), texto(o.getCpf()),
                        texto(a.getNome())));
            }
        }

        for (Ingresso i : RepositorioIngressoController.listarTodos()) {
            if (i.getVisitante() == null) {
                continue;
            }
            linhas.add(String.join(SEPARADOR, "I",
                    texto(i.getVisitante().getNome()), i.getTipoIngresso().name(),
                    data(i.getDataCompra()), data(i.getDataValidade()),
                    String.valueOf(i.getValor()), texto(i.getFormaPagamento())));
        }

        // reservas do fura-fila de hoje em diante (as antigas não precisam ser guardadas)
        LocalDate hoje = LocalDate.now();
        for (Atracao a : RepositorioAtracaoController.listarTodas()) {
            for (Reserva r : a.getFilaVirtual().getReservas()) {
                if (r.getVisitante() == null || r.getData().isBefore(hoje)) {
                    continue;
                }
                linhas.add(String.join(SEPARADOR, "R", texto(r.getVisitante().getNome()), texto(a.getNome()),
                        data(r.getData()), r.getHorario().format(HORA), r.getEstado().name()));
            }
        }

        for (RepositorioEscalaTrabalho.Turno turno : RepositorioEscalaTrabalhoController.listarOrdenado()) {
            Atracao daAtracao = RepositorioAtracaoController.buscarAtracaoPorOperador(turno.operador());
            if (daAtracao == null) {
                continue;
            }
            linhas.add(String.join(SEPARADOR, "E", data(turno.data()),
                    texto(turno.operador().getNome()), texto(daAtracao.getNome())));
        }

        try {
            Files.createDirectories(arquivo.getParent());
            // grava num arquivo temporário e só depois troca: se o programa cair no meio da gravação,
            // o arquivo de dados antigo continua inteiro (antes ele podia ficar pela metade)
            Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
            Files.write(temporario, linhas, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
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

    private static String campo(String[] c, int indice) {
        return indice < c.length ? c[indice] : "";
    }

    private static String vazioParaNulo(String valor) {
        return valor.isEmpty() ? null : valor;
    }
}