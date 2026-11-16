package com.ufrpe.parkadventure.service;

import com.ufrpe.parkadventure.model.atracao.Atracao;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;

/**
 * Cuidados em volta da carga de demonstração que já existe no projeto (DadosExemplo.carregar()):
 * confere se pode carregar (para não duplicar nem falhar) e, depois da carga, dá usuário e senha aos
 * visitantes de exemplo para que eles consigam entrar no sistema.
 */
public class ServicoDemonstracao {

    /** Senha dos visitantes de demonstração (o login de cada um é o nome sem acentos, ex.: "miguelferreira"). */
    public static final String SENHA_DEMONSTRACAO = "visitante123";

    private static final String[] VISITANTES_DE_EXEMPLO = {
            "Miguel Ferreira", "Pedro Neto", "Edivan José", "Ana Souza", "Bruno Lima", "Carla Dias", "Diego Alves"};
    private static final String[] ATRACOES_USADAS = {"Piratas do Caribe", "Lula Voadora", "Água Ardente"};

    private final ServicoAtracao servicoAtracao;
    private final ServicoVisitante servicoVisitante;
    private final ServicoAutenticacao servicoAutenticacao;

    public ServicoDemonstracao(ServicoAtracao servicoAtracao, ServicoVisitante servicoVisitante,
                               ServicoAutenticacao servicoAutenticacao) {
        this.servicoAtracao = servicoAtracao;
        this.servicoVisitante = servicoVisitante;
        this.servicoAutenticacao = servicoAutenticacao;
    }

    /**
     * Devolve null se a carga pode ser feita, ou a mensagem do motivo de não poder.
     */
    public String verificarPodeCarregar() {
        if (servicoVisitante.buscar(VISITANTES_DE_EXEMPLO[0]) != null) {
            return "Os dados de demonstração já foram carregados.";
        }
        for (String nome : ATRACOES_USADAS) {
            Atracao atracao = servicoAtracao.buscar(nome);
            if (atracao == null || atracao.getOperador() == null) {
                return "A demonstração usa as atrações \"Piratas do Caribe\", \"Lula Voadora\" e \"Água Ardente\" (com operador). "
                        + "Elas não existem (ou foram renomeadas) neste arquivo de dados.";
            }
        }
        return null;
    }

    /**
     * Chamado depois da carga: dá usuário e senha aos visitantes de exemplo e grava os dados.
     */
    public void liberarAcessoDosVisitantes() {
        for (String nome : VISITANTES_DE_EXEMPLO) {
            Visitante visitante = servicoVisitante.buscar(nome);
            if (visitante != null && !visitante.temCredenciais()) {
                if (visitante.getNomeCompleto() == null || visitante.getNomeCompleto().isBlank()) {
                    visitante.setNomeCompleto(nome);
                }
                visitante.setUsuario(servicoAutenticacao.gerarUsuario(nome, visitante));
                visitante.definirSenha(SENHA_DEMONSTRACAO);
            }
        }
        Persistencia.salvar();
    }
}
