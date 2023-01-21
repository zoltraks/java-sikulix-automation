package pl.alyx.robot.sikulix.logic;

import org.sikuli.basics.Settings;
import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.Result;
import pl.alyx.robot.sikulix.Context;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;
import java.nio.file.Paths;

import static pl.alyx.robot.sikulix.utility.StringUtility.stringToDouble;

public class Automation {

    private final Context context;

    private final Step step;

    public Automation(Context context, Step step) {
        this.context = context;
        this.step = step;
    }

    public Result step() {

        Result result = new Result();

        result.success = true;

        Screen screen = this.context.screen;

        boolean verbose = context.settings.isVerbose();

        String path = this.step.path;
        if (StringUtility.isNotWhite(path)) {
            ImagePath.setBundlePath(Paths.get(path).toAbsolutePath().toString());
            if (verbose) {
                System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
            }
        }

        String delay = this.step.delay;
        if (StringUtility.isNotWhite(delay)) {
            context.state.delay = stringToDouble(this.step.delay);
        }

        String wait = this.step.wait;
        if (StringUtility.isNotWhite(wait)) {
            double value = stringToDouble(wait);
            if (value > 0.0) {
                context.state.wait = value;
            } else {
                context.state.wait = 3.0;
            }
        }

        String similarity = this.step.similarity;
        if (null != similarity && 0 < similarity.length()) {
            double value = StringUtility.stringToDouble(similarity);
            if (value > 0) {
                Settings.MinSimilarity = value;
                if (verbose) {
                    System.out.printf("Similarity set to %.1f%n", value);
                }
            }
        }

        String print = this.step.print;
        if (StringUtility.isNotEmpty(print)) {
            System.out.println(print);
        }

        String mouse = this.step.mouse;
        if (StringUtility.isNotWhite(mouse)) {
            if (mouse.equalsIgnoreCase("RIGHT")) {
                screen.mouseDown(Button.RIGHT);
            }
            if (mouse.equalsIgnoreCase("LEFT")) {
                screen.mouseDown(Button.LEFT);
            }
            if (mouse.equalsIgnoreCase("MIDDLE")) {
                screen.mouseDown(Button.MIDDLE);
            }
            if (mouse.equalsIgnoreCase("UP")) {
                screen.mouseUp();
            }
        }

        String find = this.step.find;
        if (StringUtility.isNotWhite(find)) {
            try {
                screen.find(find);
            } catch (FindFailed e) {
                result.error = String.format("Find failed for %s", e.getMessage());
                result.success = false;
                return result;
            }
        }

        String click = this.step.click;
        if (StringUtility.isNotWhite(click)) {
            try {
                screen.click(click);
            } catch (FindFailed e) {
                result.error = String.format("Click failed for %s", e.getMessage());
                result.success = false;
                return result;
            }
        }

        String popup = this.step.popup;
        if (StringUtility.isNotEmpty(popup)) {
            try {
                screen.rightClick(popup);
            } catch (FindFailed e) {
                result.error = String.format("Right click failed for %s", e.getMessage());
                result.success = false;
                return result;
            }
        }

        String type = this.step.type;
        if (StringUtility.isNotEmpty(type)) {
            screen.type(type);
        }

        String message = this.step.message;
        if (StringUtility.isNotWhite(message)) {
            JOptionPane.showMessageDialog(null, message);
        }

        String when = this.step.when;
        if (StringUtility.isNotEmpty(when)) {
            context.state.result = screen.has(when, context.state.wait);
        }

        String then = this.step.then;
        if (StringUtility.isNotWhite(then)) {
            if (context.state.result) {
                result.jump = then;
            }
        }

        String jump = this.step.jump;
        if (StringUtility.isNotWhite(jump)) {
            if (null == jump) {
                result.jump = jump;
            }
        }

        String pause = this.step.pause;
        if (pause != null) {
            double value = stringToDouble(pause);
            if (value < 0.1) {
                value = context.state.delay;
            }
            if (value > 0.0) {
                screen.wait(value);
            }
        }

        if (context.state.delay > 0.0) {
            screen.wait(context.state.delay);
        }

        return result;

    }
}
