package park_adventure.controller;

import java.time.LocalDate;

import park_adventure.model.api.Feriados;
import park_adventure.model.ingresso.Ingresso;
import park_adventure.model.ingresso.TipoIngresso;
import park_adventure.model.pessoa.Visitante;

// Emissão de ingressos com acréscimo de preço em feriados
public class Cadastro {
    private final ControleIngressos controleIngressos;
    private final double precoDiaria;
    private final double precoPasseAnual;
    private final double acrescimoFeriadoPercentual;

    public Cadastro(ControleIngressos controleIngressos, double precoDiaria,
                    double precoPasseAnual, double acrescimoFeriadoPercentual) {
        this.controleIngressos = controleIngressos;
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

    public Ingresso emitirIngresso(Visitante visitante, TipoIngresso tipo, LocalDate dataCompra) {
        double valor;
        LocalDate validade;
        if (tipo == TipoIngresso.PASSE_ANUAL) {
            valor = precoPasseAnual;
            validade = dataCompra.plusYears(1);
        } else {
            valor = precoDiaria;
            validade = dataCompra;
            if (ehFeriado(dataCompra)) {
                valor += valor * acrescimoFeriadoPercentual / 100.0;
            }
        }
        Ingresso ingresso = new Ingresso(visitante, tipo, dataCompra, validade, valor);
        controleIngressos.adicionarIngresso(ingresso);
        return ingresso;
    }
}
