package fr.veridiangames.uberstrap.utils;

import fr.veridiangames.uberstrap.exceptions.InvalidSystemException;

import java.io.File;
import java.io.IOException;

public class FileUtils
{
    public static void deleteFolder(File dir)
    {
        if(dir == null || !dir.exists()) return;

        final File[] files = dir.listFiles();
        if(files != null)
            for (File f: files) deleteFolder(f);
        dir.delete();
    }

    public static String getAppDataDir(int os) throws InvalidSystemException
    {
        String wdPath = "";

        switch (os)
        {
            case OsChecker.WINDOWS:
                wdPath = System.getenv("APPDATA");
                break;
            case OsChecker.LINUX:
                wdPath = System.getProperty("user.home");
                break;
            case OsChecker.MAC:
                wdPath = System.getProperty("user.home") + "/Library/Application Support";
                break;
            default:
                throw new InvalidSystemException();
        }

        return wdPath;
    }

    public static File createDir (String pathToNewDir) throws IOException
    {
        File dir = new File(pathToNewDir);
        if (!dir.exists() && !dir.mkdirs())
            throw new IOException("Can't create launcher directory...");
        return dir;
    }
}
