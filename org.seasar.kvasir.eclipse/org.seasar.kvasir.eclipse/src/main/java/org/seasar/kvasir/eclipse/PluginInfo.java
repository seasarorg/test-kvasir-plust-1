package org.seasar.kvasir.eclipse;

import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;


public class PluginInfo
{
    private PluginDescriptor descriptor_;

    private I18NPropertyHandler properties_;


    public PluginInfo(PluginDescriptor descriptor, I18NPropertyHandler properties)
    {
        descriptor_ = descriptor;
        properties_ = properties;
    }


    public PluginDescriptor getDescriptor()
    {
        return descriptor_;
    }


    public I18NPropertyHandler getProperties()
    {
        return properties_;
    }
}
