package com.ufrpe.parkadventure.javafx_parkadventure.model.atracao;

import com.ufrpe.parkadventure.javafx_parkadventure.model.pessoa.Visitante;

public interface IRestricao {
    // Retorna NENHUM quando o visitante pode acessar; caso contrário, o motivo do bloqueio
    MotivoBloqueio verificarRestricao(Visitante visitante);
}
