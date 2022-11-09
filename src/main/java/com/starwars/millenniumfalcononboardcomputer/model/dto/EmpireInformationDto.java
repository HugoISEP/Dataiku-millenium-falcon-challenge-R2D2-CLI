package com.starwars.millenniumfalcononboardcomputer.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

public record EmpireInformationDto(@Min(0) int countdown, @Valid @JsonAlias("bounty_hunters") List<BountyHunterDto> bountyHunters) {
}
