package park_adventure.model.atracao;

import park_adventure.model.pessoa.Visitante;

public interface IRestricao {
    // Retorna NENHUM quando o visitante pode acessar; caso contrário, o motivo do bloqueio
    MotivoBloqueio verificarRestricao(Visitante visitante);
}
