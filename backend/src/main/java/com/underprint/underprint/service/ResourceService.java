package com.underprint.underprint.service;

import com.underprint.underprint.model.Resource;
import com.underprint.underprint.repository.ResourceRepository;
import org.springframework.stereotype.Service;

@Service
// Diz para o Spring que essa classe é um serviço e injeta onde precisar
public class ResourceService {

    private final ResourceRepository resourceRepository;
    // Service recebe o Repository por injeção, é ele quem fala com o banco

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
        initializeResources();
    }

    private void initializeResources() {
        createIfNotExists("rock", 10, 60);
        createIfNotExists("mushroom", 5, 120);
        createIfNotExists("weed", 20, 300);
    }
    // Chamado uma vez quando o Spring inicia, cadastra os recursos no banco se ainda nao existirem

    private void createIfNotExists(String name, int maxDurability, int respawnSeconds) {
        if (resourceRepository.findByName(name).isEmpty()){
            resourceRepository.save(new Resource(name, maxDurability, respawnSeconds));
        }
    }
    // .isEmpty() verifica se o Optional veio vazio, se o recurso ainda não existe no banco,
    // só cria se não existir, evitando duplicatas

    public Resource getResource(String name) {
        // Busca um recurso pelo nome e valida
        return resourceRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Recurso não encontrado:" + name));
    }
    // .orElseThrow() lança um erro automaticamente se o Optional vier vazio


    public Resource mine(String name) {
        Resource resource = getResource(name);
        resource.mine();
        return resourceRepository.save(resource);
    }
    // Busca o recurso pelo nome, chama o metodo mine() e retorna o recurso atualizado
    // .save() faz um update se o recurso já existe

}
