package com.ufrpe.parkadventure;

import com.ufrpe.parkadventure.controller.AutenticacaoController;
import com.ufrpe.parkadventure.controller.AutenticacaoController.Resultado;
import com.ufrpe.parkadventure.controller.RepositorioVisitanteController;
import com.ufrpe.parkadventure.model.pessoa.Visitante;
import com.ufrpe.parkadventure.repository.Persistencia;
import com.ufrpe.parkadventure.repository.RepositorioVisitante;

public class TestLoginCompleto {
    public static void main(String[] args) {
        // Simula o carregamento do arquivo de dados
        Persistencia.definirArquivo(java.nio.file.Paths.get("dados/dados.txt"));
        Persistencia.carregar();
        
        System.out.println("=== Visitantes carregados ===");
        for (Visitante v : RepositorioVisitanteController.listarTodos()) {
            System.out.println("Nome: " + v.getNome() + 
                ", Usuario: " + v.getUsuario() + 
                ", SenhaHash: " + (v.getSenhaHash() == null ? "null" : v.getSenhaHash().substring(0, Math.min(20, v.getSenhaHash().length())) + "...") +
                ", TemCredenciais: " + v.temCredenciais());
        }
        
        System.out.println("\n=== Busca por usuario 'b0t1berto' ===");
        Visitante encontrado = RepositorioVisitanteController.buscarPorUsuario("b0t1berto");
        System.out.println("Encontrado: " + (encontrado != null ? encontrado.getNome() : "null"));
        
        System.out.println("\n=== Tentativa de login ===");
        Resultado resultado = AutenticacaoController.entrar("b0t1berto", "Taylorseries");
        System.out.println("Resultado: " + (resultado != null ? "SUCESSO - Papel: " + resultado.papel() : "FALHA - null"));
        
        // Testa com diferentes variações
        System.out.println("\n=== Testes com variações ===");
        String[][] testes = {
            {"b0t1berto", "Taylorseries"},
            {" b0t1berto ", "Taylorseries"},
            {"b0t1berto", " Taylorseries"},
            {"B0T1BERTO", "Taylorseries"},
        };
        for (String[] t : testes) {
            Resultado r = AutenticacaoController.entrar(t[0], t[1]);
            System.out.println("Usuario: '" + t[0] + "', Senha: '" + t[1] + "' -> " + (r != null ? "SUCESSO" : "FALHA"));
        }
    }
}
