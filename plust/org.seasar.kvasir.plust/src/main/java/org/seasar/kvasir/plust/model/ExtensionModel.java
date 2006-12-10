/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.List;

import net.skirnir.xom.Attribute;
import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.Element;

/**
 * @author shidat
 *
 */
public class ExtensionModel extends PlustModel
{

    
    private String point;

    /*
     * メモ:
     * ExtensionPointでは拡張の定義はJavaクラスでなされている。
     * しかし、XOMを使ってExtensionを読み込むと拡張はJavaオブジェクトではなく
     * XOMElementになっている。
     * 必須入力項目は簡単にXOMを使ってマッピングをとることができるが
     * オプショナルはBeanAccessorを使わないと難しい。
     * BeanAccessorを使って、Elementを作るという多少二度手間な感じのする
     * 作成方法を行う必要があり、ちょっとげんなり。
     */
    private Element[] elements;

    private BeanAccessor accessor;

    private Element rootElement;
    
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
        this.rootElement = property[0];
        firePropertyChange("property", property);
    }

    public BeanAccessor getAccessor()
    {
        return accessor;
    }

    public void setAccessor(BeanAccessor accessor)
    {
        this.accessor = accessor;
    }
    
    void initialize() {
        String[] names = accessor.getAttributeNames();
        List attrs = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Attribute attribute = rootElement.getAttribute(name);
            if (attribute == null) {
                attribute = new Attribute(name, "", "");
            }
            attrs.add(attribute);
        }
//        rootElement.setAttributes(attributes)
    }
    
}
