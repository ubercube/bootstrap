package fr.veridiangames.uberstrap.utils;

import java.util.Locale;

public class OsChecker {

    public static final int WINDOWS = 0;
    public static final int LINUX = 1;
    public static final int MAC = 2;
    public static final int NULL = 3;

    public static int getOsId()
    {
        int detectedOS = NULL;

        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            detectedOS = MAC;
        } else if (OS.contains("win")) {
            detectedOS = WINDOWS;
        } else if (OS.contains("nux")) {
            detectedOS = LINUX;
        }

        return  detectedOS;
    }

    public static String getOsName()
    {
        switch (getOsId())
        {
            case WINDOWS:
                return "windows";
            case LINUX:
                return "linux";
            case MAC:
                return "macos";
            default:
                System.err.println("Os not found !");
                System.exit(1);
        }
        return null;
    }
}
