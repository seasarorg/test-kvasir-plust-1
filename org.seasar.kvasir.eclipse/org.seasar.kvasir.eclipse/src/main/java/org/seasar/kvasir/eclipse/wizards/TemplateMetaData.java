package org.seasar.kvasir.eclipse.wizards;

public class TemplateMetaData
{
    private String id_;

    private String name_;

    private String description_;


    public TemplateMetaData(String id, String name, String description)
    {
        id_ = id;
        name_ = name;
        description_ = description;
    }


    public String getId()
    {
        return id_;
    }


    public String getName()
    {
        return name_;
    }


    public String getDescription()
    {
        return description_;
    }
}
