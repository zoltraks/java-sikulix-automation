package pl.alyx.robot.sikulix.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtility {

    public static String getExtension(String fileName) {
        if (null == fileName) {
            return "";
        }
        fileName = fileName.trim();
        if (0 == fileName.length()) {
            return "";
        }
        int i1 = fileName.lastIndexOf('/');
        int i2 = fileName.lastIndexOf('\\');
        if (i2 > i1) {
            i1 = i2;
        }
        if (0 <= i1) {
            fileName = fileName.substring(i1 + 1);
        }
        int i3 = fileName.lastIndexOf('.');
        if (0 > i3) {
            return "";
        } else {
            return fileName.substring(i3);
        }
    }

    public static boolean hasExtension(String fileName) {
        String extension = getExtension(fileName);
        return 0 < extension.length();
    }

    public static boolean fileExists(String fileName) {
        boolean result;
        Path path = Paths.get(fileName);
        result = Files.exists(path);
        return result;
    }

}
