package com.underprint.underprint.controller;

import com.underprint.underprint.model.Resource;
import com.underprint.underprint.dto.ResourceResponse;
import com.underprint.underprint.service.ResourceService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
// Diz para o Spring que essa classe recebe requisições HTTP e devolve dados JSON
@RequestMapping("/resource")
// Todas as rotas dessa classe começam com /resource
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    // Injeção de dependencia por construtor. O spring entrega o ResourceService automaticamente.
    // O Controller não sabe como o Service funciona por dentro
    // Só sabe que pode chamar os métodos dele

    @GetMapping("/{name}")
    public ResourceResponse getResource(@PathVariable String name) {
        Resource resource = resourceService.getResource(name);
        return new ResourceResponse(resource);
    }
    // {name} é uma variavel na URL e @PathVariable captura esse valor e passa pro metodo

    @PostMapping("/{name}/mine")
    public ResourceResponse mine(@PathVariable String name) {
        Resource resource = resourceService.mine(name);
        return new ResourceResponse(resource);
    }
    // Mesmo conceito, mas para minerar
}
