package pl.alyx.robot.sikulix;

import org.python.antlr.ast.Str;
import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;

import java.nio.file.Paths;

import static pl.alyx.robot.sikulix.utility.StringUtility.StringToDouble;

public class Automation {

    private final State state;

    private final Step step;

    public Automation(State state, Step step) {
        this.state = state;
        this.step = step;
    }

    public Result step() {

        Result result = new Result();
        result.success = true;

        Screen screen = this.state.screen;

        boolean verbose = StringUtility.StringToBoolean(this.state.configuration.verbose);

        double wait = StringToDouble(this.step.wait);
        if (wait > 0.0) {
            screen.wait(wait);
        }

        String path = this.step.path;

        if (StringUtility.isNotEmpty(path)) {
            ImagePath.setBundlePath(Paths.get(path).toAbsolutePath().toString());
            if (verbose) {
                System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
            }
        }

        String mouse = this.step.mouse;
        if (null != mouse && 0 < mouse.length()) {
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

        String click = this.step.click;
        if (null != click && 0 < click.length()) {
            try {
                screen.click(click);
            } catch (FindFailed e) {
                result.error = e.getMessage();
                result.success = false;
                return result;
            }
        }

        String type = this.step.type;
        if (null != type && 0 < type.length()) {
            screen.type(type);
        }

        String message = this.step.message;
        if (null != message && 0 < message.length()) {
            JOptionPane.showMessageDialog(null, message);
        }

        String jump = this.step.jump;
        if (StringUtility.isNotEmpty(jump)) {
            result.jump = jump;
        }

        return result;

    }
}
