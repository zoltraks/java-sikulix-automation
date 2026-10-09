package pl.alyx.robot.sikulix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.cli.*;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.logic.Flow;
import pl.alyx.robot.sikulix.logic.Loader;
import pl.alyx.robot.sikulix.structure.Configuration;
import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {

    public Context context = new Context();

    public App parseArgs(String[] args) {

        Options options = new Options();
        options.addOption("v", "verbose", false, "give me more messsages");
        options.addOption("i", "ignore", false, "print step errors and continue the scenario");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmdLine = parser.parse(options, args);
            String[] cmdLineArgs = cmdLine.getArgs();
            if (cmdLineArgs.length > 0) {
                context.configurationFile = cmdLineArgs[0];
            }
            if (cmdLine.hasOption("verbose")) {
                context.settings.setVerbose(true);
            }
            if (cmdLine.hasOption("ignore")) {
                context.settings.setIgnore(true);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public App initialize() throws Exception {

        if (!readConfiguration()) {
            throw new Exception("Failed to read configuration");
        }

        if (context.configuration.verbose != null) {
            context.settings.setVerbose(StringUtility.stringToBoolean(context.configuration.verbose));
        }

        if (context.configuration.ignore != null) {
            context.settings.setIgnore(StringUtility.stringToBoolean(context.configuration.ignore));
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException | ClassNotFoundException ignored) {
        }

        return this;

    }

    public App start() throws URISyntaxException {

        this.context.screen = new Screen();

        Scenario[] scenarioArray = new Loader(context).collectScenarioArray();

        int index = 0;

        for (Scenario scenario : scenarioArray) {

            index++;

            if (context.settings.isVerbose()) {
                String name = StringUtility.isEmpty(scenario.name) ? "unnamed scenario"
                        : String.format("scenario %s", scenario.name);
                int count = scenarioArray.length;
                boolean single = 2 > count;
                if (single) {
                    System.out.printf("Playing %s%n", name);
                } else {
                    System.out.printf("Playing %s (%d of %d)%n", name, index, count);
                }
            }

            try (Flow flow = new Flow(this.context, scenario)) {
                while (flow.next()) ;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public App dump() {
        return this;
    }

    private boolean readConfiguration() throws IOException, JsonSyntaxException {
        String configurationFile;
        configurationFile = context.configurationFile;
        if (null == configurationFile || 0 == configurationFile.length()) {
            configurationFile = Global.CONFIGURATION_FILE;
        }
        configurationFile = Paths.get(configurationFile).toAbsolutePath().toString();
        String json;
        Path path = Paths.get(configurationFile);
        try {
            json = new String(Files.readAllBytes(Paths.get(configurationFile)), StandardCharsets.UTF_8);
        } catch (NoSuchFileException ingored) {
            System.out.printf("Error reading configuration file, no such file: %s%n", path.toAbsolutePath());
            return false;
        }
        Gson gson = new GsonBuilder()
                .create();
        this.context.configuration = gson.fromJson(json, Configuration.class);
        return true;
    }
}
