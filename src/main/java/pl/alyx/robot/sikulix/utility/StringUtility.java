package pl.alyx.robot.sikulix.utility;

public final class StringUtility {

    public static boolean stringToBoolean(String value) {
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

    public static String stringValue(String value) {
        return null == value ? "" : value;
    }

    public static double stringToDouble(String value) {
        double result = 0;
        if (null != value && 0 < value.length()) {
            try {
                result = Double.parseDouble(value);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    public static boolean isNotEmpty(String value) {
        return null != value && 0 < value.trim().length();
    }

    //region Quote

    public static String quote(String text, String quote, String escape) {
        if (null == text) {
            text = "";
        } else {
            text = text.replace(quote, escape + quote);
        }
        text = quote + text + quote;
        return text;
    }

    public static String quote(String text, char quote, char escape) {
        return quote(text, "" + quote, "" + escape);
    }

    public static String quote(String text, char quote) {
        return quote(text, "" + quote, "" + quote);
    }

    public static String quote(String text, String quote) {
        return quote(text, quote, quote);
    }

    public static String quote(String text) {
        return quote(text, "\"", "\"");
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
            text = quote(text, quote, escape);
        }
        return text;
    }

    public static String QuoteVolatile(String text, String quote) {
        return QuoteVolatile(text, quote, quote);
    }

    public static String QuoteVolatile(String text) {
        return QuoteVolatile(text, "\"");
    }

    public static boolean isEmpty(String text) {
        return !isNotEmpty(text);
    }

    public static boolean isWhite(String text) {
        return null == text || 0 == text.trim().length();
    }

    public static boolean isNotWhite(String text) {
        return null != text && 0 < text.trim().length();
    }

}
