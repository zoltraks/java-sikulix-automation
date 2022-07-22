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
            case "FALSE":
            case "NO":
            case "NULL":
            case "NONE":
            case "N":
            case "0":
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
            catch (NumberFormatException e) {
            }
        }
        return result;
    }

    public static boolean isNotEmpty(String value) {
        return null == value ? false : 0 < value.trim().length();
    }

}
