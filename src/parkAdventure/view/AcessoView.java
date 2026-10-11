package parkAdventure.view;

import parkAdventure.controller.ResultadoAcesso;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.pessoa.Visitante;

public class AcessoView {

    public void exibirResultado(Visitante visitante, Atracao atracao, ResultadoAcesso resultado) {
        String nome = visitante.getNome();
        switch (resultado) {
            case AUTORIZADO -> System.out.println("Aproveite o brinquedo " + atracao.getNome() + ", " + nome + "!");
            case NA_FILA -> System.out.println(nome + " entrou na fila virtual de " + atracao.getNome() + ".");
            case FILA_CHEIA -> System.out.println("A fila de " + atracao.getNome() + " está cheia. " + nome + " não pôde entrar.");
            case INGRESSO_DE_OUTRO_VISITANTE -> System.out.println("O ingresso apresentado não pertence a " + nome + ".");
            case INGRESSO_EXPIRADO -> System.out.println("O ingresso de " + nome + " está fora da validade.");
            case FORA_DOS_REQUISITOS -> System.out.println(nome + " não atinge os requisitos mínimos para " + atracao.getNome() + "!");
            case SEM_RESPONSAVEL -> System.out.println(nome + " precisa de um responsável para entrar em " + atracao.getNome() + "!");
        }
    }

    public void exibirLiberacao(Atracao atracao, Visitante proximo) {
        if (proximo == null) {
            System.out.println("Vaga liberada em " + atracao.getNome() + ".");
        } else {
            System.out.println("Vaga liberada em " + atracao.getNome() + ". " + proximo.getNome() + " saiu da fila e entrou!");
        }
    }
}
