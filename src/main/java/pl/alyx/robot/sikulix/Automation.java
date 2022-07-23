package pl.alyx.robot.sikulix;

import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.structure.Step;

import javax.swing.*;

import static pl.alyx.robot.sikulix.utility.StringUtility.StringToDouble;

public class Automation {

    private final State state;

    private final Step step;

    public Automation(State state, Step step) {
        this.state = state;
        this.step = step;
    }

    public void step() throws FindFailed {

        Screen screen = this.state.screen;

        double wait = StringToDouble(this.step.wait);
        if (wait > 0.0) {
            screen.wait(wait);
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
            screen.click(click);
        }

        String type = this.step.type;
        if (null != type && 0 < type.length()) {
            screen.type(type);
        }

        String message = this.step.message;
        if (null != message && 0 < message.length()) {
            JOptionPane.showMessageDialog(null, message);
        }

    }
}
