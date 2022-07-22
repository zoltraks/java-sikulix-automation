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
import pl.alyx.robot.sikulix.logic.Automation;
import pl.alyx.robot.sikulix.structure.configuration.Configuration;
import pl.alyx.robot.sikulix.structure.scenario.Scenario;
import pl.alyx.robot.sikulix.structure.scenario.Step;
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
            System.out.println(String.format("ImagePath: %s", ImagePath.getBundlePath()));
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
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
            System.out.println(String.format("ImagePath: %s", ImagePath.getBundlePath()));
        }

        for (Step step : scenario.steps) {
            try {
                new Automation(state, step).take();
            } catch (FindFailed eFindFailed) {
                eFindFailed.printStackTrace();
            }
        }

        return this;
    }

    public App dump() {
        return this;
    }

    private boolean readConfiguration() throws IOException {
        String configurationFile = null;
        configurationFile = state.configurationFile;
        if (null == configurationFile || 0 == configurationFile.length()) {
            configurationFile = Global.CONFIGURATION_FILE;
        }
        if (null != configurationFile && 0 < configurationFile.length()) {
            configurationFile = Paths.get(configurationFile).toAbsolutePath().toString();
            String json = new String(Files.readAllBytes(Paths.get(configurationFile)), StandardCharsets.UTF_8);
            System.out.println(json);
            Gson gson = new GsonBuilder()
                    .create();
            this.state.configuration = (Configuration)gson.fromJson(json, Configuration.class);
        }
        return true;
    }
}
