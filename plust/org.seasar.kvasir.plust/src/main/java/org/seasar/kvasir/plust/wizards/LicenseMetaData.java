package org.seasar.kvasir.plust.wizards;

public class LicenseMetaData
{
    private String fileName_;

    private String name_;


    public LicenseMetaData(String fileName, String name)
    {
        fileName_ = fileName;
        name_ = name;
    }


    public String getFileName()
    {
        return fileName_;
    }


    public String getName()
    {
        return name_;
    }
}
