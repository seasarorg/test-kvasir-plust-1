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

    public String getPluginId()
    {
        return pluginId;
    }

    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
        firePropertyChange("pluginId", pluginId);
    }
    
    
}
