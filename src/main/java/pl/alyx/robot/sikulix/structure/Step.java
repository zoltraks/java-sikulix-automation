package pl.alyx.robot.sikulix.structure;

import pl.alyx.robot.sikulix.utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class Step {

    public String label;

    public String path;

    public String delay;

    public String wait;

    public String similarity;

    public String mouse;

    public String find;

    public String click;

    public String popup;

    public String type;

    public String message;

    public String execute;

    public String when;

    public String then;

    public String jump;

    public String print;

    public String pause;

    public static Step create(String key, String value) {
        if (null == key) {
            return null;
        }
        key = key.trim();
        if (0 == key.length()) {
            return null;
        }
        key = key.toUpperCase();
        Step step = new Step();
        switch (key) {
            case "LABEL":
                step.label = value;
                break;
            case "PATH":
                step.path = value;
                break;
            case "DELAY":
                step.delay = value;
                break;
            case "WAIT":
                step.wait = value;
                break;
            case "SIMILARITY":
                step.similarity = value;
                break;
            case "PRINT":
                step.print = value;
                break;
            case "MOUSE":
                step.mouse = value;
                break;
            case "FIND":
                step.find = value;
                break;
            case "CLICK":
                step.click = value;
                break;
            case "POPUP":
                step.popup = value;
                break;
            case "TYPE":
                step.type = value;
                break;
            case "MESSAGE":
                step.message = value;
                break;
            case "EXECUTE":
                step.execute = value;
                break;
            case "WHEN":
                step.when = value;
                break;
            case "THEN":
                step.then = value;
                break;
            case "JUMP":
                step.jump = value;
                break;
            case "PAUSE":
                step.pause = value;
                break;
            default:
                step = null;
        }
        return step;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        if (StringUtility.isNotEmpty(label)) {
            list.add(String.format("LABEL %s", label));
        }
        if (StringUtility.isNotEmpty(print)) {
            list.add(String.format("PRINT %s", print));
        }
        if (StringUtility.isNotEmpty(path)) {
            list.add(String.format("PATH %s", path));
        }
        if (StringUtility.isNotEmpty(delay)) {
            list.add(String.format("DELAY %s", delay));
        }
        if (StringUtility.isNotEmpty(similarity)) {
            list.add(String.format("SIMILARITY %s", similarity));
        }
        if (StringUtility.isNotEmpty(wait)) {
            list.add(String.format("WAIT %s", wait));
        }
        if (StringUtility.isNotEmpty(mouse)) {
            list.add(String.format("MOUSE %s", mouse));
        }
        if (StringUtility.isNotEmpty(find)) {
            list.add(String.format("FIND %s", find));
        }
        if (StringUtility.isNotEmpty(click)) {
            list.add(String.format("CLICK %s", click));
        }
        if (StringUtility.isNotEmpty(popup)) {
            list.add(String.format("POPUP %s", popup));
        }
        if (StringUtility.isNotEmpty(type)) {
            list.add(String.format("TYPE %s", type));
        }
        if (StringUtility.isNotEmpty(message)) {
            list.add(String.format("MESSAGE %s", message));
        }
        if (StringUtility.isNotEmpty(execute)) {
            list.add(String.format("EXECUTE %s", execute));
        }
        if (StringUtility.isNotEmpty(when)) {
            list.add(String.format("WHEN %s", when));
        }
        if (StringUtility.isNotEmpty(then)) {
            list.add(String.format("THEN %s", then));
        }
        if (StringUtility.isNotEmpty(jump)) {
            list.add(String.format("JUMP %s", jump));
        }
        if (StringUtility.isNotEmpty(pause)) {
            list.add(String.format("PAUSE %s", pause));
        }
        return String.join(String.format("%n"), list);
    }

}
