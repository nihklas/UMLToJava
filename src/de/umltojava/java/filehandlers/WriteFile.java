package de.umltojava.java.filehandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile
{
    private String fileName;
    private String postFix;
    private String path;

    public WriteFile(String fileName, String postFix, String path)
    {
        this.fileName = fileName;
        this.postFix = postFix;

        if(path.lastIndexOf("/") != path.length() - 1)
            path += "/";

        this.path = path;
    }

    public void writeFile(String text) throws IOException
    {
        File file = new File(path + fileName + postFix);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.flush();
        bw.write(text);
        bw.close();
    }
}
