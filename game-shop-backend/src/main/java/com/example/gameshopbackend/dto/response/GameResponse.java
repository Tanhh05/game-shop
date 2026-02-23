package com.example.gameshopbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class GameResponse {
    private Long id;
    private String name;
    private String slug;
    private String thumbnail;
    private String description;
    private Boolean status;
    private LocalDateTime createdAt;
}

