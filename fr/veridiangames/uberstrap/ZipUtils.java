package fr.veridiangames.uberstrap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils
{
	public static boolean unzip(String zipFilePath, String destDirectory) {
        try
        {
            File destDir = new File(destDirectory);
	        if (!destDir.exists())
	            destDir.mkdir();
	        ZipInputStream zipIn;
	        zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	        ZipEntry entry = zipIn.getNextEntry();
	        while (entry != null)
	        {
	            String filePath = destDirectory + File.separator + entry.getName();
	            if (!entry.isDirectory())
	                extractFile(zipIn, filePath);
	            else
	            {
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
	        return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException
    {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1)
            bos.write(bytesIn, 0, read);
        bos.close();
    }
}
