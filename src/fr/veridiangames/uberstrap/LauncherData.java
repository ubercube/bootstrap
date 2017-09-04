package fr.veridiangames.uberstrap;

import fr.veridiangames.uberstrap.utils.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LauncherData
{
    private String remoteVersion;
    private String localVersion;

    private String versionDirPath;

    private String[] launcherLink;
    private String[] executable;

    public LauncherData(String url, File launcherDir) throws IOException
    {
        this.getRemoteInfos(url);
        this.getLocalInfos(launcherDir);
    }

    public void downloadAndUpdate(File launcherDir) throws IOException
    {
        if (launcherDir.exists() && launcherDir.isDirectory())
            for (File content : launcherDir.listFiles())
                FileUtils.deleteFolder(content);

        File archive = new File(versionDirPath + File.separatorChar + "launcherarchive.zip.temp");
        JFrame frame = new JFrame("Updating Ubercube launcher...");
        JProgressBar bar = new JProgressBar(0, 100);
        frame.setVisible(true);
        frame.setSize(500, 80);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.add(bar);
        DownloadUtils.downloadMAJ(launcherLink[OsChecker.getOsId()], archive, bar);
        frame.dispose();
        frame.setTitle("Extracting Launcher...");
        bar.setIndeterminate(true);
        frame.setVisible(true);
        ZipUtils.unzip(archive.getCanonicalPath(), versionDirPath);
        frame.setTitle("Deleting temp files...");
        archive.delete();
        frame.dispose();
    }

    private void getRemoteInfos(String url) throws IOException
    {
        BufferedReader br = HttpUtils.getUrlBufferedReader(url);
        this.launcherLink = new String[3];
        this.executable = new String[3];

        this.remoteVersion = br.readLine();
        this.launcherLink[OsChecker.WINDOWS] = br.readLine();
        this.launcherLink[OsChecker.LINUX] = br.readLine();
        this.launcherLink[OsChecker.MAC] = br.readLine();

        for (int i = 0; i < 3; i++)
        {
            this.executable[i] = this.launcherLink[i].split(" ")[1];
            this.launcherLink[i] = this.launcherLink[i].split(" ")[0];
        }
    }

    private void getLocalInfos (File launcherDir) throws IOException
    {
        this.versionDirPath = launcherDir.getCanonicalPath();
        File f = new File(this.versionDirPath + File.separatorChar + ".version");
        if (f.exists())
        {
            BufferedReader br = new BufferedReader(new FileReader(f));
            this.localVersion = br.readLine();
            br.close();
        }
    }

    public String getRemoteVersion()
    {
        return remoteVersion;
    }

    public String getLocalVersion()
    {
        return localVersion;
    }

    public String getVersionDirPath()
    {
        return versionDirPath;
    }

    public String getExecutable(int os)
    {
        return executable[os];
    }

    public String getLauncherLink(int os)
    {
        return launcherLink[os];
    }
}
