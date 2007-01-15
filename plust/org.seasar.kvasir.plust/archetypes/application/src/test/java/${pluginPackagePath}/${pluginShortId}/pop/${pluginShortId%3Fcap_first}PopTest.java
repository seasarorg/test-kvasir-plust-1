package ${pluginId}.${pluginShortId}.pop;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.cms.pop.test.PopTestCase;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.mock.MockPage;

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
        setProperty(PopElement.PROP_TITLE, "title");
        setProperty(PopElement.PROP_BODY, "body");

        Page page = registerPage(new MockPage(1000, "/path/to/page"));

        ${pluginShortId?cap_first}Pop target = newPopInstance();
        RenderedPop actual = target.render(newContext(null, page),
            new String[0]);

        assertEquals("title", actual.getTitle());
        assertEquals("body", actual.getBody());
    }
}
