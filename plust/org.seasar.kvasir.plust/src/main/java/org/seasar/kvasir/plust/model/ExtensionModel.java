/**
 * 
 */
package org.seasar.kvasir.plust.model;

import net.skirnir.xom.Element;

/**
 * @author shidat
 *
 */
public class ExtensionModel extends PlustModel
{

    
    private String point;
    
    //ここ難しそう
    private Element[] elements;

    public String getPoint()
    {
        return point != null ? point : "";
    }

    public void setPoint(String point)
    {
        this.point = point;
        firePropertyChange("point", point);
    }

    public Element[] getProperty()
    {
        return elements;
    }

    public void setProperty(Element[] property)
    {
        this.elements = property;
        firePropertyChange("property", property);
    }
    
 
    
}
