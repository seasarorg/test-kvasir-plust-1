package ${pluginId}.impl;

import org.seasar.kvasir.base.plugin.AbstractPlugin;

import ${pluginId}.${pluginClassName};
import ${pluginId}.setting.${pluginClassName}Settings;


public class ${pluginClassName}Impl extends AbstractPlugin<${pluginClassName}Settings>
    implements ${pluginClassName}
{
    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
