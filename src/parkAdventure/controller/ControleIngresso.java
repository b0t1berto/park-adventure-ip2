package parkAdventure.controller;

import java.time.LocalDate;

import parkAdventure.model.api.Feriados;
import parkAdventure.model.ingresso.Ingresso;
import parkAdventure.model.ingresso.TipoIngresso;
import parkAdventure.model.pessoa.Visitante;
import parkAdventure.repository.ListaAtracao;
import parkAdventure.repository.ListaIngresso;

// Emissão de ingressos com acréscimo de preço em feriados
public class ControleIngresso {
    private final ListaIngresso ingressos;
    private final ListaAtracao atracoes;
    private final double precoDiaria;
    private final double precoPasseAnual;
    private final double acrescimoFeriadoPercentual;

    public ControleIngresso(ListaIngresso ingressos, ListaAtracao atracoes, double precoDiaria,
                            double precoPasseAnual, double acrescimoFeriadoPercentual) {
        this.ingressos = ingressos;
        this.atracoes = atracoes;
        this.precoDiaria = precoDiaria;
        this.precoPasseAnual = precoPasseAnual;
        this.acrescimoFeriadoPercentual = acrescimoFeriadoPercentual;
    }

    // Se a API estiver fora do ar considera que não é feriado
    public boolean ehFeriado(LocalDate data) {
        try {
            String dataTexto = data.toString();
            return Feriados.buscarFeriadosBrasilAPI(data.getYear()).stream()
                    .anyMatch(f -> f.date().equals(dataTexto));
        } catch (Exception e) {
            return false;
        }
    }

    // REQ19: capacidade total diária = soma das capacidades das atrações
    public int capacidadeDiariaParque() {
        return atracoes.capacidadeTotal();
    }

    public boolean diariaEsgotada(LocalDate data) {
        return ingressos.contarDiariasPorData(data) >= capacidadeDiariaParque();
    }

    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra) {
        return emitirIngresso(visitante, tipo, dataCompra, tipo == TipoIngresso.DIARIA && ehFeriado(dataCompra));
    }

    // Para quem já consultou a API ou quer simular um feriado.
    // Lança IllegalStateException se a capacidade diária do parque já foi atingida (REQ19).
    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra, boolean feriado) {
        double valor;
        LocalDate validade;
        if (tipo == TipoIngresso.PASSE_ANUAL) {
            valor = precoPasseAnual;
            validade = dataCompra.plusYears(1);
        } else {
            if (diariaEsgotada(dataCompra)) {
                throw new IllegalStateException("Capacidade diária do parque atingida ("
                        + capacidadeDiariaParque() + " ingressos) para a data escolhida.");
            }
            valor = precoDiaria;
            validade = dataCompra;
            if (feriado) {
                valor += valor * acrescimoFeriadoPercentual / 100.0;
            }
        }
        Ingresso ingresso = new Ingresso(visitante, tipo, dataCompra, validade, valor);
        ingressos.cadastrar(ingresso);
        return ingresso;
    }
}
