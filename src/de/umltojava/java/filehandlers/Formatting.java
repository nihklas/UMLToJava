package de.umltojava.java.filehandlers;

public class Formatting
{
    private String   template;
    private String   className;
    private String   packageName;
    private String[] attributes;
    private String[] methods;

    public Formatting(String packageName, String className, String[] attributes, String[] methods)
    {
        this.className   = className;
        this.packageName = packageName;
        this.attributes  = attributes;
        this.methods     = methods;
        initTemplate();
    }

    private void initTemplate()
    {
        String temp = "";
        temp += packageName.isEmpty() ? "" : "package " + packageName + ";" + "\n\n";
        temp += "public class " + className;
        temp += "\n{";
        temp += "\n\n";
        temp += "(attributes)\n";
        temp += "\tpublic " + className + "()";
        temp += "\n";
        temp += "\t{";
        temp += "\n\n";
        temp += "\t}";
        temp += "\n\n";
        temp += "(methods)\n";
        temp += "\n}";

        this.template = temp;
    }

    public String getFileText()
    {
        String fileText = "";

        fileText += template;

        String attributesList = "";
        String methodsList    = "";

        if(this.attributes != null)
        {
            for(String s : this.attributes)
            {
                attributesList += "\t" + getAttribute(s);
                attributesList += ";\n";
            }
        }

        if(this.methods != null)
        {
            for(String s : this.methods)
            {
                methodsList += "\t" + getMethods(s);
                methodsList += "\n\n";
            }
        }

        fileText = fileText.replace("(attributes)", attributesList);
        fileText = fileText.replace("(methods)", methodsList);

        return fileText;
    }

    private String getAttribute(String attribute)
    {
        String identifier;
        String dataType;
        String name;

        switch(attribute.charAt(0))
        {
            case '-':
                identifier = "private";
                break;
            case '+':
                identifier = "public";
                break;
            case '#':
                identifier = "protected";
                break;
            default:
                identifier = "";
        }

        name     = attribute.substring(1, attribute.indexOf(":"));
        dataType = attribute.substring(attribute.indexOf(": ") + 2);

        return identifier + " " + dataType + " " + name;
    }

    private String getMethods(String method)
    {
        String identifier = "";
        String returnType = "";
        String name       = "";
        String params     = "";

        switch(method.charAt(0))
        {
            case '-':
                identifier = "private";
                break;
            case '+':
                identifier = "public";
                break;
            default:
                identifier = "";
        }

        name = method.substring(1, method.indexOf("("));
        params = getParams(method.substring(method.indexOf("(") + 1, method.indexOf(")")));

        if(method.lastIndexOf(":") > method.lastIndexOf(")"))
        {
            returnType = method.substring(method.lastIndexOf(": ") + 2);

            if(isGetter(name, returnType))
            {
                return getGetter(identifier, returnType, name);
            }
            else if(isSetter(name, returnType, params))
            {
                return getSetter(identifier, returnType, name, params);
            }

        }
        else
        {
            if(!name.equals(className))
            {
                returnType = "void";
            }
        }

        String methodText = identifier + " " + returnType + " " + name + "(" + params + ")";
        methodText = methodText.replaceAll(" {2,}", " ");
        methodText += "\n\t{";
        methodText += "\n";

        if(!returnType.equals("void") && !returnType.isEmpty())
        {
            methodText += "\n\n\t\t";
            if(returnType.equals("int") || returnType.equals("double"))
            {
                methodText += "return 0;";
            }
            else
            {
                methodText += "return null;";
            }
        }

        methodText += "\n\t}";

        return methodText;
    }

    private String getSetter(String identifier, String returnType, String name, String params)
    {
        String attr = "";

        for(String attribute : this.attributes)
        {
            String att     = getAttribute(attribute);
            String attName = att.substring(att.lastIndexOf(" ") + 1);
            if(name.equalsIgnoreCase("set" + attName))
            {
                attr = attName;
                break;
            }
        }

        String methodText = identifier + " " + returnType + " " + name + "(" + params + ")";
        methodText = methodText.replaceAll(" {2,}", " ");
        methodText += "\n\t{";
        methodText += "\n\t\t";
        methodText += "this." + attr + " = " + params.substring(params.indexOf(" ") + 1) + ";";
        methodText += "\n\t}";

        return methodText;
    }

    private boolean isSetter(String name, String returnType, String params)
    {
        boolean isSetter = false;

        if(params.contains(","))
            return false;

        for(String attr : this.attributes)
        {
            String att     = getAttribute(attr);
            String attName = att.substring(att.lastIndexOf(" ") + 1);
            String attType = att.substring(att.indexOf(" ") + 1, att.lastIndexOf(" "));

            if(name.equalsIgnoreCase("set" + attName))
            {
                if(returnType.equalsIgnoreCase("void"))
                {
                    if(params.substring(0, params.indexOf(" ")).equalsIgnoreCase(attType))
                    {
                        isSetter = true;
                    }
                }
            }
        }
        return isSetter;
    }

    private String getGetter(String identifier, String returnType, String name)
    {
        String attr = "";

        for(String attribute : this.attributes)
        {
            String att     = getAttribute(attribute);
            String attName = att.substring(att.lastIndexOf(" ") + 1);
            if(name.equalsIgnoreCase("get" + attName))
            {
                attr = attName;
                break;
            }
        }

        String methodText = identifier + " " + returnType + " " + name + "()";
        methodText = methodText.replaceAll(" {2,}", " ");
        methodText += "\n\t{";
        methodText += "\n\t\t";
        methodText += "return this." + attr + ";";
        methodText += "\n\t}";

        return methodText;
    }

    private boolean isGetter(String name, String returnType)
    {
        boolean isGetter = false;
        for(String attr : this.attributes)
        {
            String att     = getAttribute(attr);
            String attName = att.substring(att.lastIndexOf(" ") + 1);
            String attType = att.substring(att.indexOf(" ") + 1, att.lastIndexOf(" "));

            if(name.equalsIgnoreCase("get" + attName))
            {
                if(returnType.equalsIgnoreCase(attType))
                {
                    isGetter = true;
                }
            }
        }
        return isGetter;
    }

    private String getParams(String substring)
    {
        String params = "";
        if(substring.equals(""))
        {
            return params;
        }
        for(String s : substring.split(","))
        {
            s = s.replace(" ", "");
            params += s.substring(s.indexOf(":") + 1);
            params += " ";
            params += s.substring(0, s.indexOf(":"));
            params += ", ";
        }
        params = params.substring(0, params.length() - 2);
        return params;
    }
}
