package pl.alyx.robot.sikulix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.structure.Configuration;
import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;

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

        Scenario scenario = this.state.configuration.scenario;

        String imagePath = ImagePath.getBundlePath();

        if (StringUtility.isNotEmpty(scenario.path)) {
            ImagePath.setBundlePath(Paths.get(scenario.path).toAbsolutePath().toString());
        }

        if (StringUtility.StringToBoolean(state.configuration.verbose)) {
            System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
        }

        for (Step step : scenario.steps) {
            try {
                new Automation(state, step).step();
            } catch (FindFailed eFindFailed) {
                eFindFailed.printStackTrace();
            }
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public App dump() {
        return this;
    }

    private boolean readConfiguration() throws IOException {
        String configurationFile;
        configurationFile = state.configurationFile;
        if (null == configurationFile || 0 == configurationFile.length()) {
            configurationFile = Global.CONFIGURATION_FILE;
        }
        configurationFile = Paths.get(configurationFile).toAbsolutePath().toString();
        String json = new String(Files.readAllBytes(Paths.get(configurationFile)), StandardCharsets.UTF_8);
        System.out.println(json);
        Gson gson = new GsonBuilder()
                .create();
        this.state.configuration = gson.fromJson(json, Configuration.class);
        return true;
    }
}
