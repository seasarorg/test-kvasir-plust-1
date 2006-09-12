package org.seasar.kvasir.eclipse.builder;

public class Import
{
    private String plugin_;

    private String version_;


    public Import()
    {
    }


    public Import(String plugin, String version)
    {
        plugin_ = plugin;
        version_ = version;
    }


    public String getPlugin()
    {
        return plugin_;
    }


    public void setPlugin(String plugin)
    {
        plugin_ = plugin;
    }


    public String getVersion()
    {
        return version_;
    }


    public void setVersion(String version)
    {
        version_ = version;
    }
}
