package pl.alyx.robot.sikulix.structure;

import pl.alyx.robot.sikulix.utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class Step {

    public String label;

    public String path;

    public String wait;

    public String find;

    public String click;

    public String type;

    public String mouse;

    public String message;

    public String run;

    public String when;

    public String then;

    public String jump;

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        if (StringUtility.isNotEmpty(label)) {
            list.add(String.format("LABEL %s", label));
        }
        if (StringUtility.isNotEmpty(path)) {
            list.add(String.format("PATH %s", path));
        }
        if (StringUtility.isNotEmpty(wait)) {
            list.add(String.format("WAIT %s", wait));
        }
        if (StringUtility.isNotEmpty(mouse)) {
            list.add(String.format("MOUSE %s", StringUtility.Quote(mouse)));
        }
        if (StringUtility.isNotEmpty(find)) {
            list.add(String.format("FIND %s", click));
        }
        if (StringUtility.isNotEmpty(click)) {
            list.add(String.format("CLICK %s", click));
        }
        if (StringUtility.isNotEmpty(type)) {
            list.add(String.format("TYPE %s", type));
        }
        if (StringUtility.isNotEmpty(message)) {
            list.add(String.format("MESSAGE %s", message));
        }
        if (StringUtility.isNotEmpty(when)) {
            list.add(String.format("WHEN %s", when));
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
        return String.join(String.format("%n"), list);
    }

}
