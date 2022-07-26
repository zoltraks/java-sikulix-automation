package pl.alyx.robot.sikulix.structure;

import pl.alyx.robot.sikulix.utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class Scenario {

    public String name;

    public List<Step> steps;

    public String path;

    public String delay;

    public String toString() {
        List<String> list = new ArrayList<>();
        if (StringUtility.isNotEmpty(this.path)) {
            list.add(String.format("PATH %s", StringUtility.QuoteVolatile(this.path)));
        }
        if (StringUtility.isNotEmpty(this.delay)) {
            list.add(String.format("DELAY %s", StringUtility.QuoteVolatile(this.delay)));
        }
        if (null != this.steps && 0 < this.steps.size()) {
            for (Step step : this.steps) {
                list.add(step.toString());
            }
        }
        String result;
        result = String.join(String.format("%n"), list);
        return result;
    }

}
