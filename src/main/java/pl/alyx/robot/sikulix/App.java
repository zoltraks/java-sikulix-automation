package pl.alyx.robot.sikulix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.cli.*;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.logic.Flow;
import pl.alyx.robot.sikulix.structure.Configuration;
import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.FileUtility;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public State state = new State();

    public App parseArgs(String[] args) {

        Options options = new Options();
        options.addOption("v", "verbose", false, "give me more messsages");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmdLine = parser.parse(options, args);
            String[] cmdLineArgs = cmdLine.getArgs();
            if (cmdLineArgs.length > 0) {
                state.configurationFile = cmdLineArgs[0];
            }
            if (cmdLine.hasOption("verbose")) {
                state.settings.setVerbose(true);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

//        if (args.length > 0) {
//            state.configurationFile = args[0];
//        }

        return this;
    }

    public App initialize() throws Exception {

        if (!readConfiguration()) {
            throw new Exception((String) null);
        }

        if (state.configuration.verbose != null) {
            state.settings.setVerbose(StringUtility.stringToBoolean(state.configuration.verbose));
        }

//        if (state.settings.isVerbose()) {
//            System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
//        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException | ClassNotFoundException ignored) {
        }

        return this;

    }

    public App start() throws URISyntaxException {

        this.state.screen = new Screen();

        Scenario[] scenarioArray = collectScenarioArray();

        int index = 0;

        for (Scenario scenario : scenarioArray) {

            index++;

            if (state.settings.isVerbose()) {
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

            try (Flow flow = new Flow(this.state, scenario)) {
                while (flow.next()) ;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        return this;
    }

    private Scenario[] collectScenarioArray() {
        List<Scenario> list = new ArrayList<>();
        Configuration configuration = this.state.configuration;
        boolean verbose = StringUtility.stringToBoolean(configuration.verbose);
        if (configuration.scenario != null) {
            list.add(configuration.scenario);
        }
        if (configuration.play != null) {
            Scenario s = readScenarioFromFile(configuration.play);
            if (null != s) {
                list.add(s);
            } else if (verbose) {
                System.out.printf("Can't read scenario from %s%n", configuration.play);
            }
        }
        if (configuration.playlist != null) {
            for (int i = 0; i < configuration.playlist.size(); i++) {
                String c = configuration.playlist.get(i);
                Scenario s = readScenarioFromFile(c);
                if (null != s) {
                    if (StringUtility.isEmpty(s.name)) {
                        s.name = c;
                    }
                    list.add(s);
                } else if (verbose) {
                    System.out.printf("Can't read scenario from %s%n", c);
                }
            }
        }
//        Scenario[] array = new Scenario[list.size()];
//        int i = 0;
//        for (Scenario scenario : list) {
//            array[i++] = scenario;
//        }
        Scenario[] array;
        array = list.toArray(new Scenario[0]);
        return array;
    }

    private Scenario readScenarioFromFile(String file) {
        if (null == file) {
            return null;
        }

        String[] alternatives = {".json", ".script"};

        boolean fileExists = FileUtility.fileExists(file);

        if (!fileExists) {
            for (String alternative : alternatives) {
                String newName = file + alternative;
                if (FileUtility.fileExists(newName)) {
                    file = newName;
                    fileExists = true;
                    break;
                }
            }
            if (!fileExists) {
                return null;
            }
        }

        String content;

        try {
            content = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        content = content.trim();

        if (content.startsWith("{")) {
            Gson gson = new GsonBuilder()
                    .create();
            Configuration configuration = gson.fromJson(content, Configuration.class);

            return configuration.scenario;
        }

        Scenario scriptScenario;
        scriptScenario = parseScenarioScript(content);
        return scriptScenario;
    }

    private Scenario parseScenarioScript(String content) {
        String pattern = "\\s*\n" +
                "(?:\n" +
                "\\#[^\\r\\n]*\n" +
                "|\n" +
                "([\\p{L}@$%][\\p{L}0-9_\\.\\-@$%!]*)\n" +
                "(?:[\\ \\t]*((?:(?:\"(?:\"\"|[^\"])*\"|[^\\s]+)[\\ \\t]*)*))?\n" +
                ")";
        boolean found = false;
        int flags = Pattern.COMMENTS;
        Pattern r = Pattern.compile(pattern, flags);
        Matcher m = r.matcher(content);
        List<Step> steps = new ArrayList<>();
        while (m.find()) {
            String key = m.group(1);
            String value = m.group(2);
            if (StringUtility.isEmpty(key)) {
                continue;
            }
            Step step = Step.create(key, value);
            if (null == step && state.settings.isVerbose()) {
                System.out.printf("Ignoring unknown script operation %s %s%n", key, value);
            }
            if (null != step) {
                steps.add(step);
            }
        }
        Scenario scenario = null;
        if (0 < steps.size()) {
            scenario = new Scenario();
            scenario.steps = steps;
        }
        return scenario;
    }

    @SuppressWarnings("UnusedReturnValue")
    public App dump() {
        return this;
    }

    private boolean readConfiguration() throws IOException, JsonSyntaxException {
        String configurationFile;
        configurationFile = state.configurationFile;
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
        this.state.configuration = gson.fromJson(json, Configuration.class);
        return true;
    }
}
