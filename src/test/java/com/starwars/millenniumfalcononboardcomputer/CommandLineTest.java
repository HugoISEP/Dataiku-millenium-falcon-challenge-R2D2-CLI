package com.starwars.millenniumfalcononboardcomputer;

import com.starwars.millenniumfalcononboardcomputer.command.GiveMeTheOddsCommand;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import picocli.CommandLine;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class CommandLineTest {

    @Autowired
    CommandLine.IFactory factory;

    @Autowired
    GiveMeTheOddsCommand giveMeTheOddsCommand;

    @Test
    void commandLineExecutionSuccessTest() throws Exception {
        val millenniumFalconFilePath = "src/test/resources/millennium-falcon.json";
        val empireFilePath = "src/test/resources/empire.json";
        int exitCode = catchSystemExit(() -> {
            MillenniumFalconOnboardComputerApplication.main(new String[]{millenniumFalconFilePath, empireFilePath});
        });
        assertEquals(0, exitCode);
    }

    @Test
    void commandLineExecutionFailureTest() throws Exception {
        val millenniumFalconFilePath = "src/test/resources/millennium-falcon.json";
        val empireFilePath = "BadFilePath";
        int exitCode = catchSystemExit(() -> {
            MillenniumFalconOnboardComputerApplication.main(new String[]{millenniumFalconFilePath, empireFilePath});
        });
        assertEquals(1, exitCode);
    }

    @Test
    void helpCommandTest() {
        String expectedHelpMessage = "Usage: give-me-the-odds [-hV] <millenniumFalconFile> <empireFile>\n" +
                "Prints the odds of the millennium falcon\n" +
                "      <millenniumFalconFile>\n" +
                "                     Configuration file containing the properties of the\n" +
                "                       spaceship\n" +
                "      <empireFile>   The file containing the information about the empire\n" +
                "  -h, --help         Show this help message and exit.\n" +
                "  -V, --version      Print version information and exit.\n";
        String actualHelpMessage = new CommandLine(giveMeTheOddsCommand, factory).getUsageMessage(CommandLine.Help.Ansi.OFF);
        assertEquals(expectedHelpMessage, actualHelpMessage);
    }

    @Test
    void commandLineArgsTest() {
        String arg1 = "millennium-falcon.json";
        String arg2 = "empire.json";
        CommandLine.ParseResult parseResult = new CommandLine(giveMeTheOddsCommand, factory)
                .parseArgs(arg1,  arg2);
        assertEquals(arg1, parseResult.originalArgs().get(0));
        assertEquals(arg2, parseResult.originalArgs().get(1));
    }

}
