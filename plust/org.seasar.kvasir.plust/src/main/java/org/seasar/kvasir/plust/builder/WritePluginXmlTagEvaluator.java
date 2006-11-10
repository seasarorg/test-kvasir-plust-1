package org.seasar.kvasir.plust.builder;

import net.skirnir.freyja.Attribute;
import net.skirnir.freyja.Element;
import net.skirnir.freyja.Macro;
import net.skirnir.freyja.TagEvaluator;
import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.VariableResolver;


public class WritePluginXmlTagEvaluator
    implements TagEvaluator
{
    public String[] getSpecialTagPatternStrings()
    {
        return null;
    }


    public String[] getSpecialAttributePatternStrings()
    {
        return null;
    }


    public TemplateContext newContext()
    {
        return null;
    }


    public String evaluate(TemplateContext arg0, String arg1, Attribute[] arg2,
        Element[] arg3)
    {
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
