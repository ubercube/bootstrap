package fr.veridiangames.uberstrap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import net.jimmc.jshortcut.JShellLink;

/*
 * Appdata/
 * 	.ubercube/
 * 		ubercube.jar <- bootstrap
 * 		data/
 * 			gamefiles
 * 		launcher/
 * 				version
 * 				iconengines/
 * 				imageformats/
 * 				platforms/
 * 				translations/
 * 				d3dcompiler_47.dll
 * 				libEGL.dll
 * 				opengl32sw.dll
 * 				Qt5Core.dll
 * 				Qt5Gui.dll
 * 				Qt5Svg.dll
 * 				Qt5Widgets.dll
 * 				UbercubeLauncher.exe
 * 
 */

public class UberStrap
{
	private enum Os
	{
		WINDOWS(0),
		LINUX(1),
		MACOS(2);
		
		int val;
		
		Os(int i)
		{
			val = i;
		}
		
		int getValue()
		{
			return (val);
		}
	};
	
	private File		wd;
	private File		launcherDir;
	private Os			currentOs;
	static ServerSocket	ss;
	
	public static boolean	debug;
	
	public static void	main(String[] args)
	{
		//preventing from double start
		try
		{
			ss = new ServerSocket(65535, 1, InetAddress.getLocalHost());	
		}
		catch (IOException e)
		{
			System.err.println("Another instance of the program is currently in use...");
			System.exit(0);
		}
		
		UberStrap		main;
		File			toDelete;
		String			a;
		
		main = new UberStrap();
		
		debug = false;
		toDelete = null;
		for (int i = 0; i < args.length; i++)
		{
			a = args[i];
			System.out.println("argument : " + a);
			if (a.equals("debug"))
				debug = true;
			if (a.equals("delete"))
				toDelete = new File(args[i + 1]);
		}
		
		if (!debug && toDelete != null && toDelete.exists())
			toDelete.delete();
		
		try
		{
			main.setDirTree();
			main.update();
			ss.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public			UberStrap() {}
	
	public void		deleteFile(final File f)
	{
		if (!f.exists())
			return;
		if (f.isDirectory())
			for (File c : f.listFiles())
				deleteFile(c);
		f.delete();
	}
	
	public void		update() throws IOException
	{
		String			versionDirPath;
		String			launcherLink[] = new String[3];
		String			executable[] = new String[3];
		
		String			localVersion;
		String			remoteVersion;	
		
		BufferedReader	br;
		
		localVersion = null;
		
		//getting remote version and launcher download link
		{
			URL versionUrl = new URL("https://ubercube.github.io/download/launcher/info.udf");
			try
			{
				br = new BufferedReader(new InputStreamReader(versionUrl.openStream()));
			}
			catch (IOException e)
			{
				System.err.println("Unable to find update infos...");
				return;
			}
			
			remoteVersion = br.readLine();
			launcherLink[Os.WINDOWS.getValue()] = br.readLine();
			launcherLink[Os.LINUX.getValue()] = br.readLine();
			launcherLink[Os.MACOS.getValue()] = br.readLine();
			br.close();
			
			for (int i = 0; i < 3; i++)
			{
				executable[i] = launcherLink[i].split(" ")[1];
				launcherLink[i] = launcherLink[i].split(" ")[0];
			}			
		}
		
		// getting local version
		{
			versionDirPath = this.launcherDir.getCanonicalPath();
			File f = new File(versionDirPath + File.separatorChar + "version");
			if (f.exists())
			{
				br = new BufferedReader(new FileReader(f));
				localVersion = br.readLine();
				br.close();
			}
		}
		
		System.out.println("Local  version : " + localVersion);
		System.out.println("Online version : " + remoteVersion);
		if (!remoteVersion.equals(localVersion))
		{
			System.out.println("Should download from : " + launcherLink[this.currentOs.getValue()] + " to " + versionDirPath);
		
			if (launcherDir.exists() && launcherDir.isDirectory())
				for (File content : launcherDir.listFiles())
					deleteFile(content);
			
			File archive = new File(versionDirPath + File.separatorChar + "launcherarchive.zip.temp");
			JFrame frame = new JFrame("Updating Ubercube launcher...");
			JProgressBar bar = new JProgressBar(0, 100); 
			frame.setVisible(true);
			frame.setSize(500, 80);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.add(bar);
			DownloadUtils.downloadMAJ(launcherLink[this.currentOs.getValue()], archive, bar);
			frame.dispose();
			frame.setTitle("Extracting Launcher...");
			bar.setIndeterminate(true);
			frame.setVisible(true);
			ZipUtils.unzip(archive.getCanonicalPath(), versionDirPath);
			frame.setTitle("Deleting temp files...");
			archive.delete();
			frame.dispose();	
		}
		File launcher = new File(versionDirPath + File.separatorChar + executable[this.currentOs.getValue()]);
		
		System.out.println("Executing launcher : " + launcher.getCanonicalPath());
		
		// exec the launcher
		Runtime.getRuntime().exec(launcher.getCanonicalPath());
	}
	
	public void		setDirTree() throws IOException
	{
		String		wdPath;
		
		// getting working directory path
		{
			String os = (System.getProperty("os.name")).toUpperCase();
			
			if (os.contains("WIN") && !os.contains("DARWIN"))
			{
				wdPath = System.getenv("APPDATA");
				this.currentOs = Os.WINDOWS;
			}
			else
			{
				wdPath = System.getProperty("user.home");
				this.currentOs = Os.LINUX;
			}
			
			if (os.contains("MAC") || os.contains("DARWIN"))
			{
				wdPath += "/Library/Application Support";
				this.currentOs = Os.MACOS;
			}
			
			wdPath += File.separatorChar + ".ubercube";
		}
		
		// .ubercube
		{
			this.wd = new File(wdPath);
			if (!this.wd.exists() && this.wd.mkdirs() == false)
				throw new IOException("Can't create game directory...");
		}
		
		// .ubercube/launcher
		{
			this.launcherDir = new File(wdPath + File.separatorChar + "launcher");
			if (!this.launcherDir.exists() && this.launcherDir.mkdirs() == false)
				throw new IOException("Can't create launcher directory...");
		}
		
		// copy the bootstrap if the current working directory is not correct
		{
			String	jar;
			String	currentWD;
			File	current;
									
			current = new File(System.getProperty("java.class.path"));
			jar = File.separatorChar + current.getName();
			currentWD = current.getAbsolutePath();
			currentWD = currentWD.substring(0, currentWD.length() - jar.length());
			while (currentWD.endsWith("\\."))
				currentWD = currentWD.substring(0, currentWD.length() - 2);
				
			if (currentWD.equals(wdPath))
				System.out.println("Correct !");
			else
			{
				System.out.println("Incorect !");
				if (debug)
				{
					System.out.println("... but debug");
					return;
				}				
				
				File c = new File(wdPath + jar);
				if (!c.exists())
					Files.copy(current.toPath(), c.toPath(), StandardCopyOption.REPLACE_EXISTING);


				{
					if (this.currentOs == Os.WINDOWS)
					{
						JShellLink		link;
						String			filepath;

						try
						{
							//creating icon
							File icon = new File(wdPath + File.separatorChar + "icon.ico");
							if (!icon.exists())
							{
								icon.createNewFile();
								BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(icon)));
								InputStream is = UberStrap.class.getResourceAsStream("/icon.ico");
								BufferedReader br = new BufferedReader(new InputStreamReader(is));
								char[] buff = new char[10];
								
								while (br.read(buff, 0, 10) > 0)
									bw.write(buff, 0, 10);
								bw.close();
								br.close();
							}
							
							System.out.println("Creating shortcut...");
							link = new JShellLink();
							filepath = JShellLink.getDirectory("") + c.getCanonicalPath();
							link.setFolder(JShellLink.getDirectory("desktop"));
							link.setIconLocation(icon.getCanonicalPath());
							link.setName("Ubercube");
							link.setPath(filepath);
							link.save();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				String cmd = "java -jar " + c.getCanonicalPath();
				if (this.currentOs == Os.WINDOWS)
					cmd = cmd + " delete " + current.getCanonicalPath();
				System.out.println("Restarting from corect position... command : (" + cmd + ")");
				Process p = Runtime.getRuntime().exec(cmd);
				if (debug)
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
					while ((jar = in.readLine()) != null)
						System.out.println("\t" + jar);
				}
				ss.close();
				System.exit(0);
			}
		}
	}
}
