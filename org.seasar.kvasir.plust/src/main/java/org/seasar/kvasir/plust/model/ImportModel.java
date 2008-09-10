/**
 * 
 */
package org.seasar.kvasir.plust.model;

/**
 * @author shidat
 *
 */
public class ImportModel extends PlustModel
{

    private String pluginId;

    private String version;
    
    public String getPluginId()
    {
        return pluginId != null ? pluginId : "";
    }

    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
        firePropertyChange("pluginId", pluginId);
    }

    public String getVersion()
    {
        return version != null ? version : "";
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
    
    
}
