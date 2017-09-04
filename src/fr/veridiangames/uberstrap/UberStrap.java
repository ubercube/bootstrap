package fr.veridiangames.uberstrap;

import fr.veridiangames.uberstrap.exceptions.InvalidSystemException;
import fr.veridiangames.uberstrap.utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class UberStrap
{
    private File		wd;
    private File		launcherDir;
    private int			currentOs;

    public static boolean	debug;

    public static void	main(String[] args)
    {
        UberStrap		main;
        File			toDelete;
        String			a;

        main = new UberStrap();

        try
        {
            main.setDirTree();
            main.verifyOtherBootstrap();
            main.update();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public UberStrap()
    {
        this.currentOs = OsChecker.getOsId();
    }

    public void verifyOtherBootstrap() throws IOException
    {
        File bootsrapFile = new File(this.wd.getCanonicalPath() + File.separatorChar + "uberstrap.jar");
        if(bootsrapFile.exists())
        {
            Runtime.getRuntime().exec("java -jar uberstrap.jar", null, this.wd);
            System.out.println("Launch new bootstrap !");
            System.exit(0);
        }
    }

    public void	update() throws IOException
    {
        LauncherData data = new LauncherData("https://ubercube.github.io/download/launcher/info.udf", this.launcherDir);

        System.out.println("Local  version : " + data.getLocalVersion());
        System.out.println("Online version : " + data.getRemoteVersion());

        String launcherLink = data.getLauncherLink(this.currentOs);
        String executable = data.getExecutable(this.currentOs);

        if (!data.getRemoteVersion().equals(data.getLocalVersion()))
        {
            System.out.println("Should download from : " + launcherLink + " to " + data.getVersionDirPath());
            data.downloadAndUpdate(this.launcherDir);
        }

        File launcher = new File(data.getVersionDirPath() + File.separatorChar + executable);

        System.out.println("Executing launcher : " + launcher.getCanonicalPath());

        // exec the launcher
        if(launcher.getCanonicalPath().matches("^.*.jar$"))
            Runtime.getRuntime().exec("java -jar " + executable, null, this.launcherDir);
        else
            Runtime.getRuntime().exec(executable, null, this.launcherDir);
    }

    public void setDirTree() throws IOException, InvalidSystemException
    {
        String wdPath = FileUtils.getAppDataDir(this.currentOs) + File.separatorChar + ".ubercube";
        this.wd = FileUtils.createDir(wdPath);
        this.launcherDir = FileUtils.createDir(wdPath + File.separatorChar + "launcher");
    }
}
