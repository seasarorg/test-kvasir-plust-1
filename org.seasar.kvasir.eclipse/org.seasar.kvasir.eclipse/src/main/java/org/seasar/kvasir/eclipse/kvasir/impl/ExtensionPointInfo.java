package org.seasar.kvasir.eclipse.kvasir.impl;

import org.seasar.kvasir.eclipse.kvasir.IExtensionPointInfo;

import net.skirnir.xom.BeanAccessor;


public class ExtensionPointInfo
    implements IExtensionPointInfo
{
    private String id_;

    private BeanAccessor elementClassAccessor_;


    public ExtensionPointInfo(String id, BeanAccessor elementClassAccessor)
    {
        id_ = id;
        elementClassAccessor_ = elementClassAccessor;
    }


    public String toString()
    {
        return "{id=" + id_ + ", elementClassAccessor=" + elementClassAccessor_
            + "}";
    }


    public BeanAccessor getElementClassAccessor()
    {
        return elementClassAccessor_;
    }


    public String getId()
    {
        return id_;
    }
}
