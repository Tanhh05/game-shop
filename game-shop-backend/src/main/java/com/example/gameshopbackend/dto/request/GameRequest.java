package com.example.gameshopbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequest {
    private String name;
    private String slug;
    private String thumbnail;
    private String description;
    private Boolean status = true;
}

