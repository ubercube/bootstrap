package fr.veridiangames.uberstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JProgressBar;

public class DownloadUtils
{	
	public static void downloadMAJ(final String adresse, final File dest, final JProgressBar bar)
	{
		bar.setIndeterminate(true);

		Thread n = new Thread()
		{
			@Override
			public void run()
			{
				FileOutputStream fos = null;
				BufferedReader reader = null;

				try
				{
					// Création de la connexion
					URL url = new URL(adresse);

					URLConnection conn = url.openConnection();
					System.out.println(adresse);

					String FileType = conn.getContentType();
					System.out.println("FileType : " + FileType);

					int fileLength = conn.getContentLength();

					if(fileLength == -1)
						throw new IOException("Fichier non valide.");
					else
						bar.setMaximum(fileLength);

					// Lecture de la réponse

					InputStream in = conn.getInputStream();
					reader = new BufferedReader(new InputStreamReader(in));

					if(dest == null)
					{
						String fileName = url.getFile();
						fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
						fos = new FileOutputStream(new File(fileName));
					}
					else
						fos = new FileOutputStream(dest);

					byte[] buff = new byte[1024];

					bar.setValue(0);
					bar.setIndeterminate(false);

					int n;
					while((n = in.read(buff)) != -1)
					{
						fos.write(buff, 0, n);
						bar.setValue(bar.getValue() + n);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						fos.flush();
						fos.close();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}

					try
					{
						reader.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		n.start();
		try
		{
			n.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
