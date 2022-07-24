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
            list.add(String.format("LABEL %s", StringUtility.QuoteVolatile(label)));
        }
        if (StringUtility.isNotEmpty(path)) {
            list.add(String.format("PATH %s", StringUtility.QuoteVolatile(path)));
        }
        if (StringUtility.isNotEmpty(wait)) {
            list.add(String.format("WAIT %s", StringUtility.QuoteVolatile(wait)));
        }
        if (StringUtility.isNotEmpty(mouse)) {
            list.add(String.format("MOUSE %s", StringUtility.QuoteVolatile(mouse)));
        }
        if (StringUtility.isNotEmpty(find)) {
            list.add(String.format("FIND %s", StringUtility.QuoteVolatile(find)));
        }
        if (StringUtility.isNotEmpty(click)) {
            list.add(String.format("CLICK %s", StringUtility.QuoteVolatile(click)));
        }
        if (StringUtility.isNotEmpty(type)) {
            list.add(String.format("TYPE %s", StringUtility.QuoteVolatile(type)));
        }
        if (StringUtility.isNotEmpty(message)) {
            list.add(String.format("MESSAGE %s", StringUtility.QuoteVolatile(message)));
        }
        if (StringUtility.isNotEmpty(run)) {
            list.add(String.format("RUN %s", StringUtility.QuoteVolatile(run)));
        }
        if (StringUtility.isNotEmpty(when)) {
            list.add(String.format("WHEN %s", StringUtility.QuoteVolatile(when)));
        }
        if (StringUtility.isNotEmpty(then)) {
            list.add(String.format("THEN %s", StringUtility.QuoteVolatile(then)));
        }
        if (StringUtility.isNotEmpty(jump)) {
            list.add(String.format("JUMP %s", StringUtility.QuoteVolatile(jump)));
        }
        return String.join(String.format("%n"), list);
    }

}
