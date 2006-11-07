package org.seasar.kvasir.eclipse.kvasir.impl;

import java.util.Locale;

import org.seasar.kvasir.eclipse.kvasir.IExtensionPointInfo;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.I18NString;


public class ExtensionPointInfo
    implements IExtensionPointInfo
{
    private String id_;

    private String pluginId_;

    private I18NString description_;

    private BeanAccessor elementClassAccessor_;


    public ExtensionPointInfo(String id, String pluginId,
        I18NString description, BeanAccessor elementClassAccessor)
    {
        id_ = id;
        pluginId_ = pluginId;
        description_ = description;
        elementClassAccessor_ = elementClassAccessor;
    }


    public String toString()
    {
        return "{id=" + id_ + ", pluginId=" + pluginId_
            + ", elementClassAccessor=" + elementClassAccessor_ + "}";
    }


    public String getId()
    {
        return id_;
    }


    public String getDescription(Locale locale)
    {
        if (description_ != null) {
            return description_.getString(locale);
        } else if (elementClassAccessor_ != null) {
            return elementClassAccessor_.getDescription(locale);
        } else {
            return null;
        }
    }


    public String getPluginId()
    {
        return pluginId_;
    }


    public BeanAccessor getElementClassAccessor()
    {
        return elementClassAccessor_;
    }
}
