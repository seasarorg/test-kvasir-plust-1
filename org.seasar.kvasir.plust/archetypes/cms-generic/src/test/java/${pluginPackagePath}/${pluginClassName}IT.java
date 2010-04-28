package ${pluginId};

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class ${pluginClassName}IT extends KvasirPluginTestCase<${pluginClassName}>
{
    protected String getTargetPluginId()
    {
        return ${pluginClassName}.ID;
    }


    public static Test suite()
        throws Exception
    {
        // kvasir-webappに直接的にも間接的にも依存していないプラグインの場合は
        // kvasir-webapp関連のクラスがないというエラーが発生することがあります。
        // その場合は第二引数をfalseにして下さい。
        return createTestSuite(${pluginClassName}IT.class, true);
    }
}
