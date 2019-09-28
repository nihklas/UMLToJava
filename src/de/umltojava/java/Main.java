package de.umltojava.java;

import de.umltojava.java.filehandlers.DocumentHandler;
import de.umltojava.java.filehandlers.Formatting;
import de.umltojava.java.filehandlers.ReadFile;
import de.umltojava.java.filehandlers.WriteFile;

import java.io.IOException;
import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        if(args.length < 2)
        {
            System.out.println("Wrong amount of arguments. Needed: input file path, output directory path(, package name)");
            return;
        }
        try
        {
            String regexToDelete = "<ClassNode.*</textColor>";
            String fileText = ReadFile.readFile(args[0]);
            String path = args[1];

            fileText = fileText.substring(fileText.indexOf("<ClassNode id=\"3\">"));
            fileText = fileText.substring(0, fileText.indexOf("</ClassDiagramGraph>]]"));
            fileText = fileText.replaceAll(" {2,}", " ");
            String[] fileParts = fileText.split("</ClassNode>");
            String[] classes   = Arrays.copyOf(fileParts, fileParts.length - 1);

            DocumentHandler documentHandler;
            WriteFile       writeFile;
            Formatting      formatting;
            String          packageName = args.length == 3 ? args[2] : "";

            System.out.println("Start writing " + classes.length + " class files!");

            for(String s : classes)
            {
                s = s.replaceAll(regexToDelete, "");
                documentHandler = new DocumentHandler(s);
                System.out.println("Writing class: " + documentHandler.getClassName() + "...");
                formatting = new Formatting(packageName, documentHandler.getClassName(), documentHandler.getAttributes(), documentHandler.getMethods());
                writeFile = new WriteFile(documentHandler.getClassName(), ".java", path);
                writeFile.writeFile(formatting.getFileText());
                System.out.println("Finished Writing class: " + documentHandler.getClassName() + "!");
                System.out.println("---------------------------------------------------------");
            }


        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
