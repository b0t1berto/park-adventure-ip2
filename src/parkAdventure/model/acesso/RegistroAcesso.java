package parkAdventure.model.acesso;

import java.time.LocalDateTime;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.ingresso.Ingresso;
import parkAdventure.model.pessoa.Visitante;

// REQ10: vínculo entre visitante, ingresso e atração (histórico de acessos)
public class RegistroAcesso {
    private final Visitante visitante;
    private final Ingresso ingresso;
    private final Atracao atracao;
    private final String nomeAtracao;
    private final LocalDateTime entrada;
    private LocalDateTime saida;

    public RegistroAcesso(Visitante visitante, Ingresso ingresso, Atracao atracao, LocalDateTime entrada) {
        this.visitante = visitante;
        this.ingresso = ingresso;
        this.atracao = atracao;
        this.nomeAtracao = atracao.getNome();
        this.entrada = entrada;
    }

    public boolean estaAtivo() { return saida == null; }

    public void finalizar(LocalDateTime saida) { this.saida = saida; }

    public Visitante getVisitante() { return visitante; }
    public Ingresso getIngresso() { return ingresso; }
    public Atracao getAtracao() { return atracao; }
    public String getNomeAtracao() { return nomeAtracao; }
    public LocalDateTime getEntrada() { return entrada; }
    public LocalDateTime getSaida() { return saida; }
}
