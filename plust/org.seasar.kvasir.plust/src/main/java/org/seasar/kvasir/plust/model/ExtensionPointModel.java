/**
 * 
 */
package org.seasar.kvasir.plust.model;

/**
 * @author shidat
 *
 */
public class ExtensionPointModel extends PlustModel
{

    private String id;
    
    private String className;

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
        firePropertyChange("className", className);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
        firePropertyChange("id", id);
    }

    
}
