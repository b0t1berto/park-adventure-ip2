package parkAdventure.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import parkAdventure.model.pessoa.Operador;

// REQ15: operadores escalados por data (feriados)
public class EscalaOperadores {
    private final Map<LocalDate, List<Operador>> escala = new TreeMap<>();

    // false se o operador for nulo ou já estiver escalado na data
    public boolean escalar(LocalDate data, Operador operador) {
        if (data == null || operador == null) return false;
        List<Operador> lista = escala.computeIfAbsent(data, d -> new ArrayList<>());
        for (Operador o : lista) {
            if (o.getNome().equalsIgnoreCase(operador.getNome())) return false;
        }
        return lista.add(operador);
    }

    public List<Operador> operadoresDe(LocalDate data) {
        return new ArrayList<>(escala.getOrDefault(data, List.of()));
    }

    public String descrever(LocalDate data) {
        List<Operador> lista = operadoresDe(data);
        if (lista.isEmpty()) return "Sem operador escalado";
        StringBuilder nomes = new StringBuilder();
        for (Operador o : lista) {
            nomes.append(nomes.length() == 0 ? "" : ", ").append(o.getNome());
        }
        return nomes.toString();
    }
}
