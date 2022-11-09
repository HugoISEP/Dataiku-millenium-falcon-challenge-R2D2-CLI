package com.starwars.millenniumfalcononboardcomputer.model.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class MillenniumFalcon {
    @Min(1)
    private int autonomy;
    @NotBlank
    private String departure;
    @NotBlank
    private String arrival;
    @NotBlank
    @JsonAlias("routes_db")
    private String routesDb;
}
