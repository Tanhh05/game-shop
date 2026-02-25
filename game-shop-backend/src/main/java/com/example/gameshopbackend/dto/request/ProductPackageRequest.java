package com.example.gameshopbackend.dto.request;

import com.example.gameshopbackend.util.DurationUnit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPackageRequest {

    private String name;
    private Long price;
    private Integer durationValue;
    private DurationUnit durationUnit;
}