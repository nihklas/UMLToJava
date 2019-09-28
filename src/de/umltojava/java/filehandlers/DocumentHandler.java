package de.umltojava.java.filehandlers;

public class DocumentHandler
{
    private String document;

    public DocumentHandler(String document)
    {
        this.document = document;
    }

    public String getClassName()
    {
        String className = document.substring(document.indexOf("<name"), document.indexOf("</name>"));
        className = className.substring(className.indexOf("<text>"), className.indexOf("</text>"));
        className = className.substring(className.indexOf(">") + 1);
        return className;
    }

    public String[] getAttributes()
    {
        String[] attributes;
        String attributeList = document.substring(document.indexOf("<attributes"), document.indexOf("</attributes>"));
        attributeList = attributeList.substring(attributeList.indexOf("<text>"), attributeList.indexOf("</text>"));
        attributeList = attributeList.substring(attributeList.indexOf(">") + 1);

        attributeList = attributeList.replace("-", "\n-");
        attributeList = attributeList.replace("#", "\n#");
        attributeList = attributeList.replace("+", "\n+").replaceFirst("\n", "");
        attributes = attributeList.split("\n");

        if(attributes.length == 1)
        {
            if(attributes[0].isEmpty())
            {
                return null;
            }
        }

        return attributes;
    }

    public String[] getMethods()
    {
        String[] methods;

        String methodList = document.substring(document.indexOf("<methods"), document.indexOf("</methods>"));
        methodList = methodList.substring(methodList.indexOf("<text>"), methodList.indexOf("</text>"));
        methodList = methodList.substring(methodList.indexOf(">") + 1);

        methodList = methodList.replace("-", "\n-");
        methodList = methodList.replace("+", "\n+").replaceFirst("\n", "");
        methods = methodList.split("\n");

        if(methods.length == 1)
        {
            if(methods[0].isEmpty())
            {
                return null;
            }
        }

        return methods;
    }
}
