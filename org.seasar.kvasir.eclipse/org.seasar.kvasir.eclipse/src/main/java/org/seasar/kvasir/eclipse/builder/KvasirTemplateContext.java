package org.seasar.kvasir.eclipse.builder;

import java.util.ArrayList;
import java.util.List;

import net.skirnir.freyja.impl.TemplateContextImpl;


public class KvasirTemplateContext extends TemplateContextImpl
{
    private List importsList_ = new ArrayList();


    public Import[] getImports()
    {
        return (Import[])importsList_.toArray(new Import[0]);
    }


    public void addImport(Import inport)
    {
        importsList_.add(inport);
    }
}
