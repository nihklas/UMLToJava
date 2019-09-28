package de.umltojava.java.filehandlers;

import java.io.*;

public class ReadFile
{
    private ReadFile()
    {
    }

    public static String readFile(String fileName) throws IOException
    {
        String         fileText = "";
        File           file     = new File(fileName);
        BufferedReader br       = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null)
        {
            fileText += st;
        }

        return fileText;
    }
}
