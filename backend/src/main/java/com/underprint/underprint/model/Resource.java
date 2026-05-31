package com.underprint.underprint.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
// Diz pro JPA que essa classe representa uma tabela no banco
@Table(name = "resources")
// Define o nome da tabela, sem isso o JPA usaria o nome da classe
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Id marca a chave primaria da tabela
    // GeneratedValue gera automaticamente

    @Column(unique = true, nullable = false)
    private String name;
    // Define o nome do recurso
    // O nome do recurso não pode repetir nem ser nulo no banco


    private int maxDurability; // Durabilidade máxima
    private int durability; // Durabilidade atual
    private int resourceCount; // Quantidade já coletada
    private int respawnSeconds; // Quantos segundos fica indisponivel
    private Instant exhaustedUntil; // Momento exato em que o recurso volta a ficar disponivel

    public Resource() {}
    // Construtor vazio obrigatorio para o JPA, ele precisa criar objetos sem parametros internamente

    public Resource(String name, int maxDurability, int respawnSeconds) {
        // Este construtor recebe só 3 informações: as que variam de recurso para recurso
        this.name = name;
        // O name desse objeto aqui recebe o name que veio como parametro
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
        // Os dois recebem o mesmo valor inicial, durability vai diminuir e maxDurability vai comparar
        this.respawnSeconds = respawnSeconds;
        // Cada recurso pode ter um valor diferente
        this.resourceCount = 0;
        // Começa com zero
        this.exhaustedUntil = null;
        // No inicio o recurso esta disponivel, por isso NULL para não ter nenhum respawn marcado
    }

    public boolean isAvailable() {
        if (exhaustedUntil == null) return true;
        // Se exhaustedUntil é null, a pedra nunca foi esgotada
        if (Instant.now().isAfter(exhaustedUntil)) {
            // Se a pedra foi esgotada, verifica se o tempo já passou.
            // Instat.now() é o momento atual e .isAfter() pergunta "o agora é depois do tempo de respawn?
            durability = maxDurability;
            exhaustedUntil = null;
            // Se o tempo já passou, reseta a pedra
            return true;
        }
        return false;
    }

    public boolean mine() {
        if(!isAvailable()) return false;
        // Verifica se a pedra esta disponivel
        durability--;
        resourceCount++;
        // Um golpe aconteceu, durabilidade cai e recurso aumenta
        if (durability <= 0) {
            exhaustedUntil = Instant.now().plusSeconds(respawnSeconds);
            // Se a durabilidade zerou, marca o momento exato em que ela vai voltar:
            // agora + os segundos de respawn definidos para este recurso
        }
        return true;
    }

    public Long getId() { return  id; }
    public String getName() { return name; }
    public int getDurability() { return durability; }
    public int getResourceCount() { return resourceCount; }
    // Getters simples, janelas que deixam ver o valor de um campo privado sem poder modifica-los por fora

    public long getSecondsUntilRespawn() {
        // Calcula quantos segundos faltam para o respawn
        if (exhaustedUntil == null) return 0;
        long seconds = exhaustedUntil.getEpochSecond() - Instant.now().getEpochSecond();
        // .getEpochSecond() converte Instant em segundos subtraindo o atual do futuro, temos o tempo restante
        return Math.max(0, seconds);
        // Garante que nunca retorna negativo
    }

    public String getStatus() {
        return isAvailable() ? "DISPONIVEL" : "EMPTY";
        // ? Operador ternário
    }

}
