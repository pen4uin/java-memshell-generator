package jmg.all;

import jmg.core.config.Constants;

public class jMGApp {
    public static void main(String[] args) throws Throwable {
        if (args.length < 1) {
            System.out.println("jmg usage:");
            System.out.printf("1. java -jar jmg-all-%s.jar cli%n", Constants.JMG_VERSION);
            System.out.printf("2. java -jar jmg-all-%s.jar gui%n", Constants.JMG_VERSION);
            return;
        }

        switch (args[0]) {
            case "gui":
                jmg.gui.GUIApp.main(args);
                break;
            case "cli":
                jmg.cli.CLIApp.main(args);
                break;
            default:
                System.out.println("Invalid command. Please use either 'cli' or 'gui'.");
                break;
        }
    }
}