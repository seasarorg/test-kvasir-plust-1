package org.seasar.kvasir.plust;

import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.util.collection.DelegatingI18NPropertyHandler;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;

import net.skirnir.xom.XOMapper;


public class Plugin
    implements IPlugin
{
    private XOMapper mapper_;

    private PluginDescriptor descriptor_;

    private I18NPropertyHandler properties_;


    public Plugin(XOMapper mapper, PluginDescriptor descriptor,
        I18NPropertyHandler properties)
    {
        mapper_ = mapper;
        descriptor_ = descriptor;
        properties_ = properties;
    }


    public String getId()
    {
        if (descriptor_ != null) {
            return descriptor_.getId();
        } else {
            return null;
        }
    }


    public PluginDescriptor getDescriptor()
    {
        return descriptor_;
    }


    public I18NPropertyHandler getProperties()
    {
        return properties_;
    }


    public void merge(Plugin parentInfo)
    {
        descriptor_ = (PluginDescriptor)mapper_.merge(parentInfo
            .getDescriptor(), descriptor_);
        properties_ = new DelegatingI18NPropertyHandler(properties_, parentInfo
            .getProperties());
    }


    public String getVersion()
    {
        if (descriptor_ != null) {
            return descriptor_.getVersionString();
        } else {
            return null;
        }
    }
}
