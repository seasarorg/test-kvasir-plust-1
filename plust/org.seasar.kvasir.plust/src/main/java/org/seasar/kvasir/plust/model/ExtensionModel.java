/**
 * 
 */
package org.seasar.kvasir.plust.model;

/**
 * @author shidat
 *
 */
public class ExtensionModel extends PlustModel
{

    
    private String point;
    
    //ここ難しそう
    private Object property;

    public String getPoint()
    {
        return point;
    }

    public void setPoint(String point)
    {
        this.point = point;
        firePropertyChange("point", point);
    }

    public Object getProperty()
    {
        return property;
    }

    public void setProperty(Object property)
    {
        this.property = property;
        firePropertyChange("property", property);
    }
    
 
    
}
