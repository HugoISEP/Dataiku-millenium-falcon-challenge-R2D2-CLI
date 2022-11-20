package com.starwars.millenniumfalcononboardcomputer.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starwars.millenniumfalcononboardcomputer.model.dto.EmpireInformationDto;
import com.starwars.millenniumfalcononboardcomputer.model.pojo.MillenniumFalcon;
import com.starwars.millenniumfalcononboardcomputer.service.MillenniumFalconService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
@CommandLine.Command(
        name = "give-me-the-odds",
        exitCodeOnInvalidInput = 74,
        mixinStandardHelpOptions = true,
        version = "give-me-the-odds 1.0",
        description = "Prints the odds of the millennium falcon")
public class GiveMeTheOddsCommand implements Callable<Integer> {

    private final DriverManagerDataSource dataSource;

    private final MillenniumFalconService millenniumFalconService;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Parameters(index = "0", arity = "1",
            description = "Configuration file containing the properties of the spaceship")
    private File millenniumFalconFile;

    @CommandLine.Parameters(index = "1", arity = "1",
            description = "The file containing the information about the empire")
    private File empireFile;


    @Override
    public Integer call() throws Exception {
        var empireData = loadDataFromFile(empireFile, EmpireInformationDto.class);
        var millenniumFalconData = loadDataFromFile(millenniumFalconFile, MillenniumFalcon.class);
        loadDbFile(millenniumFalconData);
        var result = millenniumFalconService.computeBestOddsToReachDestination(empireData, millenniumFalconData);
        spec.commandLine().getOut().println(result);
        return 0;
    }

    private <T> T loadDataFromFile(File file, Class<T> clazz) throws IOException {
        if (file.exists()) {
            return new ObjectMapper().readValue(file, clazz);
        } else {
            throw new FileNotFoundException("File not found: " + file.getName());
        }
    }

    public void loadDbFile(MillenniumFalcon millenniumFalconInformation) throws FileNotFoundException {
        val millenniumFilePath = millenniumFalconFile.getAbsoluteFile().getParent();
        val jdbcUrlPrefix = "jdbc:sqlite:";
        var routeDbPath = millenniumFalconInformation.getRoutesDb();
        if (new File(routeDbPath).exists()) {
            dataSource.setUrl(String.format("%s%s", jdbcUrlPrefix, routeDbPath));
        } else if (new File(millenniumFilePath + routeDbPath).exists()) {
            dataSource.setUrl(String.format("%s%s/%s", jdbcUrlPrefix, millenniumFilePath, routeDbPath));
        } else {
            throw new FileNotFoundException("db file not found");
        }
    }
}
