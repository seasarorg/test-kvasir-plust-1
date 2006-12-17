/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.plust.IExtensionPoint;
import org.seasar.kvasir.plust.KvasirProject;

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

    private KvasirProject kvasirProject;

    private ExtensionElementModel model;


    public KvasirProject getKvasirProject()
    {
        return kvasirProject;
    }


    public void setKvasirProject(KvasirProject kvasirProject)
    {
        this.kvasirProject = kvasirProject;
    }


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


    public ExtensionElementModel[] getRootElements()
    {
        List rv = new ArrayList();
        try {
            IExtensionPoint extensionPoint = kvasirProject
                .getExtensionPoint(point);
            if (extensionPoint != null) {
                for (int i = 0; i < elements.length; i++) {
                    Element element = elements[i];
                    BeanAccessor accessor = extensionPoint
                        .getElementClassAccessor();
                    Object object = accessor.getMapper().toBean(element,
                        accessor.getBeanClass());
                    model = new ExtensionElementModel(element
                                            .getName(), object, accessor, this);
                    rv.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ExtensionElementModel[])rv.toArray(new ExtensionElementModel[rv
            .size()]);
    }
    
    public void refresh() {
        if (model != null) {
            Element element = model.getAccessor().getMapper().toElement(model.getBean());
            this.elements[0] = element;
        }
    }
}
