package org.seasar.kvasir.eclipse;

import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.util.collection.DelegatingI18NPropertyHandler;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;

import net.skirnir.xom.XOMapper;


public class PluginInfo
{
    private XOMapper mapper_;

    private PluginDescriptor descriptor_;

    private I18NPropertyHandler properties_;


    public PluginInfo(XOMapper mapper, PluginDescriptor descriptor,
        I18NPropertyHandler properties)
    {
        mapper_ = mapper;
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


    public void merge(PluginInfo parentInfo)
    {
        descriptor_ = (PluginDescriptor)mapper_.merge(parentInfo
            .getDescriptor(), descriptor_);
        properties_ = new DelegatingI18NPropertyHandler(properties_, parentInfo
            .getProperties());
    }
}
