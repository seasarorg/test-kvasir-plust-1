package org.seasar.kvasir.eclipse.builder;

import net.skirnir.freyja.Attribute;
import net.skirnir.freyja.Element;
import net.skirnir.freyja.Macro;
import net.skirnir.freyja.TagEvaluator;
import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.VariableResolver;


public class ReadPluginXmlTagEvaluator
    implements TagEvaluator
{
    public String[] getSpecialTagPatternStrings()
    {
        return new String[] { "import" };
    }


    public String[] getSpecialAttributePatternStrings()
    {
        return null;
    }


    public TemplateContext newContext()
    {
        return new KvasirTemplateContext();
    }


    public String evaluate(TemplateContext context, String name,
        Attribute[] attributes, Element[] body)
    {
        KvasirTemplateContext kvasirContext = (KvasirTemplateContext)context;
        Import inport = new Import();
        for (int i = 0; i < attributes.length; i++) {
            if ("plugin".equals(attributes[i].getName())) {
                inport.setPlugin(attributes[i].getValue());
            } else if ("version".equals(attributes[i].getName())) {
                inport.setVersion(attributes[i].getValue());
            }
        }
        kvasirContext.addImport(inport);

        return null;
    }


    public void gatherMacroVariables(TemplateContext arg0,
        VariableResolver arg1, String arg2, Attribute[] arg3, Element[] arg4)
    {
    }


    public Macro getMacro(TemplateEvaluator arg0, String arg1,
        Attribute[] arg2, Element[] arg3, String arg4)
    {
        return null;
    }


    public Element expandMacroVariables(TemplateContext arg0,
        VariableResolver arg1, String arg2, Attribute[] arg3, Element[] arg4)
    {
        return null;
    }
}
