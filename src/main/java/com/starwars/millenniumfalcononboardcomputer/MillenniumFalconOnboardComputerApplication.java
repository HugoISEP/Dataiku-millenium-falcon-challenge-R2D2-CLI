package com.starwars.millenniumfalcononboardcomputer;

import com.starwars.millenniumfalcononboardcomputer.command.GiveMeTheOddsCommand;
import com.starwars.millenniumfalcononboardcomputer.configuration.SQLiteDialect;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.TypeHint;
import picocli.CommandLine;

@TypeHint(types = {CommandLine.Mixin.class, SQLiteDialect.class})
@SpringBootApplication
public class MillenniumFalconOnboardComputerApplication implements CommandLineRunner, ExitCodeGenerator {
    private final CommandLine.IFactory factory;

    private final GiveMeTheOddsCommand command;

    private int exitCode;

    MillenniumFalconOnboardComputerApplication(CommandLine.IFactory factory, GiveMeTheOddsCommand command) {
        this.factory = factory;
        this.command = command;
    }

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(command, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(MillenniumFalconOnboardComputerApplication.class, args)));
    }
}
