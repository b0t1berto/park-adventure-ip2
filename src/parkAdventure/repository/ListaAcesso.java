package parkAdventure.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import parkAdventure.model.acesso.RegistroAcesso;
import parkAdventure.model.atracao.Atracao;
import parkAdventure.model.pessoa.Visitante;

// Histórico de acessos às atrações
public class ListaAcesso {
    private final ArrayList<RegistroAcesso> acessos = new ArrayList<>();

    public boolean registrar(RegistroAcesso registro) {
        return registro != null && acessos.add(registro);
    }

    public List<RegistroAcesso> listarTodos() {
        return new ArrayList<>(acessos);
    }

    // Acesso em andamento (sem saída) do visitante naquela atração
    public RegistroAcesso buscarAtivo(Visitante visitante, Atracao atracao) {
        for (RegistroAcesso r : acessos) {
            if (r.estaAtivo() && r.getVisitante() == visitante && r.getAtracao() == atracao) {
                return r;
            }
        }
        return null;
    }

    // Acessos cuja entrada está entre as duas datas (inclusive)
    public List<RegistroAcesso> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<RegistroAcesso> resultado = new ArrayList<>();
        for (RegistroAcesso r : acessos) {
            LocalDate data = r.getEntrada().toLocalDate();
            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                resultado.add(r);
            }
        }
        return resultado;
    }
}
