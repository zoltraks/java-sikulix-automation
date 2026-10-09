package pl.alyx.robot.sikulix.logic;

import com.google.gson.JsonElement;
import org.sikuli.script.Button;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Screen;
import pl.alyx.robot.sikulix.Result;
import pl.alyx.robot.sikulix.Context;
import pl.alyx.robot.sikulix.structure.Step;
import pl.alyx.robot.sikulix.utility.StringUtility;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        applyPath();
        applyTiming();
        applySimilarity();
        applyPrint();
        applyMouse(screen);
        if (!applyFind(screen, result)) {
            return result;
        }
        if (!applyClick(screen, result)) {
            return result;
        }
        if (!applyPopup(screen, result)) {
            return result;
        }
        applyType(screen);
        if (!applyPress(result)) {
            return result;
        }
        applyMessage();
        if (!applyExecute(result)) {
            return result;
        }
        applyCondition(screen, result);
        applyPause(screen);

        if (context.state.delay > 0.0) {
            screen.wait(context.state.delay);
        }

        return result;

    }

    private void applyPath() {
        String path = this.step.path;
        if (StringUtility.isNotWhite(path)) {
            ImagePath.setBundlePath(Paths.get(path).toAbsolutePath().toString());
            if (context.settings.isVerbose()) {
                System.out.printf("ImagePath: %s%n", ImagePath.getBundlePath());
            }
        }
    }

    private void applyTiming() {
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
    }

    private void applySimilarity() {
        String similarity = this.step.similarity;
        if (null != similarity && 0 < similarity.length()) {
            double value = StringUtility.stringToDouble(similarity);
            if (value > 0) {
                org.sikuli.basics.Settings.MinSimilarity = value;
                if (context.settings.isVerbose()) {
                    System.out.printf("Similarity set to %.1f%n", value);
                }
            }
        }
    }

    private void applyPrint() {
        String print = this.step.print;
        if (StringUtility.isNotEmpty(print)) {
            System.out.println(print);
        }
    }

    private void applyMouse(Screen screen) {
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
    }

    private interface FindAction {
        void run() throws FindFailed;
    }

    private boolean attempt(String label, FindAction action, Result result) {
        try {
            action.run();
            return true;
        } catch (FindFailed e) {
            result.error = String.format("%s failed for %s", label, e.getMessage());
            result.success = false;
            return false;
        }
    }

    private boolean applyFind(Screen screen, Result result) {
        String find = this.step.find;
        return !StringUtility.isNotWhite(find)
                || attempt("Find", () -> screen.find(find), result);
    }

    private boolean applyClick(Screen screen, Result result) {
        String click = this.step.click;
        return !StringUtility.isNotWhite(click)
                || attempt("Click", () -> screen.click(click), result);
    }

    private boolean applyPopup(Screen screen, Result result) {
        String popup = this.step.popup;
        return !StringUtility.isNotEmpty(popup)
                || attempt("Right click", () -> screen.rightClick(popup), result);
    }

    private void applyType(Screen screen) {
        String type = this.step.type;
        if (StringUtility.isNotEmpty(type)) {
            screen.type(type);
        }
    }

    private boolean applyPress(Result result) {
        JsonElement press = this.step.press;
        if (null != press && !press.isJsonNull()) {
            String error = press(press);
            if (null != error) {
                result.error = error;
                result.success = false;
                return false;
            }
        }
        return true;
    }

    private void applyMessage() {
        String message = this.step.message;
        if (StringUtility.isNotWhite(message)) {
            JOptionPane.showMessageDialog(null, message);
        }
    }

    private boolean applyExecute(Result result) {
        String execute = this.step.execute;
        if (StringUtility.isNotWhite(execute)) {
            try {
                Runtime.getRuntime().exec(execute);
            } catch (IOException e) {
                result.error = String.format("Execute failed for %s", execute);
                result.success = false;
                return false;
            }
        }
        return true;
    }

    private void applyCondition(Screen screen, Result result) {
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
    }

    private void applyPause(Screen screen) {
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
    }

    private String press(JsonElement element) {
        List<String> specs = new ArrayList<>();
        if (element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                specs.add(item.getAsString());
            }
        } else {
            specs.add(element.getAsString());
        }
        for (String spec : specs) {
            String error = pressKey(spec);
            if (null != error) {
                return error;
            }
        }
        return null;
    }

    private String pressKey(String spec) {
        if (null == spec || 0 == spec.length()) {
            return "Press failed for empty key";
        }
        String key;
        int modifiers = 0;
        if (1 == spec.length()) {
            key = spec;
        } else {
            spec = spec.trim();
            if (0 == spec.length()) {
                return "Press failed for empty key";
            }
            key = spec;
            if (spec.contains("+")) {
                String[] tokens = spec.split("\\+");
                if (2 <= tokens.length) {
                    for (int i = 0; i < tokens.length - 1; i++) {
                        Integer modifier = MODIFIER_NAMES.get(tokens[i].trim().toUpperCase());
                        if (null == modifier) {
                            return String.format("Press failed for %s", spec);
                        }
                        modifiers |= modifier;
                    }
                    key = tokens[tokens.length - 1].trim();
                }
            }
            int i = 0;
            while (i < key.length()) {
                Integer modifier = MODIFIER_SYMBOLS.get(key.charAt(i));
                if (null == modifier) {
                    break;
                }
                modifiers |= modifier;
                i++;
            }
            key = key.substring(i);
        }
        String resolved = resolveKey(key);
        if (null == resolved) {
            return String.format("Press failed for %s", spec);
        }
        this.context.screen.type(resolved, modifiers);
        return null;
    }

    private static String resolveKey(String name) {
        if (1 == name.length()) {
            return name;
        }
        return KEY_NAMES.get(name.toUpperCase());
    }

    private static final Map<Character, Integer> MODIFIER_SYMBOLS;

    private static final Map<String, Integer> MODIFIER_NAMES;

    private static final Map<String, String> KEY_NAMES;

    static {
        Map<Character, Integer> symbols = new HashMap<>();
        symbols.put('!', KeyModifier.SHIFT);
        symbols.put('@', KeyModifier.ALT);
        symbols.put('^', KeyModifier.CTRL);
        symbols.put('$', KeyModifier.WIN);
        MODIFIER_SYMBOLS = Collections.unmodifiableMap(symbols);

        Map<String, Integer> names = new HashMap<>();
        names.put("CTRL", KeyModifier.CTRL);
        names.put("CONTROL", KeyModifier.CTRL);
        names.put("ALT", KeyModifier.ALT);
        names.put("SHIFT", KeyModifier.SHIFT);
        names.put("WIN", KeyModifier.WIN);
        names.put("META", KeyModifier.WIN);
        names.put("CMD", KeyModifier.WIN);
        MODIFIER_NAMES = Collections.unmodifiableMap(names);

        Map<String, String> keys = new HashMap<>();
        keys.put("ENTER", Key.ENTER);
        keys.put("TAB", Key.TAB);
        keys.put("ESC", Key.ESC);
        keys.put("ESCAPE", Key.ESC);
        keys.put("BACKSPACE", Key.BACKSPACE);
        keys.put("SPACE", Key.SPACE);
        keys.put("UP", Key.UP);
        keys.put("DOWN", Key.DOWN);
        keys.put("LEFT", Key.LEFT);
        keys.put("RIGHT", Key.RIGHT);
        keys.put("PAGE_UP", Key.PAGE_UP);
        keys.put("PGUP", Key.PAGE_UP);
        keys.put("PAGE_DOWN", Key.PAGE_DOWN);
        keys.put("PGDN", Key.PAGE_DOWN);
        keys.put("DELETE", Key.DELETE);
        keys.put("DEL", Key.DELETE);
        keys.put("INSERT", Key.INSERT);
        keys.put("INS", Key.INSERT);
        keys.put("HOME", Key.HOME);
        keys.put("END", Key.END);
        keys.put("F1", Key.F1);
        keys.put("F2", Key.F2);
        keys.put("F3", Key.F3);
        keys.put("F4", Key.F4);
        keys.put("F5", Key.F5);
        keys.put("F6", Key.F6);
        keys.put("F7", Key.F7);
        keys.put("F8", Key.F8);
        keys.put("F9", Key.F9);
        keys.put("F10", Key.F10);
        keys.put("F11", Key.F11);
        keys.put("F12", Key.F12);
        keys.put("F13", Key.F13);
        keys.put("F14", Key.F14);
        keys.put("F15", Key.F15);
        keys.put("CAPS_LOCK", Key.CAPS_LOCK);
        keys.put("NUM_LOCK", Key.NUM_LOCK);
        keys.put("SCROLL_LOCK", Key.SCROLL_LOCK);
        keys.put("PRINTSCREEN", Key.PRINTSCREEN);
        keys.put("PRTSC", Key.PRINTSCREEN);
        keys.put("PAUSE", Key.PAUSE);
        keys.put("CONTEXT", Key.CONTEXT);
        keys.put("ADD", Key.ADD);
        keys.put("MINUS", Key.MINUS);
        keys.put("MULTIPLY", Key.MULTIPLY);
        keys.put("DIVIDE", Key.DIVIDE);
        keys.put("DECIMAL", Key.DECIMAL);
        keys.put("SEPARATOR", Key.SEPARATOR);
        keys.put("NEXT", Key.NEXT);
        keys.put("ALT", Key.ALT);
        keys.put("CTRL", Key.CTRL);
        keys.put("SHIFT", Key.SHIFT);
        keys.put("WIN", Key.WIN);
        KEY_NAMES = Collections.unmodifiableMap(keys);
    }
}
