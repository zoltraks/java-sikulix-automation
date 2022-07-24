package pl.alyx.robot.sikulix;

import pl.alyx.robot.sikulix.structure.Scenario;
import pl.alyx.robot.sikulix.structure.Step;

public class Flow {

    private final Scenario scenario;

    private final State state;

    public Flow(State state, Scenario scenario) {
        this.state = state;
        this.scenario = scenario;
    }

    private int index;

    public boolean next() {
        if (index >= scenario.steps.size()) {
            return false;
        }
        Step step = this.scenario.steps.get(index);
        index++;
        return new Automation(state, step).step();
    }
}
