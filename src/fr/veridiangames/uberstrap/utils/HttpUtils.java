package fr.veridiangames.uberstrap.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils
{
    public static BufferedReader getUrlBufferedReader(String url) throws IOException
    {
        URL urlObject = new URL(url);
        return new BufferedReader(new InputStreamReader(urlObject.openStream()));
    }
}
