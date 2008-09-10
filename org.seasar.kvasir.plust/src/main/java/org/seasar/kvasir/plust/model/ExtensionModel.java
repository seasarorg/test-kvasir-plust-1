/**
 *
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.seasar.kvasir.plust.IExtensionPoint;
import org.seasar.kvasir.plust.KvasirProject;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.Element;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


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

    private ExtensionElementModel[] models;


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
        this.models = null;
        firePropertyChange("property", property);
    }


    public ExtensionElementModel[] getRootElements()
        throws ValidationException
    {
        if (models == null) {
            List<ExtensionElementModel> rv = new ArrayList<ExtensionElementModel>();
            try {
                IExtensionPoint extensionPoint = kvasirProject
                    .getExtensionPoint(point);
                if (extensionPoint != null) {
                    for (int i = 0; i < elements.length; i++) {
                        Element element = elements[i];
                        BeanAccessor accessor = extensionPoint
                            .getElementClassAccessor();
                        XOMapper mapper = accessor.getMapper();
                        mapper
                            .setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
                        Object object;
                        try {
                            mapper.setStrict(false);
                            object = mapper.toBean(element, accessor
                                .getBeanClass());
                        } finally {
                            mapper.setStrict(true);
                        }
                        rv.add(new ExtensionElementModel(element.getName(), i,
                            object, accessor, this, true));
                    }
                }
            } catch (ValidationException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
            models = (ExtensionElementModel[])rv
                .toArray(new ExtensionElementModel[rv.size()]);
        }
        return models;
    }


    public ExtensionElementModel addRootElement()
    {
        try {
            IExtensionPoint extensionPoint = kvasirProject
                .getExtensionPoint(point);
            if (extensionPoint != null) {
                BeanAccessor accessor = extensionPoint
                    .getElementClassAccessor();
                Object object = accessor.newInstance();
                XOMapper mapper = accessor.getMapper();
                mapper
                    .setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
                Element element;
                try {
                    mapper.setStrict(false);
                    element = mapper.toElement(object);
                } finally {
                    mapper.setStrict(true);
                }
                List<Element> l = new ArrayList<Element>();
                for (int i = 0; i < elements.length; i++) {
                    l.add(elements[i]);
                }
                l.add(element);
                this.elements = l.toArray(new Element[l.size()]);
                models = null;
                return new ExtensionElementModel(accessor.getBeanName(),
                    elements.length - 1, element, accessor, this, true);
            }
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void removeRootElement(ExtensionElementModel element)
    {
        List<Element> newElements = new ArrayList<Element>();
        for (int i = 0; i < elements.length; i++) {
            if (i != element.getOrder()) {
                newElements.add(elements[i]);
            }
        }
        this.elements = newElements.toArray(new Element[newElements.size()]);
        models = null;
    }


    public String getChildRootName()
    {
        try {
            IExtensionPoint extensionPoint = kvasirProject
                .getExtensionPoint(point);
            if (extensionPoint != null) {
                BeanAccessor elementClassAccessor = extensionPoint
                    .getElementClassAccessor();
                return elementClassAccessor.getBeanName();
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void refresh()
    {
        if (models != null) {
            List<Element> elementList = new ArrayList<Element>();
            try {
                IExtensionPoint extensionPoint = kvasirProject
                    .getExtensionPoint(point);
                if (extensionPoint != null) {
                    BeanAccessor accessor = extensionPoint
                        .getElementClassAccessor();
                    XOMapper mapper = accessor.getMapper();
                    try {
                        mapper.setStrict(false);
                        for (int i = 0; i < models.length; i++) {
                            try {
                                elementList.add(mapper.toElement(models[i]
                                    .getBean()));
                            } catch (ValidationException ex) {
                                // object中の、requiredな値が埋まっていない。
                                MessageDialog.openError(null,
                                    "Required attribute is not set", ex
                                        .getMessage());
                                //    throw new RuntimeException("Can't happen!", ex);
                            }
                        }
                    } finally {
                        mapper.setStrict(true);
                    }
                }
            } catch (CoreException ex) {
                ex.printStackTrace();
            }
            elements = elementList.toArray(new Element[0]);
        }
    }
}
