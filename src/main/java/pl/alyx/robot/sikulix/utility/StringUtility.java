package pl.alyx.robot.sikulix.utility;

public final class StringUtility {

    public static boolean StringToBoolean(String value) {
        if (null == value) {
            return false;
        }
        value = value.trim();
        if (0 == value.length()) {
            return false;
        }
        switch (value.toUpperCase()) {
            case "0":
            case "FALSE":
            case "NO":
            case "NULL":
            case "NONE":
            case "N":
                return false;
            default:
                return true;
        }
    }

    public static String StringValue(String value) {
        return null == value ? "" : value;
    }

    public static double StringToDouble(String value) {
        double result = 0;
        if (null != value && 0 < value.length()) {
            try {
                result = Double.parseDouble(value);
            }
            catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    public static boolean isNotEmpty(String value) {
        return null != value && 0 < value.trim().length();
    }

    //region Quote

    public static String Quote(String text, String quote, String escape) {
        if (null == text) {
            text = "";
        } else {
            text = text.replace(quote, escape + quote);
        }
        text = quote + text + quote;
        return text;
    }

    public static String Quote(String text, char quote, char escape) {
        return Quote(text, "" + quote, "" + escape);
    }

    public static String Quote(String text, char quote) {
        return Quote(text, "" + quote, "" + quote);
    }

    public static String Quote(String text, String quote) {
        return Quote(text, quote, quote);
    }

    public static String Quote(String text) {
        return Quote(text, "\"", "\"");
    }

    //endregion

    public static String QuoteVolatile(String text, String quote, String escape) {
        if (null == text || 0 == text.length()) {
            return text;
        }
        boolean need = text.contains(quote);
        if (!need) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (32 >= c) {
                    need = true;
                    break;
                }
            }
        }
        if (need) {
            text = Quote(text, quote, escape);
        }
        return text;
    }

    public static String QuoteVolatile(String text, String quote) {
        return QuoteVolatile(text, quote, quote);
    }

    public static String QuoteVolatile(String text) {
        return QuoteVolatile(text, "\"");
    }

}
