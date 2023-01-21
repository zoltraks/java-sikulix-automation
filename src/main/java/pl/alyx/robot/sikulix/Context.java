package pl.alyx.robot.sikulix;

import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.structure.Configuration;
import pl.alyx.robot.sikulix.structure.Settings;

public class Context {

    public String configurationFile;

    public Configuration configuration;

    public Screen screen;

    public Settings settings = new Settings();

    public State state;

}
