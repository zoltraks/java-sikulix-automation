package pl.alyx.robot.sikulix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.structure.Configuration;
import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.utility.FileUtility;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class App {

    public State state = new State();

    public App parse(String[] args) {
        if (args.length > 0) {
            state.configurationFile = args[0];
        }
        return this;
    }

    public App initialize() throws Exception {
        if (!readConfiguration()) {
            throw new Exception((String) null);
        }

        if (StringUtility.StringToBoolean(state.configuration.verbose)) {
            System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException | ClassNotFoundException ignored) {
        }

        return this;
    }

    public App start() {

        this.state.screen = new Screen();

        boolean verbose = StringUtility.StringToBoolean(state.configuration.verbose);

        Scenario[] scenarioArray = collectScenarioArray();

        for (Scenario scenario : scenarioArray) {

            if (StringUtility.isNotEmpty(scenario.path)) {
                ImagePath.setBundlePath(Paths.get(scenario.path).toAbsolutePath().toString());
                if (verbose) {
                    System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
                }
            }

            Flow flow = new Flow(this.state, scenario);

            //noinspection StatementWithEmptyBody
            while (flow.next());

        }

        return this;
    }

    private Scenario[] collectScenarioArray() {
        List<Scenario> list = new ArrayList<>();
        Configuration configuration = this.state.configuration;
        boolean verbose = StringUtility.StringToBoolean(configuration.verbose);
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

        String[] alternatives = { ".json", ".script" };

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

        return null;
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
        String json = new String(Files.readAllBytes(Paths.get(configurationFile)), StandardCharsets.UTF_8);
        Gson gson = new GsonBuilder()
                .create();
        this.state.configuration = gson.fromJson(json, Configuration.class);
        return true;
    }
}
