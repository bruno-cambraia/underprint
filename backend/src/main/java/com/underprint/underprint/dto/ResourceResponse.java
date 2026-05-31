package com.underprint.underprint.dto;

import com.underprint.underprint.model.Resource;

public class ResourceResponse {

    private String name;
    private int durability;
    private int resourceCount;
    private String status;
    private long secondsUntilRespawn;
    // Só os campos que vou expor no JSON

    public ResourceResponse(Resource resource) {
        // Construtor recebe um Resource já processado e extrai só o que interessa
        this.name = resource.getName();
        this.durability = resource.getDurability();
        this.resourceCount = resource.getResourceCount();
        this.status = resource.getStatus();
        this.secondsUntilRespawn = resource.getSecondsUntilRespawn();
        // Copia cada valor do Resource para o DTO
        // A partir daqui o Resource pode ter quantos métodos internos quiser
        // O JSON nunca vai expor o que não está aqui
    }


    public String getName() { return name; }
    public int getDurability() { return  durability; }
    public int getResourceCount() { return resourceCount; }
    public String getStatus() { return status; }
    public long getSecondsUntilRespawn() { return secondsUntilRespawn; }
}
