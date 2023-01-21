package pl.alyx.robot.sikulix.logic;

import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import pl.alyx.robot.sikulix.Result;
import pl.alyx.robot.sikulix.Context;
import pl.alyx.robot.sikulix.State;
import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.StringUtility;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Flow implements AutoCloseable {

    public Flow(Context context, Scenario scenario) {
        this.context = context;
        this.scenario = scenario;
    }

    private final Scenario scenario;

    private final Context context;

    private int index;

    private boolean virgin = true;

    public boolean next() {
        if (virgin) {
            virgin = false;
            reset();
        }
        if (index >= this.scenario.steps.size()) {
            return false;
        }
        Step step = this.scenario.steps.get(index);
        index++;
        Result result = new Automation(context, step).step();
        if (!result.success) {
            if (StringUtility.isNotEmpty(result.error)) {
                System.out.println(result.error);
                return false;
            }
        }
        if (StringUtility.isNotEmpty(result.jump)) {
            int labelIndex = findLabel(result.jump);
            if (0 <= labelIndex) {
                index = labelIndex;
            } else {
                System.out.printf("Can't jump to %s%n", StringUtility.QuoteVolatile(result.jump));
            }
        }
        return result.success;
    }

    private int findLabel(String label) {

        for (int i = 0; i < this.scenario.steps.size(); i++) {
            Step step = this.scenario.steps.get(i);
            if (StringUtility.isNotEmpty(step.label) && step.label.equalsIgnoreCase(label)) {
                return i;
            }
        }

        return -1;

    }

    @Override
    public void close() throws Exception {
        reset();
    }

    private void reset() {
        context.state = new State();
        context.state.wait = 3.0;
        context.state.delay = StringUtility.stringToDouble(context.configuration.delay);

        Settings.MinSimilarity = 0.7;

        Path path = null;

        if (StringUtility.isNotEmpty(context.configuration.path)) {
            path = Paths.get(context.configuration.path);
        }

        if (StringUtility.isNotEmpty(scenario.path)) {
            if (null == path || Paths.get(scenario.path).isAbsolute()) {
                path = Paths.get(scenario.path);
            } else {
                path = Paths.get(path.toString(), scenario.path);
            }
        }

        if (null != path) {
            String oldPath = ImagePath.getBundlePath();
            String newPath = path.toAbsolutePath().toString();
            if (!newPath.equals(oldPath)) {
                ImagePath.setBundlePath(path.toAbsolutePath().toString());
                if (context.settings.isVerbose()) {
                    System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
                }
            }
        }

        //ImagePath.setBundlePath("pl.alyx.robot.sikulix.Main/asset");
        //ImagePath.add("pl.alyx.robot.sikulix.Main/asset");
        //ImagePath.add("pl.alyx.robot.sikulix/assets");
        //ImagePath.addJar("pl.alyx.robot.sikulix");

        URL dir_url = ClassLoader.getSystemResource("assets");
        File dir = null;
        try {
            dir = new File(dir_url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
//        String[] files = dir.list();
//        if (state.settings.isVerbose()) {
//            System.out.println("" +
//                    String.format("Assets: ") +
//                    String.join(String.format(" , "), files) +
//                    ""
//            );
//        }
        ImagePath.add(dir_url);
    }

}
