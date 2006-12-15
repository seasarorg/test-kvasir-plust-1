package ${pluginId}.impl;

import org.seasar.kvasir.base.cache.setting.CachePluginSettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;

import ${pluginId}.${pluginClassName};
import ${pluginId}.setting.${pluginClassName}Settings;


public class ${pluginClassName}Impl extends AbstractPlugin<${pluginClassName}Settings>
    implements ${pluginClassName}
{
    @Override
    public Class<${pluginClassName}Settings> getSettingsClass()
    {
        return ${pluginClassName}Settings.class;
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
