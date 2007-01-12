package ${pluginId}.${pluginShortId}.pop;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.test.PopTestCase;
import ${pluginId}.${pluginClassName};


public class ${pluginShortId?cap_first}PopTest extends PopTestCase<${pluginShortId?cap_first}Pop>
{
    @Override
    protected Class<${pluginShortId?cap_first}Pop> getPopClass()
    {
        return ${pluginShortId?cap_first}Pop.class;
    }


    @Override
    protected String getPopId()
    {
        return ${pluginShortId?cap_first}Pop.ID;
    }


    @Override
    protected String getPluginId()
    {
        return ${pluginClassName}.ID;
    }


    public void testRender()
        throws Exception
    {
        setProperty(PROP_TITLE, "title");
        setProperty(PROP_BODY, "body");

        ${pluginShortId?cap_first}Pop target = newPopInstance();
        RenderedPop actual = target.render(newContext(null, page),
            new String[0]);

        assertEquals("title", actual.getTitle());
        assertEquals("body" actual.getBody()));
    }
}
