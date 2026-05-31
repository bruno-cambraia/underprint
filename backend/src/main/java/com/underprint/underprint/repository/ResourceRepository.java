package com.underprint.underprint.repository;

import com.underprint.underprint.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    /* Ao estender JpaRepository, o Spring gera automaticamente todos os metodos de banco
    save(), findById(), findAll(), delete()
    <Resource, Long> diz "essa interface gerencia a entidade Resource, cuja chave primaria é Long" */

    Optional<Resource> findByName(String name);
    /* Esse metodo busca um recurso pelo nome. O Spring le o nome do metodo e gera o SQL automaticamente
    SELECT * FROM resource WHERE name = ?
    o Optional é um container que pode ou nao ter um valor, evita o NullPointerException quando nao existe
    */
}
