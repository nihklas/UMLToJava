package de.umltojava.java.filehandlers;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.ArrayList;

public class TemplateFormatting
{

    private JtwigTemplate template;
    private JtwigModel    model;
    private String        className;
    private String        packageName;
    private String[]      attributes;
    private String[]      methods;

    public TemplateFormatting(String packageName, String className, String[] attributes, String[] methods)
    {
        this.template    = JtwigTemplate.classpathTemplate("de/umltojava/java/templates/class.twig");
        this.model       = JtwigModel.newModel();
        this.className   = className;
        this.packageName = packageName;
        this.attributes  = attributes;
        this.methods     = methods;

        initModel();
    }

    private void initModel()
    {
        this.model.with("package", packageName);
        this.model.with("classname", className);

        ArrayList<String> attributes = getAttributes();
        ArrayList<String> methods    = getMethods();

        this.model.with("attributes", attributes);
        this.model.with("methods", methods);
    }

    public String getFileText()
    {
        return this.template.render(model);
    }

    private ArrayList<String> getMethods()
    {
        ArrayList<String> methodsList = new ArrayList<>();
        if(this.methods != null)
        {
            for(String s : this.methods)
            {
                methodsList.add(getMethod(s));
            }
        }
        return methodsList;
    }

    private String getMethod(String method)
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

        name   = method.substring(1, method.indexOf("("));
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
                return getSetter(identifier, name, params);
            }
        }
        else
        {
            if(!name.equals(className))
            {
                returnType = "void";
            }
        }

        if(!returnType.isEmpty())
            returnType += " ";

        JtwigTemplate methodTemplate = JtwigTemplate.classpathTemplate("de/umltojava/java/templates/methods.twig");
        JtwigModel    methodModel    = JtwigModel.newModel();

        methodModel.with("identifier", identifier);
        methodModel.with("returntype", returnType);
        methodModel.with("methodname", name);
        methodModel.with("parameter", params);
        methodModel.with("return", getReturnStatement(returnType.trim()));
        return methodTemplate.render(methodModel);
    }

    private String getReturnStatement(String returnType)
    {
        if(returnType.isEmpty() || returnType.equalsIgnoreCase("void"))
        {
            return "";
        }

        String returnStatement = "return ";

        if(returnType.equalsIgnoreCase("byte") || returnType.equalsIgnoreCase("short")
           || returnType.equalsIgnoreCase("int") || returnType.equalsIgnoreCase("double")
           || returnType.equalsIgnoreCase("float") || returnType.equalsIgnoreCase("long")
           || returnType.equalsIgnoreCase("char"))
        {
            returnStatement += "0";
        }
        else if(returnType.equalsIgnoreCase("boolean"))
        {
            returnStatement = "false";
        }
        else
        {
            returnStatement = "null";
        }

        return returnStatement + " ;";
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

    private ArrayList<String> getAttributes()
    {
        ArrayList<String> attributesList = new ArrayList<>();
        if(this.attributes != null)
        {
            for(String s : this.attributes)
            {
                attributesList.add(getAttribute(s));
            }
        }
        return attributesList;
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

    private boolean isSetter(String name, String returnType, String params)
    {
        boolean isSetter = false;

        if(params.contains(","))
        {
            return false;
        }

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

        JtwigTemplate methodTemplate = JtwigTemplate.classpathTemplate("de/umltojava/java/templates/getter.twig");
        JtwigModel    methodModel    = JtwigModel.newModel();

        methodModel.with("identifier", identifier);
        methodModel.with("returntype", returnType);
        methodModel.with("methodname", name);
        methodModel.with("attribute", attr);

        return methodTemplate.render(methodModel);
    }

    private String getSetter(String identifier, String name, String params)
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

        JtwigTemplate methodTemplate = JtwigTemplate.classpathTemplate("de/umltojava/java/templates/setter.twig");
        JtwigModel    methodModel    = JtwigModel.newModel();

        methodModel.with("identifier", identifier);
        methodModel.with("methodname", name);
        methodModel.with("parameter", params);
        methodModel.with("attribute", attr);
        methodModel.with("paramvar", params.substring(params.indexOf(" ") + 1));

        return methodTemplate.render(methodModel);
    }

}
