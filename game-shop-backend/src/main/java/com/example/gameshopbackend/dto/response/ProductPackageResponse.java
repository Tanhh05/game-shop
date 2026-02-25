package com.example.gameshopbackend.dto.response;

import com.example.gameshopbackend.util.DurationUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPackageResponse {

    private Long id;
    private String name;
    private Long price;
    private Integer durationValue;
    private DurationUnit durationUnit;
}
