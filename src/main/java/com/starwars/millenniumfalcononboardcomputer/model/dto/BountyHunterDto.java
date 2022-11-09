package com.starwars.millenniumfalcononboardcomputer.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record BountyHunterDto(@NotBlank String planet, @Min(0) int day) {}
