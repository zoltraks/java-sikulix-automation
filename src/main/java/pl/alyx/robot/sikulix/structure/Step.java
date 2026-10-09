package pl.alyx.robot.sikulix.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
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

    public JsonElement press;

    public String message;

    public String execute;

    public String when;

    public String then;

    public String jump;

    public String print;

    public String pause;

    private interface Setter {
        void set(Step step, String value);
    }

    private interface Emitter {
        String emit(Step step);
    }

    private static final class Operation {
        final String keyword;
        final Setter setter;
        final Emitter emitter;

        Operation(String keyword, Setter setter, Emitter emitter) {
            this.keyword = keyword;
            this.setter = setter;
            this.emitter = emitter;
        }
    }

    private static final List<Operation> OPERATIONS = new ArrayList<>();

    static {
        OPERATIONS.add(new Operation("LABEL", (s, v) -> s.label = v, s -> emit("LABEL", s.label)));
        OPERATIONS.add(new Operation("PRINT", (s, v) -> s.print = v, s -> emit("PRINT", s.print)));
        OPERATIONS.add(new Operation("PATH", (s, v) -> s.path = v, s -> emit("PATH", s.path)));
        OPERATIONS.add(new Operation("DELAY", (s, v) -> s.delay = v, s -> emit("DELAY", s.delay)));
        OPERATIONS.add(new Operation("SIMILARITY", (s, v) -> s.similarity = v, s -> emit("SIMILARITY", s.similarity)));
        OPERATIONS.add(new Operation("WAIT", (s, v) -> s.wait = v, s -> emit("WAIT", s.wait)));
        OPERATIONS.add(new Operation("MOUSE", (s, v) -> s.mouse = v, s -> emit("MOUSE", s.mouse)));
        OPERATIONS.add(new Operation("FIND", (s, v) -> s.find = v, s -> emit("FIND", s.find)));
        OPERATIONS.add(new Operation("CLICK", (s, v) -> s.click = v, s -> emit("CLICK", s.click)));
        OPERATIONS.add(new Operation("POPUP", (s, v) -> s.popup = v, s -> emit("POPUP", s.popup)));
        OPERATIONS.add(new Operation("TYPE", (s, v) -> s.type = v, s -> emit("TYPE", s.type)));
        OPERATIONS.add(new Operation("PRESS", Step::setPress, Step::emitPress));
        OPERATIONS.add(new Operation("MESSAGE", (s, v) -> s.message = v, s -> emit("MESSAGE", s.message)));
        OPERATIONS.add(new Operation("EXECUTE", (s, v) -> s.execute = v, s -> emit("EXECUTE", s.execute)));
        OPERATIONS.add(new Operation("WHEN", (s, v) -> s.when = v, s -> emit("WHEN", s.when)));
        OPERATIONS.add(new Operation("THEN", (s, v) -> s.then = v, s -> emit("THEN", s.then)));
        OPERATIONS.add(new Operation("JUMP", (s, v) -> s.jump = v, s -> emit("JUMP", s.jump)));
        OPERATIONS.add(new Operation("PAUSE", (s, v) -> s.pause = v, s -> emit("PAUSE", s.pause)));
    }

    private static String emit(String keyword, String value) {
        return StringUtility.isNotEmpty(value)
                ? String.format("%s %s", keyword, value)
                : null;
    }

    private static void setPress(Step step, String value) {
        List<String> tokens = StringUtility.splitTokens(value);
        if (1 == tokens.size()) {
            step.press = new JsonPrimitive(tokens.get(0));
        } else if (1 < tokens.size()) {
            JsonArray array = new JsonArray();
            for (String token : tokens) {
                array.add(token);
            }
            step.press = array;
        }
    }

    private static String emitPress(Step step) {
        JsonElement press = step.press;
        if (null == press || press.isJsonNull()) {
            return null;
        }
        List<String> values = new ArrayList<>();
        if (press.isJsonArray()) {
            for (JsonElement element : press.getAsJsonArray()) {
                values.add(element.getAsString());
            }
        } else if (press.isJsonPrimitive()) {
            values.add(press.getAsString());
        }
        return values.isEmpty()
                ? null
                : String.format("PRESS %s", String.join(" ", values));
    }

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
        for (Operation operation : OPERATIONS) {
            if (operation.keyword.equals(key)) {
                operation.setter.set(step, value);
                return step;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        for (Operation operation : OPERATIONS) {
            String line = operation.emitter.emit(this);
            if (null != line) {
                list.add(line);
            }
        }
        return String.join(String.format("%n"), list);
    }

}
