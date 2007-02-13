/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.seasar.kvasir.plust.IExtensionPoint;
import org.seasar.kvasir.plust.KvasirProject;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.Element;
import net.skirnir.xom.MalformedValueException;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;
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
        throws ValidationException
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
                    accessor.getMapper().setBeanAccessorFactory(
                        new AnnotationBeanAccessorFactory());
                    Object object = accessor.getMapper().toBean(element,
                        accessor.getBeanClass());
                    model = new ExtensionElementModel(element.getName(),
                        object, accessor, this);
                    rv.add(model);
                }
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (ExtensionElementModel[])rv.toArray(new ExtensionElementModel[rv
            .size()]);
    }

    public Element addRootElement() 
    {
        try {
            IExtensionPoint extensionPoint = kvasirProject.getExtensionPoint(point);
            if (extensionPoint != null) {
                BeanAccessor accessor = extensionPoint.getElementClassAccessor();
                Object object = accessor.newInstance();
                String[] names = accessor.getAttributeNames();
                for (int i = 0; i < names.length; i++) {
                    String string = names[i];
                    PropertyDescriptor descriptor = accessor.getAttributeDescriptor(string);
                    if (descriptor.isRequired()) {
                        accessor.setAttribute(object, string, string);
                    }
                }
                XOMapper mapper = accessor.getMapper();
                mapper.setBeanAccessorFactory(new AnnotationBeanAccessorFactory());
                Element element = mapper.toElement(object);
                List l = new ArrayList();
                for (int i = 0; i < elements.length; i++) {
                    l.add(elements[i]);
                }
                l.add(element);
                this.elements = (Element[])l.toArray(new Element[l.size()]);
                return element;
            }
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (TargetNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedValueException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeRootElement(Element element)
    {
        List newElements = new ArrayList();
        for (int i = 0; i < elements.length; i++) {
            Element e = elements[i];
            if (!e.equals(element))
            {
                newElements.add(e);
            }
        }
        this.elements = (Element[])newElements.toArray(new Element[newElements.size()]);
    }
    
    public String getChildRootName()
    {
        try {
            IExtensionPoint extensionPoint = kvasirProject.getExtensionPoint(point);
            if (extensionPoint != null) {
                BeanAccessor elementClassAccessor = extensionPoint.getElementClassAccessor();
                return elementClassAccessor.getBeanName();
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void refresh()
    {
        if (model != null) {
            BeanAccessor accessor = model.getAccessor();
            XOMapper mapper = accessor.getMapper();
            Element element;
            try {
                element = mapper.toElement(model.getBean());
                this.elements[0] = element;
            } catch (ValidationException ex) {
                // object中の、requiredな値が埋まっていない。
                MessageDialog.openError(null, "Required attribute is not set",
                    ex.getMessage());
                //    throw new RuntimeException("Can't happen!", ex);
            }
        }
    }
}
