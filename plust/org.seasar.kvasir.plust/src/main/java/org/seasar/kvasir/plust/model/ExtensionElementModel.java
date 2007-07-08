/**
 *
 */
package org.seasar.kvasir.plust.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.MalformedValueException;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;


/**
 * @author shidat
 *
 */
public class ExtensionElementModel
{

    private String name;

    private int order;

    private Object bean;

    private BeanAccessor accessor;

    private ExtensionElementModel parent;

    private ExtensionModel model;

    private boolean root;


    public ExtensionElementModel(String name, int order, Object bean,
        BeanAccessor accessor, ExtensionModel model, boolean root)
    {
        super();
        this.name = name;
        this.order = order;
        this.bean = bean;
        this.accessor = accessor;
        this.model = model;
        this.root = root;
    }


    public int getOrder()
    {
        return order;
    }


    public boolean isRoot()
    {
        return root;
    }


    public ExtensionElementModel getParent()
    {
        return parent;
    }


    public void setParent(ExtensionElementModel parent)
    {
        this.parent = parent;
    }


    public ExtensionElementModel[] getChildren()
    {
        List<ExtensionElementModel> rv = new ArrayList<ExtensionElementModel>();
        try {
            String[] childNames = accessor.getChildNames();
            for (int i = 0; i < childNames.length; i++) {
                String name = childNames[i];
                PropertyDescriptor childDescriptor = accessor
                    .getChildDescriptor(name);
                Object obj = accessor.getChild(bean, name);
                if (childDescriptor.isMultiple()) {
                    Object[] objects = (Object[])obj;
                    for (int j = 0; j < objects.length; j++) {
                        Object object = objects[j];
                        ExtensionElementModel model = new ExtensionElementModel(
                            name, i, object, childDescriptor.getTypeAccessor(),
                            this.model, false);
                        model.setParent(this);
                        rv.add(model);
                    }
                } else {
                    ExtensionElementModel model = new ExtensionElementModel(
                        name, i, obj, childDescriptor.getTypeAccessor(),
                        this.model, false);
                    model.setParent(this);
                    rv.add(model);
                }
            }
        } catch (TargetNotFoundException e) {
            e.printStackTrace();
        }
        return (ExtensionElementModel[])rv.toArray(new ExtensionElementModel[rv
            .size()]);
    }


    public ExtensionElementModel addChild(String name)
    {
        PropertyDescriptor descriptor = accessor.getChildDescriptor(name);
        Object object = descriptor.getTypeAccessor().newInstance();
        try {
            // こんな気遣いは無用だった
            //            if (descriptor.isMultiple()) {
            //                Object[] objects = (Object[])accessor.getChild(bean, name);
            //                if (objects != null) {
            //                    Object[] newArray = new Object[objects.length + 1];
            //                    newArray[objects.length] = object;
            //                    for (int i = 0; i < objects.length; i++) {
            //                        newArray[i] = objects[i];
            //                    }
            //                    accessor.setChild(bean, name, newArray);
            //                    fillAttribute(descriptor, object);
            //                    return object;
            //                } else {
            //                    accessor.setChild(bean, name, new Object[] { object });
            //                    return object;
            //                }
            //            } else {
            // requiredな属性に値を入れるのは難しいので（どんな値を入れればいいか分からないから）、
            // XOMapper#setStrict(false)してrequiredチェックをスキップさせる。（skirnir）
            //            fillAttribute(descriptor, object);
            accessor.setChild(bean, name, object);
            int order;
            if (descriptor.isMultiple()) {
                order = Array.getLength(accessor.getChild(bean, name)) - 1;
            } else {
                order = 0;
            }
            return new ExtensionElementModel(name, order, object, accessor,
                model, false);
            //            }
        } catch (TargetNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedValueException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 必須属性を設定する。
     * FIXME とりあえず空文字を突っ込んでいるが、これでよいかは微妙。
     * Popプラグインだとなぜか怒られる。
     * @param descriptor
     * @param object
     */
    //    private void fillAttribute(PropertyDescriptor descriptor, Object object)
    //    {
    //        BeanAccessor beanAccessor = descriptor.getTypeAccessor();
    //        String[] requiredAttributeNames = beanAccessor
    //            .getRequiredAttributeNames();
    //        for (int i = 0; i < requiredAttributeNames.length; i++) {
    //            String name = requiredAttributeNames[i];
    //            try {
    //                String value = beanAccessor.getAttributeDescriptor(name)
    //                    .getDefault();
    //                if (value == null) {
    //                    value = "";
    //                }
    //                beanAccessor.setAttribute(object, name, value);
    //            } catch (TargetNotFoundException e) {
    //                e.printStackTrace();
    //            } catch (MalformedValueException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    public ExtensionElementModel addChild(String name, Object object)
    {
        PropertyDescriptor descriptor = accessor.getChildDescriptor(name);
        try {
            int order = 0;
            if (descriptor.isMultiple()) {
                Object[] objects = (Object[])accessor.getChild(bean, name);
                if (objects != null) {
                    order = objects.length;
                    Object[] newArray = new Object[objects.length + 1];
                    System.arraycopy(objects, 0, newArray, 0, objects.length);
                    newArray[objects.length] = object;
                    accessor.replaceChildren(bean, name, newArray);
                } else {
                    accessor.replaceChildren(bean, name,
                        new Object[] { object });
                }
            } else {
                accessor.setChild(bean, name, object);
            }
            return new ExtensionElementModel(name, order, object, accessor,
                model, false);
        } catch (TargetNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedValueException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 子要素を削除する。
     * @param name
     * @param child
     */
    public void removeChild(String name, ExtensionElementModel child)
    {
        PropertyDescriptor descriptor = accessor.getChildDescriptor(name);
        try {
            if (descriptor.isMultiple()) {
                Object[] objects = (Object[])accessor.getChild(bean, name);
                if (objects != null) {
                    List<Object> newArray = new ArrayList<Object>();
                    for (int i = 0; i < objects.length; i++) {
                        if (i != child.getOrder()) {
                            newArray.add(objects[i]);
                        }
                    }
                    accessor.replaceChildren(bean, name, (Object[])newArray
                        .toArray(new Object[newArray.size()]));
                } else {
                    accessor.replaceChildren(bean, name, new Object[] {});
                }
            } else {
                accessor.setChild(bean, name, null);
            }
        } catch (TargetNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedValueException e) {
            e.printStackTrace();
        }
    }


    public String[] getChildNames()
    {
        if (accessor != null) {
            List<String> rv = new ArrayList<String>();
            String[] names = accessor.getChildNames();
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                PropertyDescriptor descriptor = accessor
                    .getChildDescriptor(name);
                if (descriptor.isMultiple()) {
                    rv.add(name);
                } else {
                    try {
                        if (accessor.getChild(bean, name) == null) {
                            rv.add(name);
                        }
                    } catch (TargetNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return (String[])rv.toArray(new String[rv.size()]);
        }
        return new String[0];
    }


    public BeanAccessor getAccessor()
    {
        return accessor;
    }


    public void setAccessor(BeanAccessor accessor)
    {
        this.accessor = accessor;
    }


    public Object getBean()
    {
        return bean;
    }


    public void setBean(Object bean)
    {
        this.bean = bean;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public void refresh()
    {
        if (this.model != null) {
            this.model.refresh();
        }
    }


    public ExtensionModel getExtensionModel()
    {
        return model;
    }
}
