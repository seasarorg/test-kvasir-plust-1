package ${pluginId};

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class ${pluginClassName}IT extends KvasirPluginTestCase
{
    protected String getTargetPluginId()
    {
        return ${pluginClassName}.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(${pluginClassName}IT.class);
    }
}
