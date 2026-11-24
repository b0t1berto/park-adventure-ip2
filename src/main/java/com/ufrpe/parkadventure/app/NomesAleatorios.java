package com.ufrpe.parkadventure.app;

import java.util.Random;

/**
 * Gera nomes e sobrenomes brasileiros aleatórios para operadores fictícios.
 */
public final class NomesAleatorios {

    private static final Random RANDOM = new Random();

    private static final String[] NOMES = {
            "Ana", "Bruno", "Carla", "Diego", "Elena", "Felipe", "Gabriela", "Hugo",
            "Isabela", "João", "Karen", "Lucas", "Mariana", "Nicolas", "Olívia", "Pedro",
            "Rafaela", "Samuel", "Tatiana", "Ulisses", "Vanessa", "Wagner", "Yasmin", "Zeca",
            "Amanda", "Bernardo", "Camila", "Daniel", "Fernanda", "Gustavo", "Helena", "Igor",
            "Juliana", "Kevin", "Larissa", "Matheus", "Natália", "Otávio", "Patricia", "Ricardo",
            "Sofia", "Thiago", "Valentina", "Victor", "Aline", "Caio", "Davi", "Eduarda",
            "Fabio", "Giovanna", "Henrique", "Ingrid", "Joana", "Kauê", "Letícia", "Marcelo",
            "Nanda", "Paulo", "Renata", "Stefany", "Tarcísio", "Ursula", "Vitor", "Wanessa"
    };

    private static final String[] SOBRENOMES = {
            "Silva", "Santos", "Oliveira", "Souza", "Rodrigues", "Ferreira", "Alves", "Pereira",
            "Lima", "Gomes", "Costa", "Ribeiro", "Martins", "Carvalho", "Almeida", "Lopes",
            "Soares", "Fernandes", "Vieira", "Barbosa", "Rocha", "Dias", "Nascimento", "Mendes",
            "Moreira", "Nunes", "Cavalcanti", "Machado", "Araújo", "Correia", "Freitas", "Moura",
            "Barros", "Teixeira", "Monteiro", "Cardoso", "Cunha", "Moraes", "Rezende", "Siqueira",
            "Borges", "Castro", "Farias", "Guimarães", "Hermes", "Klein", "Leal", "Macedo"
    };

    private NomesAleatorios() { }

    /**
     * Retorna um nome completo aleatório (nome + sobrenome).
     */
    public static String gerarNomeCompleto() {
        String nome = NOMES[RANDOM.nextInt(NOMES.length)];
        String sobrenome = SOBRENOMES[RANDOM.nextInt(SOBRENOMES.length)];
        return nome + " " + sobrenome;
    }

    /**
     * Verifica se um nome parece ser um nome genérico de operador, como "Operador Montanha Russa"
     * ou "Operador 2 Montanha Russa".
     */
    public static boolean ehNomeGenerico(String nome) {
        if (nome == null) return false;
        return nome.startsWith("Operador ");
    }
}
