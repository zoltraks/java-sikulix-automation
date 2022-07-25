package pl.alyx.robot.sikulix;

import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.StringUtility;

public class Flow {

    private final Scenario scenario;

    private final State state;

    public Flow(State state, Scenario scenario) {
        this.state = state;
        this.scenario = scenario;
    }

    private int index;

    public boolean next() {
        if (index >= this.scenario.steps.size()) {
            return false;
        }
        Step step = this.scenario.steps.get(index);
        index++;
        Result result = new Automation(state, step).step();
        if (StringUtility.isNotEmpty(result.jump)) {
            int labelIndex = findLabel(result.jump);
            if (0 <= labelIndex) {
                index = labelIndex;
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

}
