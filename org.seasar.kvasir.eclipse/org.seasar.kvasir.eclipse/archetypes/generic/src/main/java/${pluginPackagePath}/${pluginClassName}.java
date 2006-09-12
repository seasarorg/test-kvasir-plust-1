package ${pluginId};

import org.seasar.kvasir.base.plugin.Plugin;


public interface ${pluginClassName} extends Plugin
{
    String ID = "${pluginId}";
    String ID_PATH = ID.replace('.', '/');
}
