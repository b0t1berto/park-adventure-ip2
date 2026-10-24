package park_adventure.model.atracao;

import park_adventure.model.pessoa.Operador;
import park_adventure.model.pessoa.Visitante;

public abstract class Atracao implements IRestricao {
    private static final int CAPACIDADE_FILA_PADRAO = 50;

    private String nome;
    private int capacidadeMaxima;
    private Operador operador;
    private int alturaMinima;
    private int idadeMinima;
    private int ocupacaoAtual;
    private final FilaVirtual filaVirtual = new FilaVirtual(CAPACIDADE_FILA_PADRAO);

    protected Atracao(String nome, int capacidadeMaxima, Operador operador, int alturaMinima, int idadeMinima) {
        this.nome = nome;
        this.capacidadeMaxima = capacidadeMaxima;
        this.operador = operador;
        this.alturaMinima = alturaMinima;
        this.idadeMinima = idadeMinima;
    }

    // Regras de cada tipo; as subclasses podem sobrescrever
    protected boolean atendeRequisitos(Visitante visitante) {
        return visitante.getIdade() >= idadeMinima && visitante.getAltura() >= alturaMinima;
    }

    @Override
    public MotivoBloqueio verificarRestricao(Visitante visitante) {
        if (!atendeRequisitos(visitante)) {
            return MotivoBloqueio.FORA_DOS_REQUISITOS;
        }
        if (visitante.precisaDeResponsavel()) {
            return MotivoBloqueio.SEM_RESPONSAVEL;
        }
        return MotivoBloqueio.NENHUM;
    }

    public boolean podeEntrar(Visitante visitante) {
        return verificarRestricao(visitante) == MotivoBloqueio.NENHUM;
    }

    // Registra a entrada de uma pessoa; false se estiver lotado
    public boolean entrar() {
        if (estaLotada()) {
            return false;
        }
        ocupacaoAtual++;
        return true;
    }

    // Registra a saída de uma pessoa
    public void sair() {
        if (ocupacaoAtual > 0) {
            ocupacaoAtual--;
        }
    }

    public boolean estaLotada() { return ocupacaoAtual >= capacidadeMaxima; }

    public boolean temOperador() { return operador != null; }

    public String getTipo() { return getClass().getSimpleName(); }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }

    public Operador getOperador() { return operador; }
    public void setOperador(Operador operador) { this.operador = operador; }

    public int getAlturaMinima() { return alturaMinima; }
    public void setAlturaMinima(int alturaMinima) { this.alturaMinima = alturaMinima; }

    public int getIdadeMinima() { return idadeMinima; }
    public void setIdadeMinima(int idadeMinima) { this.idadeMinima = idadeMinima; }

    public int getOcupacaoAtual() { return ocupacaoAtual; }

    public FilaVirtual getFilaVirtual() { return filaVirtual; }
}
