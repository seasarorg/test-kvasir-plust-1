package ${pluginId};

import org.seasar.kvasir.base.plugin.Plugin;

import ${pluginId}.setting.${pluginClassName}Settings;


public interface ${pluginClassName} extends Plugin<${pluginClassName}Settings>
{
    String ID = "${pluginId}";
    String ID_PATH = ID.replace('.', '/');
}
