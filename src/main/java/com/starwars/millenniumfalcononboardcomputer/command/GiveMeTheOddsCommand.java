package com.starwars.millenniumfalcononboardcomputer.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starwars.millenniumfalcononboardcomputer.model.dto.EmpireInformationDto;
import com.starwars.millenniumfalcononboardcomputer.model.pojo.MillenniumFalcon;
import com.starwars.millenniumfalcononboardcomputer.service.MillenniumFalconService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
@CommandLine.Command(name = "give-me-the-odds", mixinStandardHelpOptions = true,
        version = "give-me-the-odds 1.0",
        description = "Prints the odds of the millennium falcon")
public class GiveMeTheOddsCommand implements Callable<Float> {

    private final MillenniumFalconService millenniumFalconService;

    @CommandLine.Parameters(index = "0", arity = "1",
            description = "Configuration file containing the properties of the spaceship")
    private File millenniumFalconFile;

    @CommandLine.Parameters(index = "1", arity = "1",
            description = "The file containing the information about the empire")
    private File empireFile;


    @Override
    public Float call() throws Exception {
        var empireData = new ObjectMapper().readValue(empireFile, EmpireInformationDto.class);
        var millenniumFalconData = new ObjectMapper().readValue(millenniumFalconFile, MillenniumFalcon.class);
        var result = millenniumFalconService.computeBestOddsToReachDestination(empireData, millenniumFalconData);
        System.out.println(result);
        return result;
    }
}
